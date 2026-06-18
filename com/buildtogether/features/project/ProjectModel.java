package com.buildtogether.features.project;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Investment;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.TechStack;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.ArrayList;
import java.util.List;

class ProjectModel {

    private final ProjectView projectView;

    ProjectModel(ProjectView projectView) {
        this.projectView = projectView;
    }

    // =========================================================================
    // Step 1 — load all teams this developer belongs to
    // developer picks which team the project belongs to
    // =========================================================================

    void loadMyTeamsForProject(User user) {

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            projectView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        // get ALL accepted memberships for this developer
        List<TeamMember> memberships = BuildTogetherDB.getInstance()
                .getAcceptedMembershipsByDeveloperId(developer.getId());

        if (memberships.isEmpty()) {
            projectView.showError(
                    "You are not part of any team yet."
                    + " Create a team (option 2) or accept an invitation (option 12) first.");
            return;
        }

        // collect Team objects
        List<Team> myTeams = new ArrayList<>();
        for (TeamMember membership : memberships) {
            Team team = BuildTogetherDB.getInstance()
                    .getTeamById(membership.getTeamId());
            if (team != null) myTeams.add(team);
        }

        projectView.showMyTeamsForProject(myTeams);
    }

    // =========================================================================
    // Step 2 — create project linked to the chosen team
    // =========================================================================

