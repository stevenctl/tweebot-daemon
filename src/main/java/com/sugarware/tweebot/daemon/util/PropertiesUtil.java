package com.sugarware.tweebot.daemon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

	private static Properties INSTANCE;

	public static Properties getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Properties();
			try {
				File f = new File("config.properties");
				INSTANCE.load(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return INSTANCE;
	}

	public static int[] getTiers() {
		Properties props = PropertiesUtil.getInstance();
		String[] tierStr = props.getProperty("tiers").split(",");
		int[] tiers = new int[tierStr.length];
		for (int i = 0; i < tiers.length; i++)
			tiers[i] = Integer.parseInt(tierStr[i]);
		return tiers;
	}

	public static int getTierCount(int tier) {
		Properties props = PropertiesUtil.getInstance();
		return Integer.parseInt(props.getProperty("tier" + tier + "PerDay"));
	}

	public static int getDailyTaskCount() {
		Properties props = PropertiesUtil.getInstance();
		String[] tierStr = props.getProperty("tiers").split(",");
		int total = 0;
		for (String s : tierStr) {
			total += Integer.parseInt(props.getProperty("tier" + s + "PerDay"));
		}
		return total;
	}

}
