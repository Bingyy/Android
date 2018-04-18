package com.tactileshow.util;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class DataFile
{
	
	private PrintWriter out_press, out_temp;
	
	private RandomAccessFile out_avg_press, out_avg_temp;
	
	private double avg_press = -1, avg_temp = -1;
	
	private int records_press, records_temp;
	
	public DataFile()
	{
		StaticValue.record_time = new Time();
		StaticValue.record_time.setToNow(); // 设定记录时间
		Log.e("wshg", "Record Hour: " + StaticValue.record_time.hour);

		out_press = getTimePrintWriter(StaticValue.record_time, StaticValue.PRESS, "data", true);
		out_avg_press = getRandomAccessFile(StaticValue.record_time, StaticValue.PRESS, "avg");
		out_temp = getTimePrintWriter(StaticValue.record_time, StaticValue.TEMP, "data", true);
		out_avg_temp = getRandomAccessFile(StaticValue.record_time, StaticValue.TEMP, "avg");

		records_press = computeAvgData(StaticValue.PRESS);
		records_temp = computeAvgData(StaticValue.TEMP);
	}


	private String getTimeDirectory(Time t, String sensor)
	{
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String res = path + File.separator + "TactileShow" + File.separator + sensor + File.separator
                    + (t.month + 1) + File.separator + t.monthDay + File.separator + t.hour + File.separator;
            return res;
        }
        return null;
		// return "/storage/sdcard0/TactileShow/" + sensor + "/" + (t.month + 1) + "/" + t.monthDay + "/" + t.hour + "/";
	}

	private PrintWriter getTimePrintWriter(Time t, String sensor, String fileName, boolean append)
	{
		File f = new File(getTimeDirectory(t, sensor) + fileName);
		boolean fileExist = f.isFile();
		if (fileExist)
			Log.d("Is it a file?","Yes");
		else Log.d("Is it a file?","No");

		boolean createFileSuccess = false;
		boolean isDir = false;

		try
		{
			File parent = new File(f.getParent());
			boolean isFinished = parent.mkdirs(); // 创建目录也失败，现在拿到环境变量的路径也无法创建
            isDir = parent.isDirectory();
			File[] files = parent.listFiles();

			if (!fileExist) // 如果文件不存在则创建文件
            {
                f.createNewFile(); // 这一步无法执行，报错
                createFileSuccess = true;
            }

			fileExist = f.isFile();

			return new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, append)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private RandomAccessFile getRandomAccessFile(Time t, String sensor, String fileName)
	{
		// 先判断是否存在目录，new File不会创建目录
		File f = new File(getTimeDirectory(t, sensor) + fileName); // 只是在内存中创建了虚拟文件，真实的需要调用createNewFile创建
		try
		{
			File parent = new File(f.getParent());
			boolean isFinished = parent.mkdirs(); // 测试是否创建成功

			if (!f.isFile())
				f.createNewFile();

            boolean fileExist = f.isFile();

			return new RandomAccessFile(f, "rw");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// 此函数未得到有效的TimeBufferedReader, 报空
	public BufferedReader getTimeBufferedReader(Time t, String sensor, String fileName)
	{
		File f = new File(getTimeDirectory(t, sensor) + fileName);
		try
		{
			if (!f.isFile())
				return null;
			return new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeAvgData(Time t, String sensor, String data)
	{
		PrintWriter print = getTimePrintWriter(t, sensor, "avg", false);
		print.println(data);
		print.flush();
		print.close();
	}
	
	public String readAvgData(Time t, String sensor)
	{
		BufferedReader read = getTimeBufferedReader(t, sensor, "avg");
		if (read == null)
			return null;
		String line = null;
		try
		{
			line = read.readLine();
			read.close();
			return line;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return line;
		}
	}
	
	/*
	 * Return: the number of lines in data file.
	 *         -1: error in compute average.
	 */
	public int computeAvgData(String sensor)
	{
		BufferedReader in = getTimeBufferedReader(StaticValue.record_time, sensor, "data"); // 这里报空
		if (in == null)
		{
			
		}
		try
		{
			double all = 0;
			int lines = 0;
			String line = in.readLine(); // 此处运行出错
			while (line != null)
			{
				String[] pars = line.split(" ");
				all += Double.parseDouble(pars[1]);
				lines++;
				line = in.readLine();
			}
			if (lines != 0)
				all /= lines;
			if (sensor.equals(StaticValue.TEMP))
			{
				avg_temp = all;
				out_avg_temp.seek(0);
				out_avg_temp.writeUTF(Double.toString(all));
			}
			else if (sensor.equals(StaticValue.PRESS))
			{
				avg_press = all;
				out_avg_press.seek(0);
				out_avg_press.writeUTF(Double.toString(all));
			}
			return lines;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
		catch (NumberFormatException e2)
		{
			e2.printStackTrace();
			Log.e("wshg", "Compute average data error, cannot translate to double.");
			return -1;
		}
	}
	
	private boolean compareTime(Time t)
	{
		if (t.monthDay != StaticValue.record_time.monthDay || t.hour != StaticValue.record_time.hour || t.month != t.month)
			return false;
		return true;
	}
	
	private void reloadEnvironment(Time t)
	{
		out_press.close();
		out_temp.close();
		out_press = getTimePrintWriter(t, StaticValue.PRESS, "data", true);
		out_temp = getTimePrintWriter(t, StaticValue.TEMP, "data", true);
		out_avg_press = getRandomAccessFile(t, StaticValue.PRESS, "avg");
		out_avg_temp = getRandomAccessFile(t, StaticValue.TEMP, "avg");
		StaticValue.record_time = t;
		avg_press = -1;
		avg_temp = -1;
		records_press = computeAvgData(StaticValue.PRESS);
		records_temp = computeAvgData(StaticValue.TEMP);
	}
	
	public void writeData(Time t, String sensor, String data)
	{
		if (!compareTime(t))
		{
			reloadEnvironment(t);
		}
		
		try
		{
			if (sensor.equals(StaticValue.TEMP))
			{
				out_avg_temp.seek(0);
				out_avg_temp.writeUTF(Double.toString(((avg_temp * records_temp++) + Double.parseDouble(data)) / records_temp));
				out_temp.println(t.format2445() + " " + data);
				out_temp.flush();
				
			}
			else if (sensor.equals(StaticValue.PRESS))
			{
				out_avg_press.seek(0);
				out_avg_press.writeUTF(Double.toString(((avg_press * records_press++) + Double.parseDouble(data)) / records_press));
				out_press.println(t.format2445() + " " + data);
				out_press.flush();
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			Log.e("wshg", "Format data error, the data received may not be double.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e("wshg", "Write average data to file error.");
		}
	}
}