    void createProject(User user, String teamIdInput, String title,
                       String description, String domain,
                       String costInput, String timelineInput) {

        // validate title
        if (title == null || title.isEmpty()) {
            projectView.onProjectCreateFailed("Title cannot be empty");
            return;
        }
        // validate description
        if (description == null || description.isEmpty()) {
            projectView.onProjectCreateFailed("Description cannot be empty");
            return;
        }
        // validate domain
        if (domain == null || domain.isEmpty()) {
            projectView.onProjectCreateFailed("Domain cannot be empty");
            return;
        }

        // parse cost
        Double estimatedCost;
        try {
            estimatedCost = Double.parseDouble(costInput);
            if (estimatedCost <= 0) {
                projectView.onProjectCreateFailed("Cost must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            projectView.onProjectCreateFailed("Invalid cost. Please enter a number.");
            return;
        }

        // parse timeline
        Integer timelineDays;
        try {
            timelineDays = Integer.parseInt(timelineInput);
            if (timelineDays <= 0) {
                projectView.onProjectCreateFailed("Timeline must be greater than 0 days");
                return;
            }
        } catch (NumberFormatException e) {
            projectView.onProjectCreateFailed("Invalid timeline. Please enter a number.");
            return;
        }

        // parse team ID
        Long teamId;
        try {
            teamId = Long.parseLong(teamIdInput);
        } catch (NumberFormatException e) {
            projectView.onProjectCreateFailed("Invalid team ID. Please enter a number.");
            return;
        }

        // check team exists
        Team team = BuildTogetherDB.getInstance().getTeamById(teamId);
        if (team == null) {
            projectView.onProjectCreateFailed("No team found with ID: " + teamId);
            return;
        }

        // check developer is actually a member of this team
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            projectView.onProjectCreateFailed(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        boolean isMember = BuildTogetherDB.getInstance()
                .isAcceptedMember(teamId, developer.getId());
        if (!isMember) {
            projectView.onProjectCreateFailed(
                    "You are not a member of team: " + team.getTeamName());
            return;
        }

        // save project
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setTeamId(teamId);
        project.setDomain(domain);
        project.setEstimatedCost(estimatedCost);
        project.setTimelineDays(timelineDays);

        Project saved = BuildTogetherDB.getInstance().addProject(project);
        if (saved != null) {
            projectView.onProjectCreated(saved, team.getTeamName());
        } else {
            projectView.onProjectCreateFailed("Something went wrong. Please try again.");
        }
    }

    // =========================================================================
    // Load my projects — for tech stack screen
    // shows projects from ALL teams developer belongs to
    // =========================================================================

    void loadMyProjects(User user) {
        List<Project> projects = getAllMyProjects(user);
        if (projects == null) return;
        projectView.showMyProjectsForTechStack(projects);
    }

    // =========================================================================
    // Load my projects — for status update screen
    // shows projects from ALL teams developer belongs to
    // =========================================================================

    void loadMyProjectsForStatus(User user) {
        List<Project> projects = getAllMyProjects(user);
        if (projects == null) return;
        projectView.showMyProjectsForStatus(projects);
    }

    // =========================================================================
    // Load my projects with full details + tech stack
    // shows projects from ALL teams developer belongs to
    // =========================================================================

    void loadMyProjectsWithDetails(User user) {
        List<Project> projects = getAllMyProjects(user);
        if (projects == null) return;

        // collect all tech stacks for all projects
        List<TechStack> allTechStacks = new ArrayList<>();
        for (Project project : projects) {
            allTechStacks.addAll(BuildTogetherDB.getInstance()
                    .getTechStackByProjectId(project.getId()));
        }

        projectView.showMyProjectsWithDetails(projects, allTechStacks);
    }

    // =========================================================================
    // Helper — get all projects from ALL teams developer belongs to
    // =========================================================================

    private List<Project> getAllMyProjects(User user) {
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            projectView.showError(
                    "Please complete your developer profile first (option 1)");
            return null;
        }

        // get all accepted team memberships
        List<TeamMember> memberships = BuildTogetherDB.getInstance()
                .getAcceptedMembershipsByDeveloperId(developer.getId());

        if (memberships.isEmpty()) {
            projectView.showError(
                    "You are not part of any team yet."
                    + " Create a team or accept an invitation first.");
            return null;
        }

        // collect projects from ALL teams
        List<Project> allProjects = new ArrayList<>();
        for (TeamMember membership : memberships) {
            allProjects.addAll(BuildTogetherDB.getInstance()
                    .getProjectsByTeamId(membership.getTeamId()));
        }

        return allProjects;
    }

    // =========================================================================
    // Add tech stack
    // =========================================================================

    void addTechStack(User user, String projectIdInput, String technology) {

        if (technology == null || technology.isEmpty()) {
            projectView.onTechStackFailed("Technology name cannot be empty");
            return;
        }

        Long projectId;
        try {
            projectId = Long.parseLong(projectIdInput);
        } catch (NumberFormatException e) {
            projectView.onTechStackFailed("Invalid project ID. Please enter a number.");
            return;
        }

        Project project = BuildTogetherDB.getInstance().getProjectById(projectId);
        if (project == null) {
            projectView.onTechStackFailed("No project found with ID: " + projectId);
            return;
        }

        // check developer is member of the project's team
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            projectView.onTechStackFailed(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        boolean isMember = BuildTogetherDB.getInstance()
                .isAcceptedMember(project.getTeamId(), developer.getId());
        if (!isMember) {
            projectView.onTechStackFailed(
                    "This project does not belong to any of your teams");
            return;
        }

        TechStack techStack = new TechStack();
        techStack.setProjectId(projectId);
        techStack.setTechnology(technology);

        TechStack saved = BuildTogetherDB.getInstance().addTechStack(techStack);
        if (saved != null) {
            projectView.onTechStackAdded(saved);
        } else {
            projectView.onTechStackFailed("Something went wrong. Please try again.");
        }
    }

    // =========================================================================
    // Update project status
    // =========================================================================

    void updateProjectStatus(User user, String projectIdInput, String statusChoice) {

        Long projectId;
        try {
            projectId = Long.parseLong(projectIdInput);
        } catch (NumberFormatException e) {
            projectView.onStatusUpdateFailed("Invalid project ID. Please enter a number.");
            return;
        }

        Project project = BuildTogetherDB.getInstance().getProjectById(projectId);
        if (project == null) {
            projectView.onStatusUpdateFailed("No project found with ID: " + projectId);
            return;
        }

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            projectView.onStatusUpdateFailed(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        // check developer is member of the project's team
        boolean isMember = BuildTogetherDB.getInstance()
                .isAcceptedMember(project.getTeamId(), developer.getId());
        if (!isMember) {
            projectView.onStatusUpdateFailed(
                    "This project does not belong to any of your teams");
            return;
        }

        Project.ProjectStatus newStatus;
        switch (statusChoice) {
            case "1": newStatus = Project.ProjectStatus.IDEA; break;
            case "2": newStatus = Project.ProjectStatus.IN_PROGRESS; break;
            case "3": newStatus = Project.ProjectStatus.COMPLETED; break;
            default:
                projectView.onStatusUpdateFailed("Invalid choice. Please choose 1, 2 or 3.");
                return;
        }

        project.setStatus(newStatus);
        Project updated = BuildTogetherDB.getInstance().updateProject(project);

        if (updated != null) {
            if (newStatus == Project.ProjectStatus.COMPLETED) {
                notifyInvestorsOnCompletion(project);
            }
            projectView.onStatusUpdated(updated);
        } else {
            projectView.onStatusUpdateFailed("Something went wrong. Please try again.");
        }
    }

    // =========================================================================
    // Helper — notify all investors when project is COMPLETED
    // =========================================================================

    private void notifyInvestorsOnCompletion(Project project) {
        List<Investment> investments = BuildTogetherDB.getInstance()
                .getInvestmentsByProjectId(project.getId());
        for (Investment investment : investments) {
            Investor investor = BuildTogetherDB.getInstance()
                    .getInvestorById(investment.getInvestorId());
            if (investor != null) {
                User investorUser = BuildTogetherDB.getInstance()
                        .getUserById(investor.getUserId());
                if (investorUser != null) {
                    BuildTogetherDB.getInstance().addNotification(
                            investorUser.getId(),
                            "[PROJECT COMPLETED] The project '"
                            + project.getTitle()
                            + "' that you invested in has been COMPLETED!"
                            + " Your investment: " + investment.getAmount()
                            + ". Please mark your payment as done (Track investments).");
                }
            }
        }
    }
}
