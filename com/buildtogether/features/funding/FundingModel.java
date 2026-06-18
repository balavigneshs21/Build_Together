package com.buildtogether.features.funding;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.FundingApplication;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.ArrayList;
import java.util.List;

class FundingModel {

    private final FundingView fundingView;

    FundingModel(FundingView fundingView) {
        this.fundingView = fundingView;
    }

    // =========================================================================
    // Load developer's projects + all investors — for apply screen
    // now gets projects from ALL teams developer belongs to
    // =========================================================================

    void loadMyProjectsAndInvestors(User user) {

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            fundingView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        List<Project> projects = getAllMyProjects(user, developer);
        if (projects == null) return;

        if (projects.isEmpty()) {
            fundingView.showError(
                    "You have no projects yet. Post a project first (option 4)");
            return;
        }

        List<Investor> investors = BuildTogetherDB.getInstance().getAllInvestors();
        fundingView.showProjectsAndInvestors(projects, investors);
    }

    // =========================================================================
    // Load MY funding applications
    // now gets applications from ALL teams developer belongs to
    // =========================================================================

    void loadMyApplications(User user) {

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            fundingView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        List<Project> myProjects = getAllMyProjects(user, developer);
        if (myProjects == null) return;

        // collect all funding applications for ALL my projects
        List<FundingApplication> myApplications = new ArrayList<>();
        for (Project project : myProjects) {
            myApplications.addAll(BuildTogetherDB.getInstance()
                    .getFundingApplicationsByProjectId(project.getId()));
        }

        List<Investor> investors = BuildTogetherDB.getInstance().getAllInvestors();
        List<Project> allProjects = BuildTogetherDB.getInstance().getAllProjects();

        fundingView.showMyApplications(myApplications, allProjects, investors);
    }

    // =========================================================================
    // Apply for funding
    // checks developer is member of the project's team (not just leader)
    // =========================================================================

    void applyForFunding(User user, String projectIdInput,
                         String investorIdInput, String amountInput) {

        // Step 1 — parse project ID
        Long projectId;
        try {
            projectId = Long.parseLong(projectIdInput);
        } catch (NumberFormatException e) {
            fundingView.onApplicationFailed(
                    "Invalid project ID. Please enter a number.");
            return;
        }

        // Step 2 — parse investor ID
        Long investorId;
        try {
            investorId = Long.parseLong(investorIdInput);
        } catch (NumberFormatException e) {
            fundingView.onApplicationFailed(
                    "Invalid investor ID. Please enter a number.");
            return;
        }

        // Step 3 — parse amount
        Double requestedAmount;
        try {
            requestedAmount = Double.parseDouble(amountInput);
            if (requestedAmount <= 0) {
                fundingView.onApplicationFailed(
                        "Requested amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            fundingView.onApplicationFailed(
                    "Invalid amount. Please enter a number.");
            return;
        }

        // Step 4 — check project exists
        Project project = BuildTogetherDB.getInstance().getProjectById(projectId);
        if (project == null) {
            fundingView.onApplicationFailed(
                    "No project found with ID: " + projectId);
            return;
        }

        // Step 5 — check developer profile exists
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            fundingView.onApplicationFailed(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        // Step 6 — check developer is ACCEPTED member of the project's team
        boolean isMember = BuildTogetherDB.getInstance()
                .isAcceptedMember(project.getTeamId(), developer.getId());
        if (!isMember) {
            fundingView.onApplicationFailed(
                    "This project does not belong to any of your teams");
            return;
        }

        // Step 7 — check investor exists
        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorById(investorId);
        if (investor == null) {
            fundingView.onApplicationFailed(
                    "No investor found with ID: " + investorId);
            return;
        }

        // Step 8 — check no duplicate pending application
        boolean alreadyApplied = BuildTogetherDB.getInstance()
                .hasPendingApplication(projectId, investorId);
        if (alreadyApplied) {
            fundingView.onApplicationFailed(
                    "You already have a pending application for this"
                    + " project with this investor");
            return;
        }

        // Step 9 — check amount does not exceed investor funds
        if (requestedAmount > investor.getAvailableFunds()) {
            fundingView.onApplicationFailed(
                    "Requested amount exceeds investor's available funds ("
                    + investor.getAvailableFunds() + ")");
            return;
        }

        // Step 10 — save application
        FundingApplication application = new FundingApplication();
        application.setProjectId(projectId);
        application.setInvestorId(investorId);
        application.setRequestedAmount(requestedAmount);

        FundingApplication saved = BuildTogetherDB.getInstance()
                .addFundingApplication(application);

        if (saved != null) {
            fundingView.onApplicationSuccess(saved);
        } else {
            fundingView.onApplicationFailed(
                    "Something went wrong. Please try again.");
        }
    }

    // =========================================================================
    // Helper — get all projects from ALL teams developer belongs to
    // same logic as ProjectModel — consistent across features
    // =========================================================================

    private List<Project> getAllMyProjects(User user, Developer developer) {

        // get all ACCEPTED memberships for this developer
        List<TeamMember> memberships = BuildTogetherDB.getInstance()
                .getAcceptedMembershipsByDeveloperId(developer.getId());

        if (memberships.isEmpty()) {
            fundingView.showError(
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
}
