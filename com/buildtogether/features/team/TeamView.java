package com.buildtogether.features.team;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TeamView {

    private final TeamModel teamModel;
    private final User user;
    private final Scanner scanner;

    public TeamView(User user) {
        this.user = user;
        this.teamModel = new TeamModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView case "2" ──────────────────────────────────────
    public void init() {
        System.out.println();
        System.out.println("=== Create Team ===");
        System.out.print("Enter team name: ");
        String teamName = scanner.nextLine().trim();
        teamModel.createTeam(user, teamName);
    }

    // ── called from HomeView case "3" ──────────────────────────────────────
    public void initManageMembers() {
        System.out.println();
        System.out.println("=== Manage Team Members ===");
        System.out.println("1. Invite a developer");
        System.out.println("2. Remove a member");
        System.out.println("3. View current members");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                teamModel.loadAllDevelopers(user);
                break;
            case "2":
                teamModel.loadMembers(user);
                break;
            case "3":
                teamModel.loadMembers(user);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ── called from HomeView case "12" ─────────────────────────────────────
    public void initMyInvitations() {
        System.out.println();
        System.out.println("=== My Team Invitations ===");
        teamModel.loadMyInvitations(user);
    }

    // ── called from HomeView case "13" — NEW MyTeams feature ───────────────
    public void initMyTeams() {
        System.out.println();
        System.out.println("=== My Teams ===");
        teamModel.loadMyTeams(user);
    }

    // =========================================================================
    // Callbacks — TeamModel calls these back
    // =========================================================================

    void onTeamCreated(Team team) {
        System.out.println();
        System.out.println("Team created successfully!");
        System.out.println("Team Name : " + team.getTeamName());
        System.out.println("Team ID   : " + team.getId());
        System.out.println("You are the team leader.");
    }

    void onTeamCreateFailed(String message) {
        System.out.println("Team creation failed: " + message);
    }

    void showAllDevelopers(List<Developer> developers,
                           List<User> users,
                           Long myDeveloperId) {

        List<Developer> available = new ArrayList<>();
        for (Developer dev : developers) {
            if (!dev.getId().equals(myDeveloperId)) {
                available.add(dev);
            }
        }

        if (available.isEmpty()) {
            System.out.println("No other developers available to invite.");
            System.out.println("Other developers must complete their profile first.");
            return;
        }

        System.out.println();
        System.out.println("Available Developers to Invite:");
        System.out.println("──────────────────────────────────");
        for (Developer dev : available) {
            String name = "Unknown";
            for (User u : users) {
                if (u.getId().equals(dev.getUserId())) {
                    name = u.getName();
                    break;
                }
            }
            System.out.println("ID         : " + dev.getId());
            System.out.println("Name       : " + name);
            System.out.println("Skills     : " + dev.getSkills());
            System.out.println("Experience : " + dev.getExperienceLevel());
            System.out.println("──────────────────────────────────");
        }

        System.out.print("Enter Developer ID to invite (or 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;
        teamModel.sendInvite(user, input);
    }

    void onInviteSent(String developerName) {
        System.out.println();
        System.out.println("Invitation sent successfully to: " + developerName);
        System.out.println("They will be added to your team once they accept.");
    }

    void onInviteFailed(String message) {
        System.out.println("Invitation failed: " + message);
    }

    void showMembers(List<TeamMember> members,
                     List<Developer> developers,
                     List<User> users,
                     Long teamLeaderId) {

        if (members.isEmpty()) {
            System.out.println("No accepted members in your team yet.");
            return;
        }

        System.out.println();
        System.out.println("Current Team Members:");
        System.out.println("──────────────────────────────────");
        for (TeamMember member : members) {
            String name = "Unknown";
            String skills = "Unknown";
            String experience = "Unknown";
            for (Developer dev : developers) {
                if (dev.getId().equals(member.getDeveloperId())) {
                    skills = dev.getSkills();
                    experience = dev.getExperienceLevel();
                    for (User u : users) {
                        if (u.getId().equals(dev.getUserId())) {
                            name = u.getName();
                            break;
                        }
                    }
                    break;
                }
            }
            String role = member.getDeveloperId().equals(teamLeaderId)
                    ? "Team Leader" : "Member";
            System.out.println("Developer ID : " + member.getDeveloperId());
            System.out.println("Name         : " + name);
            System.out.println("Skills       : " + skills);
            System.out.println("Experience   : " + experience);
            System.out.println("Role         : " + role);
            System.out.println("──────────────────────────────────");
        }

        System.out.print("Enter Developer ID to remove (or 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;
        teamModel.removeMember(user, input);
    }

    void onMemberRemoved() {
        System.out.println("Member removed successfully.");
    }

    void onMemberRemoveFailed(String message) {
        System.out.println("Failed to remove member: " + message);
    }

    void showMyInvitations(List<TeamMember> invitations,
                           List<Team> teams,
                           List<Developer> developers,
                           List<User> users) {

        if (invitations.isEmpty()) {
            System.out.println("You have no pending team invitations.");
            return;
        }

        System.out.println();
        System.out.println("Your Pending Invitations:");
        System.out.println("──────────────────────────────────");
        for (TeamMember invite : invitations) {
            String teamName = "Unknown";
            String leaderName = "Unknown";
            for (Team team : teams) {
                if (team.getId().equals(invite.getTeamId())) {
                    teamName = team.getTeamName();
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
                    break;
                }
            }
            System.out.println("Team ID     : " + invite.getTeamId());
            System.out.println("Team Name   : " + teamName);
            System.out.println("Invited by  : " + leaderName);
            System.out.println("Status      : " + invite.getStatus());
            System.out.println("──────────────────────────────────");
        }

        System.out.println();
        System.out.println("1. Accept an invitation");
        System.out.println("2. Decline an invitation");
        System.out.println("3. Go back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter Team ID to accept: ");
                teamModel.acceptInvite(user, scanner.nextLine().trim());
                break;
            case "2":
                System.out.print("Enter Team ID to decline: ");
                teamModel.declineInvite(user, scanner.nextLine().trim());
                break;
            case "3":
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    void onInviteAccepted(String teamName) {
        System.out.println();
        System.out.println("You have joined the team: " + teamName);
        System.out.println("Team leader has been notified.");
    }

    void onInviteDeclined(String teamName) {
        System.out.println();
        System.out.println("You have declined the invitation from: " + teamName);
        System.out.println("Team leader has been notified.");
    }

    void onInviteActionFailed(String message) {
        System.out.println("Action failed: " + message);
    }

    // ── MY TEAMS — shows all teams developer has joined ────────────────────
    void showMyTeams(List<Team> teams,
                     List<Developer> developers,
                     List<User> users,
                     List<Project> projects) {

        if (teams.isEmpty()) {
            System.out.println("You are not part of any team yet.");
            System.out.println("Accept an invitation to join a team (option 12).");
            return;
        }

        System.out.println();
        System.out.println("Teams you are part of:");
        System.out.println("══════════════════════════════════════════");

        for (Team team : teams) {

            // find leader name
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

            // find projects for this team
            List<String> projectNames = new ArrayList<>();
            for (Project project : projects) {
                if (project.getTeamId().equals(team.getId())) {
                    projectNames.add(project.getTitle()
                            + " [" + project.getStatus() + "]");
                }
            }

            System.out.println("Team ID     : " + team.getId());
            System.out.println("Team Name   : " + team.getTeamName());
            System.out.println("Team Leader : " + leaderName);

            if (projectNames.isEmpty()) {
                System.out.println("Projects    : No projects posted yet");
            } else {
                System.out.println("Projects    :");
                for (String projectName : projectNames) {
                    System.out.println("              - " + projectName);
                }
            }
            System.out.println("══════════════════════════════════════════");
        }

        System.out.println();
        System.out.println("1. Leave a team");
        System.out.println("2. Go back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.println();
            System.out.print("Enter Team ID to leave: ");
            String teamIdInput = scanner.nextLine().trim();
            teamModel.leaveTeam(user, teamIdInput);
        }
    }

    void onLeaveTeamSuccess(String teamName) {
        System.out.println();
        System.out.println("You have left the team: " + teamName);
        System.out.println("Team leader has been notified.");
    }

    void onLeaveTeamFailed(String message) {
        System.out.println("Failed to leave team: " + message);
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
