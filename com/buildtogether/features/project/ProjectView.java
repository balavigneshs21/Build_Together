package com.buildtogether.features.project;

import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TechStack;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class ProjectView {

    private final ProjectModel projectModel;
    private final User user;
    private final Scanner scanner;

    public ProjectView(User user) {
        this.user = user;
        this.projectModel = new ProjectModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView case "4" ──────────────────────────────────────
    public void init() {
        System.out.println();
        System.out.println("=== Post Project Idea ===");
        projectModel.loadMyTeamsForProject(user);
    }

    // ── called from HomeView case "5" ──────────────────────────────────────
    public void initAddTechStack() {
        System.out.println();
        System.out.println("=== Add Tech Stack to Project ===");
        projectModel.loadMyProjects(user);
    }

    // ── called from HomeView case "6" ──────────────────────────────────────
    public void initUpdateStatus() {
        System.out.println();
        System.out.println("=== Update Project Status ===");
        projectModel.loadMyProjectsForStatus(user);
    }

    // ── called from HomeView case "7" ──────────────────────────────────────
    public void initViewMyProjects() {
        System.out.println();
        System.out.println("=== My Projects ===");
        projectModel.loadMyProjectsWithDetails(user);
    }

    // =========================================================================
    // Callbacks — ProjectModel calls these back
    // =========================================================================

    // ── Step 1 — show teams, developer picks which team ────────────────────
    void showMyTeamsForProject(List<Team> teams) {
        if (teams.isEmpty()) {
            System.out.println("You are not part of any team yet.");
            System.out.println("Create a team or accept an invitation first.");
            return;
        }

        System.out.println();
        System.out.println("Select which team this project belongs to:");
        System.out.println("──────────────────────────────────────────");
        for (Team team : teams) {
            System.out.println("Team ID   : " + team.getId());
            System.out.println("Team Name : " + team.getTeamName());
            System.out.println("──────────────────────────────────────────");
        }

        System.out.print("Enter Team ID for this project (or 0 to cancel): ");
        String teamIdInput = scanner.nextLine().trim();
        if (teamIdInput.equals("0")) return;

        // ── Step 2 — now ask project details ──────────────────────────────
        System.out.println();
        System.out.print("Enter project title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter project description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter domain (e.g. HealthTech, EdTech, FinTech): ");
        String domain = scanner.nextLine().trim();

        System.out.print("Enter estimated cost (e.g. 50000.00): ");
        String costInput = scanner.nextLine().trim();

        System.out.print("Enter timeline in days (e.g. 90): ");
        String timelineInput = scanner.nextLine().trim();

        projectModel.createProject(user, teamIdInput, title, description,
                domain, costInput, timelineInput);
    }

    void onProjectCreated(Project project, String teamName) {
        System.out.println();
        System.out.println("Project posted successfully!");
        System.out.println("Project ID  : " + project.getId());
        System.out.println("Title       : " + project.getTitle());
        System.out.println("Team        : " + teamName);
        System.out.println("Domain      : " + project.getDomain());
        System.out.println("Budget      : " + project.getEstimatedCost());
        System.out.println("Timeline    : " + project.getTimelineDays() + " days");
        System.out.println("Status      : " + project.getStatus());
    }

    void onProjectCreateFailed(String message) {
        System.out.println("Failed to post project: " + message);
    }

    void showMyProjectsForTechStack(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("You have no projects yet. Post a project first (option 4).");
            return;
        }
        System.out.println();
        System.out.println("Your Projects:");
        System.out.println("──────────────────────────────────");
        for (Project project : projects) {
            System.out.println("ID: " + project.getId()
                    + " | Title: " + project.getTitle()
                    + " | Status: " + project.getStatus());
        }
        System.out.println("──────────────────────────────────");
        System.out.print("Enter Project ID to add tech stack (or 0 to cancel): ");
        String projectIdInput = scanner.nextLine().trim();
        if (projectIdInput.equals("0")) return;
        System.out.print("Enter technology name (e.g. Java, React, MySQL): ");
        String technology = scanner.nextLine().trim();
        projectModel.addTechStack(user, projectIdInput, technology);
    }

    void onTechStackAdded(TechStack techStack) {
        System.out.println("Tech stack added: " + techStack.getTechnology());
    }

    void onTechStackFailed(String message) {
        System.out.println("Failed to add tech stack: " + message);
    }

    void showMyProjectsForStatus(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("You have no projects yet. Post a project first (option 4).");
            return;
        }
        System.out.println();
        System.out.println("Your Projects:");
        System.out.println("──────────────────────────────────");
        for (Project project : projects) {
            System.out.println("ID: " + project.getId()
                    + " | Title: " + project.getTitle()
                    + " | Current Status: " + project.getStatus());
        }
        System.out.println("──────────────────────────────────");
        System.out.print("Enter Project ID to update status (or 0 to cancel): ");
        String projectIdInput = scanner.nextLine().trim();
        if (projectIdInput.equals("0")) return;
        System.out.println();
        System.out.println("Select new status:");
        System.out.println("1. IDEA");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. COMPLETED");
        System.out.print("Choose (1, 2 or 3): ");
        String statusChoice = scanner.nextLine().trim();
        projectModel.updateProjectStatus(user, projectIdInput, statusChoice);
    }

    void onStatusUpdated(Project project) {
        System.out.println("Status updated successfully!");
        System.out.println("Title  : " + project.getTitle());
        System.out.println("Status : " + project.getStatus());
    }

    void onStatusUpdateFailed(String message) {
        System.out.println("Failed to update status: " + message);
    }

    void showMyProjectsWithDetails(List<Project> projects,
                                   List<TechStack> allTechStacks) {
        if (projects.isEmpty()) {
            System.out.println("You have no projects yet. Post a project first (option 4).");
            return;
        }
        System.out.println();
        for (Project project : projects) {
            System.out.println("══════════════════════════════════════════");
            System.out.println("Project ID  : " + project.getId());
            System.out.println("Title       : " + project.getTitle());
            System.out.println("Description : " + project.getDescription());
            System.out.println("Domain      : " + project.getDomain());
            System.out.println("Budget      : " + project.getEstimatedCost());
            System.out.println("Timeline    : " + project.getTimelineDays() + " days");
            System.out.println("Status      : " + project.getStatus());
            StringBuilder techList = new StringBuilder();
            for (TechStack tech : allTechStacks) {
                if (tech.getProjectId().equals(project.getId())) {
                    if (techList.length() > 0) techList.append(", ");
                    techList.append(tech.getTechnology());
                }
            }
            System.out.println("Tech Stack  : "
                    + (techList.length() > 0 ? techList.toString() : "Not added yet"));
        }
        System.out.println("══════════════════════════════════════════");
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
