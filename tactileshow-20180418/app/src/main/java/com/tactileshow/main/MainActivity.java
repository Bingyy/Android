package com.tactileshow.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tactileshow.util.DataFile;
import com.tactileshow.util.Point3D;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
 * 主界面的Activity。
 * 整个连接执行过程为：onMenuItemSelected里的macro.MENU_ITEMID_FRESH情况（当点击刷新时，进行设备扫描），scanLeDevice（真正开始扫描）
 * lv_device.setOnItemClickListener（当点击设备时，进行连接）
 */
public class MainActivity extends Activity
{
	BluetoothManager mBluetoothManager; //
	
	BluetoothAdapter mBluetoothAdapter; // 蓝牙交互入口点
	
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>(); // 存放搜到的设备

	private List<BluetoothGattService> mLeServices;
	
	private boolean mIsScanning;
	
	private Handler mHandler = new Handler();
	
	private MyHandler myHandler;
	
	private BluetoothGatt mBluetoothGatt;
	
	private BluetoothGattService mGattService; // 通过服务拿到特征
	
	TextView tv_hello;
	
	ListView lv_device;
	
	TextView tv_connect_info;
	
	Menu me_globle;
	
	MenuItem mi_fresh;
	
	MenuItem mi_exit;
	
	MenuItem mi_debug;
	
	AlertDialog.Builder builder_dl_connect;
	
	AlertDialog dl_connect;
	
	ArrayAdapter<String> lvaa_device;
	
	List<String> mLeDevices_lvdata = new ArrayList<String>();
	
	private final static String TAG = "测试TAG";
	
	private int mConnectionState = STATE_DISCONNECTED;
	
	private static final int STATE_DISCONNECTED = 0; //设备无法连接
	
	private static final int STATE_CONNECTING = 1;  //设备正在连接状态
	
	private static final int STATE_CONNECTED = 2;   //设备连接完毕

	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

