package com.tactileshow.util;

public class macro
{
	public static int INTENT_REQUEST_ENABLE_BT = 10001;
	
	public static long BLE_SCAN_PERIOD = 10000;
	
	public static int HANDLER_SERVICE_DISCOVERED = 20000;
	
	public static int HANDLER_SCAN_STOPPED = 20001;
	
	public static int HANDLER_UPDATE_LISTVIEW = 20002;
	
	public static int HANDLER_CONNECT_SUCCESS = 20003;
	
	public static int HANDLER_CONNECT_FAILED = 20004;
	
	public static int HANDLER_NEWDATA = 20005;
	
	public static String BROADCAST_ADDRESS = "zju.ccnt.ble";
	
	public static int MENU_GROUPID_BLE = 21000;
	
	public static int MENU_ITEMID_FRESH = 21001;
	
	public static int MENU_ITEMID_EXIT = 21002;
	
	public static int MENU_ITEMID_DEBUG = 21003;
	
	public static String UUID_HUM_SER = "f000aa20-0451-4000-b000-000000000000";//压力
	
	public static String UUID_HUM_DAT = "f000aa21-0451-4000-b000-000000000000";
	
	public static String UUID_HUM_CON = "f000aa22-0451-4000-b000-000000000000";
	
	public static String UUID_CLIENT_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	
	public static String UUID_IRT_SER = "f000aa00-0451-4000-b000-000000000000";
	
	public static String UUID_IRT_DAT = "f000aa01-0451-4000-b000-000000000000";
	
	public static String UUID_IRT_CON = "f000aa02-0451-4000-b000-000000000000"; // 0: disable, 1: enable
	
	public static String UUID_MAG_SER = "f000aa30-0451-4000-b000-000000000000";//温度
	
	public static String UUID_MAG_DAT = "f000aa31-0451-4000-b000-000000000000";
	
	public static String UUID_MAG_CON = "f000aa32-0451-4000-b000-000000000000"; // 0: disable, 1: enable
	
	public static String UUID_MAG_PER = "f000aa33-0451-4000-b000-000000000000"; // Period in tens of milliseconds
	
	
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	
	public static int INTENT_BLEACTIVITY_TESTSHOW = 22000;
	
	public static long[] VIBRATION_MODE = {100, 400, 100, 400};   // 用于手机振动的参数
	
	public static boolean SETTINGS_SOUND = false;
	
	public static boolean SETTINGS_VIBRA = false;
	
	public static boolean SETTINGS_BCAST = false;
	
	public static double SETTING_TEMP_RANGE[] = {10, 20, 40};
	
	public static double SETTING_PRESS_RANGE[] = {30, 50, 80};
	
	public static double SETTING_GERM_RANGE[] = {38, 42};
	
	public static boolean SETTING_EXIT_DIRECTLY = false;

	public static String TEMPERATURE_TREND = "Temperature Changing Trend"; //温度变化趋势

	public static String TEMPERATURE_HISTORY_RECORDS = "Temperature History Records";  //温度历史记录

	public static String HUMIDITY_TREND = "Humidity Changing Trend";// "湿度变化趋势"

	public static String HUMIDITY_HISTORY_RECORDS = "Humidity History Records";  // 湿度历史记录

	public static String SEARCH_BY_HOUR = "Search by Hour"; //按小时查询

	public static String SEARCH_BY_DAY = "Search by Day"; //按天查询

	public static String OVER_HEAT = "Over Heat"; // 过热

	public static String COMFORTABLE = "Comfortable"; // 舒适

	public static String COLD = "Cold"; // 偏冷

	public static String TOO_COLD = "Too Cold"; // 过低

	public static String PRESS_SEVERELY_AND_OFF = "Press Severely, Get Off"; // 严重挤压，请离开

	public static String PRESS_HEAVY = "Press Heavily"; // 较重挤压

	public static String PRESS_LIGHT = "Press Lighty"; // 轻微挤压

	public static String NO_PRESS = "No Press"; // 无挤压

	public static String INFLAMMATION = "May Inflammation"; // 可能出现炎症

	public static String NO_INFLAMMTION = "No Inflammation"; // 无炎症

	public static String NO_ELECTRONIC_SKIN = "No Electronic Skin this Area"; // 该区域无电子皮肤

	public static String DATE_SETTING = "Date Setting"; // "日期设定"

	public static String INSTANT_INFO = "Instant Info"; // "实时信息"

	public static String HISTORY_RECORDS = "History Records"; // "历史记录"

	public static String SETTING_THRESHOLD_INFO = "Setting Threshold Info";  // "设定阈值信息"

	public static String SETTING_BROADCAST_INFO = "Setting Broadcast Info"; // "设定广播信息"

	public static String SOUND_REMINDING_CLOSED = "Sound Reminding Closed"; // "声音提醒已关闭"

	public static String SOUND_REMINDING_OPENED = "Sound Reminding Opened"; // "声音提醒已开启"

	public static String VIBRATING_REMINDING_CLOSED = "Vibrating Reminding Closed"; // "震动提醒已关闭"

	public static String VIBRATING_REMIDNING_OPENED = "Vibrating Reminding Opened"; // "震动提醒已开启"

	public static String TEST_BROADCAST_CLOSED = "Test Broadcast Closed"; // "测试广播已关闭"

	public static String TEST_BROADCAST_OPENED = "Test Broadcast Opened"; // "测试广播已开启"


}
