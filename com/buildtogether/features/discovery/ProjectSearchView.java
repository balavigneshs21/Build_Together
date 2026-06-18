package com.buildtogether.features.discovery;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.TechStack;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class ProjectSearchView {

    private final ProjectSearchModel projectSearchModel;
    private final User user;
    private final Scanner scanner;

    public ProjectSearchView(User user) {
        this.user = user;
        this.projectSearchModel = new ProjectSearchModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView Investor case "2" ─────────────────────────────
    public void initViewAllProjects() {
        System.out.println();
        System.out.println("=== All Projects ===");
        projectSearchModel.loadAllProjectsOnly();
    }

    // ── called from HomeView Investor case "3" ─────────────────────────────
    public void initFilterProjects() {
        System.out.println();
        System.out.println("=== Filter Projects ===");
        System.out.println("1. Filter by domain");
        System.out.println("2. Filter by max budget");
        System.out.println("3. Filter by technology");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter domain (e.g. HealthTech, EdTech): ");
                String domain = scanner.nextLine().trim();
                projectSearchModel.filterByDomain(domain);
                break;
            case "2":
                System.out.print("Enter maximum budget: ");
                String budgetInput = scanner.nextLine().trim();
                projectSearchModel.filterByBudget(budgetInput);
                break;
            case "3":
                System.out.print("Enter technology (e.g. Java, React): ");
                String technology = scanner.nextLine().trim();
                projectSearchModel.filterByTechnology(technology);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ── called from HomeView Investor case "4" ─────────────────────────────
    // FIXED — first shows all projects, then asks project ID
    public void initViewTeamDetails() {
        System.out.println();
        System.out.println("=== View Team Details ===");
        projectSearchModel.loadAllProjectsForTeamDetails();
    }

    // =========================================================================
    // Callbacks — ProjectSearchModel calls these back
    // =========================================================================

    // ── shows projects only — no prompt ───────────────────────────────────
    void showProjectsOnly(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        System.out.println();
        System.out.println("Projects:");
        System.out.println("──────────────────────────────────────────");
        for (Project project : projects) {
            System.out.println("ID       : " + project.getId());
            System.out.println("Title    : " + project.getTitle());
            System.out.println("Domain   : " + project.getDomain());
            System.out.println("Budget   : " + project.getEstimatedCost());
            System.out.println("Timeline : " + project.getTimelineDays() + " days");
            System.out.println("Status   : " + project.getStatus());
            System.out.println("──────────────────────────────────────────");
        }
    }

    // ── shows projects then asks for project ID to view team ───────────────
    void showProjectsForTeamDetails(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        System.out.println();
        System.out.println("All Projects:");
        System.out.println("──────────────────────────────────────────");
        for (Project project : projects) {
            System.out.println("ID       : " + project.getId());
            System.out.println("Title    : " + project.getTitle());
            System.out.println("Domain   : " + project.getDomain());
            System.out.println("Budget   : " + project.getEstimatedCost());
            System.out.println("Timeline : " + project.getTimelineDays() + " days");
            System.out.println("Status   : " + project.getStatus());
            System.out.println("──────────────────────────────────────────");
        }

        // now ask for project ID — investor can see all IDs above
        System.out.print("Enter Project ID to view team details (or 0 to go back): ");
        String projectIdInput = scanner.nextLine().trim();
        if (projectIdInput.equals("0")) return;
        projectSearchModel.loadTeamDetails(projectIdInput);
    }

    void showNoProjectsFound() {
        System.out.println("No projects found matching your filter.");
    }

    // ── Team details — full details with member names ──────────────────────
    void showTeamDetails(Team team, List<TeamMember> members,
                         List<TechStack> techStacks,
                         List<Developer> developers,
                         List<User> users) {
        System.out.println();
        System.out.println("=== Team Details ===");
        System.out.println("Team Name : " + team.getTeamName());
        System.out.println("Team ID   : " + team.getId());
        System.out.println();

        System.out.println("Team Members:");
        System.out.println("──────────────────────────────────────────");
        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            for (TeamMember member : members) {
                for (Developer dev : developers) {
                    if (dev.getId().equals(member.getDeveloperId())) {
                        String name = "Unknown";
                        for (User u : users) {
                            if (u.getId().equals(dev.getUserId())) {
                                name = u.getName();
                                break;
                            }
                        }
                        String role = dev.getId().equals(team.getTeamLeaderId())
                                ? "Team Leader" : "Member";

                        System.out.println("Developer ID : " + dev.getId());
                        System.out.println("Name         : " + name);
                        System.out.println("Skills       : " + dev.getSkills());
                        System.out.println("Experience   : " + dev.getExperienceLevel());
                        System.out.println("Role         : " + role);
                        System.out.println("──────────────────────────────────────────");
                    }
                }
            }
        }

        System.out.println("Tech Stack:");
        System.out.println("──────────────────────────────────────────");
        if (techStacks.isEmpty()) {
            System.out.println("No tech stack added yet.");
        } else {
            for (TechStack tech : techStacks) {
                System.out.println("- " + tech.getTechnology());
            }
        }
        System.out.println("──────────────────────────────────────────");
    }

    void showTeamNotFound() {
        System.out.println("No team found for this project.");
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
