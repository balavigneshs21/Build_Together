package com.buildtogether.features.developer;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.Scanner;

public class DeveloperView {

    private final DeveloperModel developerModel;
    private final Scanner scanner;
    private final User user;

    public DeveloperView(User user) {
        this.user = user;
        this.developerModel = new DeveloperModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView case "1" ──────────────────────────────────────
    // checks if profile exists → if yes view it, if no create it
    public void initViewOrCreate() {
        developerModel.loadProfile(user);
    }

    // ── called internally when no profile found ────────────────────────────
    public void init() {
        System.out.println();
        System.out.println("=== Complete Your Developer Profile ===");

        System.out.print("Enter your bio (short description about yourself): ");
        String bio = scanner.nextLine().trim();

        System.out.print("Enter your skills (e.g. Java, Python, React): ");
        String skills = scanner.nextLine().trim();

        System.out.println("Select your experience level:");
        System.out.println("1. Beginner");
        System.out.println("2. Mid");
        System.out.println("3. Senior");
        System.out.print("Choose (1, 2 or 3): ");
        String levelChoice = scanner.nextLine().trim();

        developerModel.createProfile(user, bio, skills, levelChoice);
    }

    // =========================================================================
    // Callbacks — DeveloperModel calls these back
    // =========================================================================

    void onProfileCreated(Developer developer) {
        System.out.println();
        System.out.println("Developer profile created successfully!");
        System.out.println("Name       : " + user.getName());
        System.out.println("Bio        : " + developer.getBio());
        System.out.println("Skills     : " + developer.getSkills());
        System.out.println("Experience : " + developer.getExperienceLevel());
    }

    void onProfileFailed(String message) {
        System.out.println("Profile creation failed: " + message);
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }

    // ── called back when profile already exists ────────────────────────────
    void showProfile(Developer developer) {
        System.out.println();
        System.out.println("=== Your Developer Profile ===");
        System.out.println("Name       : " + user.getName());
        System.out.println("Email      : " + user.getEmail());
        System.out.println("Bio        : " + developer.getBio());
        System.out.println("Skills     : " + developer.getSkills());
        System.out.println("Experience : " + developer.getExperienceLevel());
    }

    // ── called back when no profile found ─────────────────────────────────
    void promptCreateProfile() {
        System.out.println();
        System.out.println("You have not set up your developer profile yet.");
        System.out.println("Let's create it now.");
        init();
    }
}
