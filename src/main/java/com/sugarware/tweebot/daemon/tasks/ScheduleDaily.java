package com.sugarware.tweebot.daemon.tasks;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.sugarware.tweebot.daemon.util.PropertiesUtil;
import com.sugarware.tweebot.daemon.util.TaskQueueUtil;

/**
 * Task that will set up the daily actions.
 * 
 * @author Steven
 *
 */
public class ScheduleDaily implements Runnable {

	@Override
	public void run() {
		int total = 0;
		
		int[] tiers = PropertiesUtil.getTiers();
		Map<Integer, Integer> tierCount = new HashMap<>();
		for(int t : tiers){
			tierCount.put(t, PropertiesUtil.getTierCount(t));
		}
		
		int tIndex = tiers.length - 1;
		
		while (totalCount(tierCount) > 0) {
			while (tierCount.get(tiers[tIndex]) < 1) {
				tIndex--;
				if (tIndex < 1)
					tIndex = tiers.length - 1;
			}
			System.out.println(tiers[tIndex]);
			TaskQueueUtil.getInstance().addTask(new PerformLevelPoliciesTask(tiers[tIndex]));
			tierCount.put(tiers[tIndex],tierCount.get(tiers[tIndex]) - 1);
			tIndex--;
			total++;
			if (tIndex < 1)
				tIndex = tiers.length - 1;
		}
		
		System.out.println("Scheduled " + total + " events.");
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		System.out.println("Will run this scheduling task again at " + tomorrow.getTime().toString());
		
	}

	private static int totalCount(Map<Integer, Integer> map) {
		int total = 0;
		for (int k : map.keySet())
			total += map.get(k);
		return total;
	}

}
