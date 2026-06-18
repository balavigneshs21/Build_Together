package com.buildtogether.features.developer;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

class DeveloperModel {

    private final DeveloperView developerView;

    DeveloperModel(DeveloperView developerView) {
        this.developerView = developerView;
    }

    // =========================================================================
    // Load profile — checks if exists or needs to be created
    // =========================================================================

    void loadProfile(User user) {
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            // no profile yet — tell View to prompt creation
            developerView.promptCreateProfile();
        } else {
            // profile exists — show it
            developerView.showProfile(developer);
        }
    }

    // =========================================================================
    // Create profile
    // =========================================================================

    void createProfile(User user, String bio, String skills, String levelChoice) {

        // Step 1 — validate bio
        if (bio == null || bio.isEmpty()) {
            developerView.onProfileFailed("Bio cannot be empty");
            return;
        }

        // Step 2 — validate skills
        if (skills == null || skills.isEmpty()) {
            developerView.onProfileFailed("Skills cannot be empty");
            return;
        }

        // Step 3 — validate experience level
        String experienceLevel;
        switch (levelChoice) {
            case "1":
                experienceLevel = "Beginner";
                break;
            case "2":
                experienceLevel = "Mid";
                break;
            case "3":
                experienceLevel = "Senior";
                break;
            default:
                developerView.onProfileFailed(
                        "Invalid experience level. Please choose 1, 2 or 3");
                return;
        }

        // Step 4 — check profile does not already exist
        Developer existing = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (existing != null) {
            developerView.onProfileFailed(
                    "A developer profile already exists for this account");
            return;
        }

        // Step 5 — build and save developer object
        Developer developer = new Developer();
        developer.setUserId(user.getId());
        developer.setBio(bio);
        developer.setSkills(skills);
        developer.setExperienceLevel(experienceLevel);

        Developer saved = BuildTogetherDB.getInstance().addDeveloper(developer);
        if (saved != null) {
            developerView.onProfileCreated(saved);
        } else {
            developerView.onProfileFailed("Something went wrong. Please try again.");
        }
    }
}
