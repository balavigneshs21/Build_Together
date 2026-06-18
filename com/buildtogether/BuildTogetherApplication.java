package com.buildtogether;

import com.buildtogether.features.signin.LoginView;
import com.buildtogether.features.signup.SignUpView;
import com.buildtogether.util.ConsoleInput;

import java.util.Scanner;
public class BuildTogetherApplication {

    public static void main(String[] args) {
        System.out.println("Welcome to BuildTogether");
        System.out.println("Connecting Developers with Investors");
        showLandingMenu();
    }

    private static void showLandingMenu() {
        Scanner scanner = ConsoleInput.getScanner();
        while(true){
            System.out.println();
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    new SignUpView().init();
                    break;
                case "2":
                    new LoginView().init();
                    break;
                case "3":
                    System.out.println("Thank you for using BuildTogether. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}