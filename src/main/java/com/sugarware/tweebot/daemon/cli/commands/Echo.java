package com.sugarware.tweebot.daemon.cli.commands;

import com.sugarware.tweebot.daemon.cli.Command;

public class Echo implements Command {

	@Override
	public void execute(String... args) {
		String fullArgs = "";
		for(String str : args){
			fullArgs += str + " ";
		}
		fullArgs = fullArgs.trim();
		System.out.println(fullArgs);
	}
	
}
