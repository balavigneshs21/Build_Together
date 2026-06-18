package com.buildtogether.features.discovery;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.TechStack;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.List;
import java.util.Locale;

class ProjectSearchModel {

    private final ProjectSearchView projectSearchView;

    ProjectSearchModel(ProjectSearchView projectSearchView) {
        this.projectSearchView = projectSearchView;
    }

    // =========================================================================
    // Load all projects — just list, no prompt (option 2)
    // =========================================================================

    void loadAllProjectsOnly() {
        List<Project> projects = BuildTogetherDB.getInstance().getAllProjects();
        if (projects.isEmpty()) {
            projectSearchView.showNoProjectsFound();
            return;
        }
        projectSearchView.showProjectsOnly(projects);
    }

    // =========================================================================
    // Load all projects — then ask project ID for team details (option 4)
    // =========================================================================

    void loadAllProjectsForTeamDetails() {
        List<Project> projects = BuildTogetherDB.getInstance().getAllProjects();
        if (projects.isEmpty()) {
            projectSearchView.showNoProjectsFound();
            return;
        }
        // passes to showProjectsForTeamDetails which shows list + asks for ID
        projectSearchView.showProjectsForTeamDetails(projects);
    }

    // =========================================================================
    // Filter by domain
    // =========================================================================

    void filterByDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            projectSearchView.showError("Domain cannot be empty");
            return;
        }
        List<Project> result = BuildTogetherDB.getInstance()
                .getProjectsByDomain(domain);
        if (result.isEmpty()) {
            projectSearchView.showNoProjectsFound();
        } else {
            projectSearchView.showProjectsOnly(result);
        }
    }

    // =========================================================================
    // Filter by budget
    // =========================================================================

    void filterByBudget(String budgetInput) {
        Double maxBudget;
        try {
            maxBudget = Double.parseDouble(budgetInput);
            if (maxBudget <= 0) {
                projectSearchView.showError("Budget must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            projectSearchView.showError("Invalid budget. Please enter a number.");
            return;
        }
        List<Project> result = BuildTogetherDB.getInstance()
                .getProjectsByMaxBudget(maxBudget);
        if (result.isEmpty()) {
            projectSearchView.showNoProjectsFound();
        } else {
            projectSearchView.showProjectsOnly(result);
        }
    }

    // =========================================================================
    // Filter by technology
    // =========================================================================

    void filterByTechnology(String technology) {
        if (technology == null || technology.isEmpty()) {
            projectSearchView.showError("Technology name cannot be empty");
            return;
        }
        List<Project> result = BuildTogetherDB.getInstance()
                .getProjectsByTechnology(technology);
        if (result.isEmpty()) {
            projectSearchView.showNoProjectsFound();
        } else {
            projectSearchView.showProjectsOnly(result);
        }
    }

    // =========================================================================
    // Load team details — by project ID
    // =========================================================================

    void loadTeamDetails(String projectIdInput) {

        Long projectId;
        try {
            projectId = Long.parseLong(projectIdInput);
        } catch (NumberFormatException e) {
            projectSearchView.showError(
                    "Invalid project ID. Please enter a number.");
            return;
        }

        Project project = BuildTogetherDB.getInstance().getProjectById(projectId);
        if (project == null) {
            projectSearchView.showError(
                    "No project found with ID: " + projectId);
            return;
        }

        Team team = BuildTogetherDB.getInstance()
                .getTeamById(project.getTeamId());
        if (team == null) {
            projectSearchView.showTeamNotFound();
            return;
        }

        List<TeamMember> members = BuildTogetherDB.getInstance()
                .getAcceptedMembersOfTeam(team.getId());

        List<TechStack> techStacks = BuildTogetherDB.getInstance()
                .getTechStackByProjectId(projectId);

        List<Developer> developers = BuildTogetherDB.getInstance()
                .getAllDevelopers();

        List<User> users = BuildTogetherDB.getInstance().getAllUsers();

        projectSearchView.showTeamDetails(
                team, members, techStacks, developers, users);
    }
}
