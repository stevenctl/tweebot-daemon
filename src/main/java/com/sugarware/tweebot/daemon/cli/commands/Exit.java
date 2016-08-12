package com.sugarware.tweebot.daemon.cli.commands;

import com.sugarware.tweebot.daemon.cli.Command;

public class Exit implements Command {

	@Override
	public void execute(String... args) {
		if(args.length > 0){
			try{
				int code = Integer.parseInt(args[0]);
				System.exit(code);
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
}
