package com.buildtogether.util;

import java.util.Scanner;

//What it is: A shared Scanner — the tool that reads what the user types in the terminal. 
//We make it a singleton so every class uses the same one (not create a new Scanner each time).


public class ConsoleInput {
	
	private static Scanner scanner = null;
    private ConsoleInput() {
    }
    public static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

}
