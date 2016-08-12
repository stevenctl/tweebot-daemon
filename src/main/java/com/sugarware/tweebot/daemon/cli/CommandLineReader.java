package com.sugarware.tweebot.daemon.cli;

import java.util.Scanner;

public class CommandLineReader {

	public void startReading() {
		boolean run = true;
		Scanner sc = new Scanner(System.in);
		while (run) {
			String input = sc.nextLine();
			executeCommand(input);
		}
		sc.close();
	}

	private void executeCommand(String command) {
		if (command.length() == 0)
			return;
		String[] commandParts = command.split(" ");
		String commandName = commandParts[0];
		String[] args = new String[commandParts.length - 1];
		for (int i = 1; i < commandParts.length; i++) {
			args[i - 1] = commandParts[i];
		}
		String commandClass = Character.toUpperCase(commandName.charAt(0))
				+ commandName.substring(1, commandName.length());
		commandClass = "com.sugarware.tweebot.daemon.cli.commands." + commandClass;
		try {
			Class<?> c = Class.forName(commandClass);
			if (!Command.class.isAssignableFrom(c)) {
				System.out.println("invalid command " + commandName);
				return;
			}
			try {
				Command cmd = (Command) c.newInstance();
				cmd.execute(args);
			} catch (InstantiationException e) {
				System.out.println("error executing command + commandName");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			System.out.println("invalid command " + commandName);
		}
	}
}
