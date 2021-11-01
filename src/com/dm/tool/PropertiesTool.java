package com.dm.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class PropertiesTool {

	public static String defaultFileName;

	//分析账单的配置map
	public static Map<String, List<String>> costProMap = new LinkedHashMap<String, List<String>>();
	public static Map<String, List<String>> earnProMap = new LinkedHashMap<String, List<String>>();

	public static ArrayList<String> tipProList = new ArrayList<String>();
	public static boolean keyListen = true;//是否开启键盘监听，默认开启（支持ctrl-z ctrl-y的全局监听）

	public static ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");

	static {
		Properties pro = new Properties();
		try {
			pro.load(new InputStreamReader(new FileInputStream("setting.properties"), "UTF-8"));

			keyListen = pro.getProperty("keyListen") == null ? true
					: Boolean.parseBoolean(pro.getProperty("keyListen"));

			defaultFileName = pro.getProperty("default") == null ? "" : pro.getProperty("default");

			String costTypeStr = pro.getProperty("costType") == null ? "" : pro.getProperty("costType");
			if (!"".equals(costTypeStr)) {
				String[] costArr = costTypeStr.split(",");
				for (String key : costArr) {
					String valueStr = pro.getProperty(key) == null ? "" : pro.getProperty(key);
					if (!"".equals(valueStr)) {
						String[] valueArr = valueStr.split(",");
						List<String> valueList = Arrays.asList(valueArr);
						costProMap.put(key, valueList);
					}
				}
			}

			String earnTypeStr = pro.getProperty("earnType") == null ? "" : pro.getProperty("earnType");
			if (!"".equals(earnTypeStr)) {
				String[] earnArr = earnTypeStr.split(",");
				for (String key : earnArr) {
					String valueStr = pro.getProperty(key) == null ? "" : pro.getProperty(key);
					if (!"".equals(valueStr)) {
						String[] valueArr = valueStr.split(",");
						List<String> valueList = Arrays.asList(valueArr);
						earnProMap.put(key, valueList);
					}
				}
			}
			System.out.println(costProMap);
			System.out.println(earnProMap);

			String tipStr = pro.getProperty("tipTip") == null ? "" : pro.getProperty("tipTip");
			if (!"".equals(tipStr)) {
				String[] tipArr = tipStr.split(",");
				for (String str : tipArr) {
					tipProList.add(str);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
