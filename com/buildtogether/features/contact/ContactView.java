package com.buildtogether.features.contact;

import com.buildtogether.dto.ContactRequest;
import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class ContactView {

    private final ContactModel contactModel;
    private final User user;
    private final Scanner scanner;

    public ContactView(User user) {
        this.user = user;
        this.contactModel = new ContactModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView Investor case "5" ─────────────────────────────
    public void init() {
        System.out.println();
        System.out.println("=== Contact Team Leader ===");
        System.out.println("1. Send contact request");
        System.out.println("2. View my sent requests");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                contactModel.loadAllTeams(user);
                break;
            case "2":
                contactModel.loadSentRequests(user);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ── called from HomeView Developer case "11" ───────────────────────────
    public void initViewIncomingMessages() {
        System.out.println();
        System.out.println("=== Incoming Messages from Investors ===");
        contactModel.loadIncomingRequests(user);
    }

    // =========================================================================
    // Callbacks — ContactModel calls these back
    // =========================================================================

    // ── Investor side — show all teams with leader name ────────────────────
    void showAllTeams(List<Team> teams, List<Developer> developers, List<User> users) {
        if (teams.isEmpty()) {
            System.out.println("No teams found.");
            return;
        }
        System.out.println();
        System.out.println("Available Teams:");
        System.out.println("──────────────────────────────────────────");
        for (Team team : teams) {

            // find leader name from developer → user
            String leaderName = "Unknown";
            for (Developer dev : developers) {
                if (dev.getId().equals(team.getTeamLeaderId())) {
                    for (User u : users) {
                        if (u.getId().equals(dev.getUserId())) {
                            leaderName = u.getName();
                            break;
                        }
                    }
                    break;
                }
            }

            System.out.println("Team ID      : " + team.getId());
            System.out.println("Team Name    : " + team.getTeamName());
            System.out.println("Leader ID    : " + team.getTeamLeaderId());
            System.out.println("Leader Name  : " + leaderName);
            System.out.println("──────────────────────────────────────────");
        }

        System.out.print("Enter Team Leader ID to contact (or 0 to cancel): ");
        String leaderIdInput = scanner.nextLine().trim();
        if (leaderIdInput.equals("0")) return;

        System.out.print("Enter your message: ");
        String message = scanner.nextLine().trim();

        contactModel.sendContactRequest(user, leaderIdInput, message);
    }

    // ── Investor side — view sent requests ─────────────────────────────────
    void showSentRequests(List<ContactRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("You have not sent any contact requests yet.");
            return;
        }
        System.out.println();
        System.out.println("Your Sent Contact Requests:");
        System.out.println("──────────────────────────────────────────");
        for (ContactRequest request : requests) {
            System.out.println("Request ID    : " + request.getId());
            System.out.println("Team Leader ID: " + request.getTeamLeaderId());
            System.out.println("Message       : " + request.getMessage());
            System.out.println("Status        : " + request.getStatus());
            System.out.println("──────────────────────────────────────────");
        }
    }

    void onRequestSent(ContactRequest request) {
        System.out.println();
        System.out.println("Contact request sent successfully!");
        System.out.println("Request ID    : " + request.getId());
        System.out.println("Team Leader ID: " + request.getTeamLeaderId());
        System.out.println("Message       : " + request.getMessage());
        System.out.println("Status        : " + request.getStatus());
        System.out.println("Developer has been notified.");
    }

    void onRequestFailed(String message) {
        System.out.println("Contact request failed: " + message);
    }

    // ── Developer side — view incoming messages with investor name ──────────
    void showIncomingRequests(List<ContactRequest> requests,
                               List<Investor> investors,
                               List<User> users) {
        if (requests.isEmpty()) {
            System.out.println("No incoming messages from investors yet.");
            return;
        }
        System.out.println();
        System.out.println("Incoming Messages from Investors:");
        System.out.println("──────────────────────────────────────────");
        for (ContactRequest request : requests) {

            // find investor company name + investor user name
            String companyName = "Unknown";
            String investorUserName = "Unknown";
            for (Investor investor : investors) {
                if (investor.getId().equals(request.getInvestorId())) {
                    companyName = investor.getCompanyName();
                    // find investor's user name
                    for (User u : users) {
                        if (u.getId().equals(investor.getUserId())) {
                            investorUserName = u.getName();
                            break;
                        }
                    }
                    break;
                }
            }

            System.out.println("Request ID  : " + request.getId());
            System.out.println("From        : " + investorUserName
                    + " (" + companyName + ")");
            System.out.println("Message     : " + request.getMessage());
            System.out.println("Status      : " + request.getStatus());
            System.out.println("──────────────────────────────────────────");
        }
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
