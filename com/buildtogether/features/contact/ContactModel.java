package com.buildtogether.features.contact;

import com.buildtogether.dto.ContactRequest;
import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.List;

class ContactModel {

    private final ContactView contactView;

    ContactModel(ContactView contactView) {
        this.contactView = contactView;
    }

    // =========================================================================
    // Load all teams — for investor to pick who to contact
    // =========================================================================

    void loadAllTeams(User user) {

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            contactView.showError(
                    "Please complete your investor profile first (option 1)");
            return;
        }

        List<Team> teams = BuildTogetherDB.getInstance().getAllTeams();
        if (teams.isEmpty()) {
            contactView.showError("No teams available to contact.");
            return;
        }

        // pass all users so View can show team leader names
        List<Developer> developers = BuildTogetherDB.getInstance().getAllDevelopers();
        List<User> users = BuildTogetherDB.getInstance().getAllUsers();

        contactView.showAllTeams(teams, developers, users);
    }

    // =========================================================================
    // Send contact request — investor → team leader
    // also sends a NOTIFICATION to the developer
    // =========================================================================

    void sendContactRequest(User user, String leaderIdInput, String message) {

        if (message == null || message.isEmpty()) {
            contactView.onRequestFailed("Message cannot be empty");
            return;
        }

        Long teamLeaderId;
        try {
            teamLeaderId = Long.parseLong(leaderIdInput);
        } catch (NumberFormatException e) {
            contactView.onRequestFailed("Invalid leader ID. Please enter a number.");
            return;
        }

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            contactView.onRequestFailed("Investor profile not found");
            return;
        }

        // check team leader (developer) exists
        Developer leaderDeveloper = BuildTogetherDB.getInstance()
                .getDeveloperById(teamLeaderId);
        if (leaderDeveloper == null) {
            contactView.onRequestFailed(
                    "No team leader found with ID: " + teamLeaderId);
            return;
        }

        boolean alreadyContacted = BuildTogetherDB.getInstance()
                .hasAlreadyContacted(investor.getId(), teamLeaderId);
        if (alreadyContacted) {
            contactView.onRequestFailed(
                    "You already have a pending contact request to this team leader");
            return;
        }

        // save contact request
        ContactRequest request = new ContactRequest();
        request.setInvestorId(investor.getId());
        request.setTeamLeaderId(teamLeaderId);
        request.setMessage(message);

        ContactRequest saved = BuildTogetherDB.getInstance()
                .addContactRequest(request);

        if (saved != null) {
            // ── SEND NOTIFICATION to developer team leader ─────────────────
            User leaderUser = BuildTogetherDB.getInstance()
                    .getUserById(leaderDeveloper.getUserId());
            if (leaderUser != null) {
                BuildTogetherDB.getInstance().addNotification(
                        leaderUser.getId(),
                        "[NEW MESSAGE] Investor '"
                        + investor.getCompanyName()
                        + "' has sent you a contact request: \""
                        + message
                        + "\". Check option 11 (Messages from investors).");
            }
            contactView.onRequestSent(saved);
        } else {
            contactView.onRequestFailed("Something went wrong. Please try again.");
        }
    }

    // =========================================================================
    // Load sent requests — investor views what they sent
    // =========================================================================

    void loadSentRequests(User user) {

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            contactView.showError(
                    "Please complete your investor profile first (option 1)");
            return;
        }

        List<ContactRequest> requests = BuildTogetherDB.getInstance()
                .getContactRequestsByInvestorId(investor.getId());

        contactView.showSentRequests(requests);
    }

    // =========================================================================
    // Load incoming requests — developer (team leader) views messages
    // =========================================================================

    void loadIncomingRequests(User user) {

        // Step 1 — get developer profile
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            contactView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        // Step 2 — check this developer is a team leader
        Team team = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(developer.getId());
        if (team == null) {
            contactView.showError(
                    "You are not a team leader. Only team leaders receive messages.");
            return;
        }

        // Step 3 — get all contact requests sent to this team leader
        List<ContactRequest> requests = BuildTogetherDB.getInstance()
                .getContactRequestsByTeamLeaderId(developer.getId());

        // Step 4 — get all investors to show company names
        List<Investor> investors = BuildTogetherDB.getInstance().getAllInvestors();

        // Step 5 — get all users to show investor names
        List<User> users = BuildTogetherDB.getInstance().getAllUsers();

        contactView.showIncomingRequests(requests, investors, users);
    }
}
