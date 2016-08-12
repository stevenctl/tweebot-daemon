package com.sugarware.tweebot.daemon.cli.commands;

import com.sugarware.tweebot.daemon.cli.Command;
import com.sugarware.tweebot.daemon.util.PropertiesUtil;

public class Property implements Command{

	private static final String USAGE = "property property_name";
	
	@Override
	public void execute(String... args) {
		if(args.length < 1){
			System.out.println(USAGE);
		}
		System.out.println(PropertiesUtil.getInstance().get(args[0]));
	}

}