	private static final String DEVICE_NAME = "ZJU-Wgmtest";

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		verifyStoragePermissions(this); // 动态申请定位权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
		}

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_ble);

		Log.e("wshg", "Starting...");
		ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.show();

		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, macro.INTENT_REQUEST_ENABLE_BT);
		}
		
		Toast.makeText(getApplicationContext(), "Bluetooth is opened", Toast.LENGTH_SHORT).show();
		
		tv_hello = (TextView) findViewById(R.id.layout_ble_hello);

		// 显示组件
		lv_device = (ListView) findViewById(R.id.lv_ble_device);

		// 直接用ArrayAdapter做设备显示填充Adapter
		lvaa_device = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mLeDevices_lvdata);

		// 初始化设备列表
		UpdateDeviceList();
		
		lv_device.setAdapter(lvaa_device);

		// 查找到的设备点击事件响应，点击查找到的item才会进行连接
		lv_device.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				BluetoothDevice tmpBleDevice = mLeDevices.get(arg2); // 拿到设备对象
				// Log.w(TAG,"拿到的对象名字是" + arg2); // arg2是在ListView中的序号，匹配mLeDevices中的对象序号

				Toast.makeText(getApplicationContext(), "Connecting" + tmpBleDevice.getName(), Toast.LENGTH_SHORT).show();

				// 扫描设备,Q: 这里干嘛还要再扫描？显然是不用写在这里的

				scanLeDevice(false);
				mBluetoothGatt = tmpBleDevice.connectGatt(MainActivity.this, true, mGattCallback);
				// mBluetoothGatt = tmpBleDevice.connectGatt(MainActivity.this, true, mGattCallback); // 开启自动连接，看起来并没用啊
				ShowConnectDialog();
			}
		});
		
		
		myHandler = new MyHandler(); //用于扫描时间设定
		
		StaticValue.data_file = new DataFile();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(macro.BROADCAST_ADDRESS);
		registerReceiver(mGattUpdateReceiver, filter);
		
		tv_hello.setText("Click Search to Scan");


		// 此时设备已经存在mLeDevices中了
		for (int i = 0; i < mLeDevices.size(); i++) {
			BluetoothDevice device  = mLeDevices.get(i); // 拿到第i个设备
			if (device.getName() == "ZJU-Wgmtest") {
				mBluetoothGatt = device.connectGatt(MainActivity.this, false, mGattCallback);
				// mBluetoothGatt = tmpBleDevice.connectGatt(MainActivity.this, true, mGattCallback); // 开启自动连接，看起来并没用啊
				ShowConnectDialog();
			}
		}
	}

	// 处理自动重连设备 -- 新增
	public void connectGatt(BluetoothDevice device) {
		if (device != null) {
			if (mBluetoothGatt != null) {
				mBluetoothGatt.close();
				mBluetoothGatt.disconnect();
				mBluetoothGatt = null;
			}
			mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		}
		else {
			Log.e(TAG, "The bluetooth device is null, please reset the bluetooth device");
		}
	}

	// 动态申请权限:不加这个无法读写文件
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE"
	};

	public static void verifyStoragePermissions(Activity activity) {
		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_COARSE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission granted, yay! Start the Bluetooth device scan.
				} else {
					// Alert the user that this application requires the location permission to perform the scan.
				}
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
		closeBle();
	}

	//	创建菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// 参数设定是自己定义的数值，根据此数值匹配到菜单项
		me_globle = menu;
		mi_debug = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_DEBUG, 0, "Test");
		mi_fresh = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_FRESH, 1, "Search");
		mi_exit = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_EXIT, 2, "Exit");

		mi_fresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi_exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	// 添加菜单点击事件
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		// 点击搜索响应
		if (item.getItemId() == macro.MENU_ITEMID_FRESH)
		{
			tv_hello.setText("");
			Toast.makeText(MainActivity.this, "SEARCHING",Toast.LENGTH_LONG).show();

			closeBle();
			mLeDevices.clear();
			scanLeDevice(true);
			UpdateDeviceList(); // 点击搜索时开启更新设备列表

			// tv_hello.setText("开始搜索");

		}
		else if (item.getItemId() == macro.MENU_ITEMID_EXIT)
		{
			Log.w(TAG, "Exit");
			finish();
		}
		else if (item.getItemId() == macro.MENU_ITEMID_DEBUG)
		{
			Log.w(TAG, "Test Mode");
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MainTabActivity.class);
			intent.putExtra("str", "come from first activity TEST");
			startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
		}
		
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	void ShowConnectDialog()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.activity_ble_connect, null);
		
		builder_dl_connect = new AlertDialog.Builder(this);
		builder_dl_connect.setTitle("Connecting Status");
		builder_dl_connect.setView(view);
		builder_dl_connect.setNegativeButton("Canceling Connecting",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						closeBle();
						dialog.dismiss();
					}
				});
		tv_connect_info = (TextView) view.findViewById(R.id.tv_ble_connect_info);
		if (tv_connect_info == null)
			Log.w(TAG, "NULL");
		tv_connect_info.setText("Connecting...");
		
		dl_connect = builder_dl_connect.show();
	}
	
	// 关闭Gatt
	public void closeBle()
	{
		if (mBluetoothGatt == null)
		{
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}
	
	void beginScanUI()
	{
		//mi_fresh.setEnabled(false);
		MenuItemCompat.setActionView(mi_fresh, R.layout.activity_ble_progressbar);
	}
	
	void endScanUI()
	{
		//mi_fresh.setEnabled(true);
		MenuItemCompat.setActionView(mi_fresh, null);
	}

	// 更新查找到的设备
	void UpdateDeviceList()
	{
		int count = 0;
		Log.w(TAG, "更新数据");
		mLeDevices_lvdata.clear();

		if (mLeDevices.size() == 0)
		{
			mLeDevices_lvdata.add("No BLE device founded yet");
			lv_device.setEnabled(false);
		}
		else
		{
			lv_device.setEnabled(true);
			Iterator<BluetoothDevice> it = mLeDevices.iterator();
			while (it.hasNext())
			{
				count++;
				BluetoothDevice bd_it = it.next();
				mLeDevices_lvdata.add(bd_it.getName() + " " + bd_it.getAddress());
				Log.w(TAG, "The device number is: " + count);
			}
		}
		lvaa_device.notifyDataSetChanged();
	}
	
	// 扫描蓝牙设备
	private void scanLeDevice(final boolean enable)
	{
		if (enable)
		{
			// 经过预定扫描期后停止扫描
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if (mIsScanning == false)
						return; //已经终止扫描
					mIsScanning = false;
					endScanUI();
					// mBluetoothAdapter.stopLeScan(mLeScanCallback);// getBluetoothLeScanner().stopScan(scanCallback)
					mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
					Log.i(TAG, "Scanning Done!");
					SendHandleMsg("Scan", macro.HANDLER_SCAN_STOPPED);
				}
			}, macro.BLE_SCAN_PERIOD);
			
			mIsScanning = true;
			beginScanUI();
			// mBluetoothAdapter.startLeScan(mLeScanCallback); // 蓝牙适配器开始进行扫描设备，回调mLeScanCallBack变量, getBluetoothLeScanner().startScan(scanCallback);
			mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
		}
		else
		{
			mIsScanning = false;
			endScanUI();
			Log.i(TAG, "Scanning Done!");
			// mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
		}
	}
	
	void SendHandleMsg(String tag, int content)
	{
		Message msg = new Message();
		msg.what = content;
		myHandler.sendMessage(msg);
	}

	// 新增的用于6.0后的API回调
	private ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanResult(final int callbackType, final ScanResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i("callbackType", String.valueOf(callbackType));
					Log.i("result", result.toString());
					final BluetoothDevice device = result.getDevice(); // 拿到设备
					int rssi = result.getRssi(); // 拿到信号强度,只是拿着玩，逻辑内暂时没用到

					// 对加入的设备去重
					// 或可以根据device.getAddress()来判定是否已经加入
					if (!mLeDevices.contains(device)) {
						// 再加一个过滤掉名字是null的设备的条件
						String name = device.getName();
						if (name != null) {
							mLeDevices.add(device);
						}
					}


					Log.d(TAG, mLeDevices.toString());

					UpdateDeviceList();

					String out_info = device.getAddress() + " " + device.getBondState() + " " + device.getName();
				}
			});
		}
	};

	// 这种写法过时了，在API21以下用这个方式
	// Observer, 中心设备，接收广播数据，扫描设备
	// 创建LeScanCallBack，接收广播，回调上报数据
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback()
			{
			    // device表示扫描到的设备， rssi：扫描到的设备信号强度，scanRecord: 广播数据，62字节
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							mLeDevices.add(device);
							Log.d(TAG, mLeDevices.toString());
							UpdateDeviceList();
							Toast.makeText(getApplicationContext(), "New BLE device scanned " + device.getName(), Toast.LENGTH_SHORT).show();
							String out_info = device.getAddress() + " " + device.getBondState() + " " + device.getName();
							tv_hello.setText(tv_hello.getText() + "\n" + out_info);
							
						}
					});
				}
			};
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{
		// 连接状态改变时回调
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
		{
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED)
			{
				intentAction = macro.ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				
				SendHandleMsg("Device", macro.HANDLER_CONNECT_SUCCESS);
				Log.i(TAG, "Connected to GATT server.");
				mBluetoothGatt.discoverServices(); //先去发现服务
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED)
			{
				//当设备无法连接
				intentAction = macro.ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				SendHandleMsg("Device", macro.HANDLER_CONNECT_FAILED);
				Log.i(TAG, "Disconnected from GATT server.");
				
			}
		}
		
		@Override
		// 发现新服务端的回调
		public void onServicesDiscovered(BluetoothGatt gatt, int status)
		{
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				SendHandleMsg("service", macro.HANDLER_SERVICE_DISCOVERED);
			}
			else
			{
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}
		
		@Override
		// 读写Characteristic
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
		{
			Log.w("TAG", "READ!!");
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		// 数据返回的回调，接收BLE设备返回数据
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
		{
			// TODO Auto-generated method stub
			super.onCharacteristicChanged(gatt, characteristic);
			Log.w(TAG, "改变");
			Time t = new Time();
			t.setToNow();
			String str_time = t.format2445();
			
			String uuid = characteristic.getUuid().toString();
			
			if (uuid.equals(macro.UUID_HUM_DAT))
			{
				Point3D p3d_hum = convertHum(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				broadcastUpdate("#" + "PRESS" + "#" + str_time + "#" + p3d_hum.x);
				
				Point3D p3d_temp = convertTemp(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				
				broadcastUpdate("#" + "TEMP" + "#" + str_time + "#" + p3d_temp.x);
			}
			/*
			if(uuid.equals(macro.UUID_MAG_DAT))
			{
				Point3D p3d = convertMag(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				
				broadcastUpdate("#" + "TEMP" + "#" + str_time + "#" + p3d.x);
			}
			*/
			
			
		}
		
	};
	
	
	void broadcastUpdate(String str_intent)
	{
		
		Intent intent = new Intent(macro.BROADCAST_ADDRESS);
		intent.putExtra("msg", str_intent);
		sendBroadcast(intent);
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		
		@Override
		public void onReceive(Context arg0, Intent arg1)
		{
			// TODO Auto-generated method stub
			final String action = arg1.getStringExtra("msg");
			Log.w(TAG, "广播来了 " + action);
			tv_hello.setText(action);
		}
		
	};
	
	String ListServices()
	{
		String ser_str = "";

		if (mBluetoothGatt != null)
		{
			mLeServices = mBluetoothGatt.getServices();
			for (int i = 0; i < mLeServices.size(); i++)
			{
				Log.w(TAG, "找到的服务为: " + i + mLeServices.get(i).getUuid());
				ser_str += "\n" + mLeServices.get(i).getUuid();
			}
			return ser_str;
		}
		else
		{
			return "Error Gatt";
		}
	}

	String ListCharacters(String uuid)
	{
		String ser_str = "";
		mGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
		List<BluetoothGattCharacteristic> tmp_listcha;
		if (mGattService == null)
		{
			Toast.makeText(getApplicationContext(), "Fetch Service Failed", Toast.LENGTH_SHORT).show();
			return "Error Service";
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Fetch Service Succeed", Toast.LENGTH_SHORT).show();
			
			tmp_listcha = mGattService.getCharacteristics();
			ser_str = "";
			for (int i = 0; i < tmp_listcha.size(); i++)
			{
				Log.w(TAG, "找到的特征为: " + i + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue());
				ser_str += "\n" + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue();
			}
			
			return ser_str;
		}
	}
	
	boolean EnableConfig(String uuid)
	{
		//此处开始按照协议对内容进行获取
		byte[] val = new byte[1];
		val[0] = 1;
		
		if (mGattService == null)
			return false;
		
		BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));
		
		if (charac == null)
			return false;
		
		charac.setValue(val); //conf
		mBluetoothGatt.writeCharacteristic(charac);
		
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	/*
	 * 读取数据前的配置工作，温度和湿度传感器的读取都要执行这个方法
	 */
	boolean EnableData(String uuid)
	{
		if (mGattService == null)
			return false;
		
		BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));
		
		if (charac == null)
			return false;
		
		boolean noti = mBluetoothGatt.setCharacteristicNotification(charac, true);
		Log.w(TAG, "noti " + noti);
		BluetoothGattDescriptor clientConfig = charac.getDescriptor(UUID.fromString(macro.UUID_CLIENT_CONFIG));
		clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(clientConfig);
		
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	class MyHandler extends Handler
	{
		public MyHandler() {}
		
		public MyHandler(Looper L)
		{
			super(L);
		}
		
		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			// 此处可以更新UI
			if (msg.what == macro.HANDLER_SCAN_STOPPED)
			{
				// tv_hello.setText(tv_hello.getText() + "\n" + "扫描结束");
				Toast.makeText(MainActivity.this,"Scanning Done", Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == macro.HANDLER_CONNECT_SUCCESS)
			{
				//Toast.makeText(getApplicationContext(), "连接已经建立", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("Connected Successfully, Start Searching Service"); // "连接成功，开始寻找服务"
			}
			else if (msg.what == macro.HANDLER_CONNECT_FAILED)
			{
				//Toast.makeText(getApplicationContext(), "连接建立失败", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("Connecting Failed, Back to Re-connect"); // 连接失败，请返回重连
			}
			else if (msg.what == macro.HANDLER_SERVICE_DISCOVERED)
			{
				//Toast.makeText(getApplicationContext(), "服务发现完毕", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("Service founded, Start up Service"); // 成功发现服务，开始启动服务
				boolean isHasValidData = false;
				ListServices();
				
				
				ListCharacters(macro.UUID_MAG_SER);
				
				if (EnableConfig(macro.UUID_MAG_CON) && EnableData(macro.UUID_MAG_DAT))
				{
					//Toast.makeText(getApplicationContext(), "开始传输磁场数据", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("Temperature Data Activated Succeed"); // 温度数据激活成功
					isHasValidData = true;
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "磁场特征写入失败", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("Temperature Data Activated Failed");  //温度数据激活失败
				}
				
				ListCharacters(macro.UUID_HUM_SER);
				
				if (EnableConfig(macro.UUID_HUM_CON) && EnableData(macro.UUID_HUM_DAT))
				{
					//Toast.makeText(getApplicationContext(), "开始传输湿度数据", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("Humidity Data Activated Succeed"); // 湿度数据激活成功
					isHasValidData = true;
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "湿度特征写入失败", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("Humidity Data Activated Failed"); // 湿度数据激活失败
				}
				
				if (isHasValidData == true)
				{
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MainTabActivity.class);
					intent.putExtra("str", "come from first activity");
					startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
					dl_connect.dismiss();
				}
			}
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.w("TAG", "sssssssssssss" + macro.INTENT_BLEACTIVITY_TESTSHOW);
		
		if (requestCode == macro.INTENT_BLEACTIVITY_TESTSHOW)
		{
			closeBle();
			if (macro.SETTING_EXIT_DIRECTLY == true) //上一个activity要求直接退出。
				finish();
		}
		
		
	}
	
	
	public Point3D convertHum(final byte[] value)
	{
		int a = shortUnsignedAtOffset(value, 2);   // 湿度
		
		// bits [1..0] are status bits and need to be cleared according
		// to the user guide, but the iOS code doesn't bother. It should
		// have minimal impact.
		a = a - (a % 4);
		
		return new Point3D((-6f) + 125f * (a / 65535f), 0, 0);    //湿度
	}
	
	public Point3D convertTemp(final byte[] value)
	{
		int a = shortUnsignedAtOffset(value, 0);     // 温度
		return new Point3D(-46.85f + 175.72f * (a / 65535f), 0, 0);    // 温度
	}
	
	public Point3D convertMag(final byte[] value)
	{    // 压力，暂时没用到该数据
		Point3D mcal = MagnetometerCalibrationCoefficients.INSTANCE.val;
		// Multiply x and y with -1 so that the values correspond with the image in the app
		float x = shortSignedAtOffset(value, 0) * (2000f / 65536f) * -1;
		float y = shortSignedAtOffset(value, 2) * (2000f / 65536f) * -1;
		float z = shortSignedAtOffset(value, 4) * (2000f / 65536f);
		
		return new Point3D(x - mcal.x, y - mcal.y, z - mcal.z);
	}
	
	public enum MagnetometerCalibrationCoefficients
	{
		INSTANCE;
		
		Point3D val = new Point3D(0.0, 0.0, 0.0);
	}
	
	private static Integer shortUnsignedAtOffset(byte[] c, int offset)
	{
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1] & 0xFF; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}
	
	private static Integer shortSignedAtOffset(byte[] c, int offset)
	{
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1]; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}
}
