package com.buildtogether.features.investors;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.FundingApplication;
import com.buildtogether.dto.Investment;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.List;

class InvestorModel {

    private final InvestorView investorView;

    InvestorModel(InvestorView investorView) {
        this.investorView = investorView;
    }

    void loadProfile(User user) {
        Investor existing = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (existing != null) {
            investorView.showExistingProfile(existing);
        } else {
            investorView.showProfileSetup();
        }
    }

    void createProfile(User user, String companyName, String fundsInput) {
        if (companyName == null || companyName.isEmpty()) {
            investorView.onProfileFailed("Company name cannot be empty");
            return;
        }
        Double availableFunds;
        try {
            availableFunds = Double.parseDouble(fundsInput);
            if (availableFunds <= 0) {
                investorView.onProfileFailed("Available funds must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            investorView.onProfileFailed("Invalid funds amount. Please enter a number.");
            return;
        }
        Investor existing = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (existing != null) {
            investorView.onProfileFailed("An investor profile already exists");
            return;
        }
        Investor investor = new Investor();
        investor.setUserId(user.getId());
        investor.setCompanyName(companyName);
        investor.setAvailableFunds(availableFunds);
        Investor saved = BuildTogetherDB.getInstance().addInvestor(investor);
        if (saved != null) {
            investorView.onProfileCreated(saved);
        } else {
            investorView.onProfileFailed("Something went wrong. Please try again.");
        }
    }

    void loadAllInvestors() {
        List<Investor> investors = BuildTogetherDB.getInstance().getAllInvestors();
        if (investors.isEmpty()) {
            investorView.showNoInvestorsFound();
            return;
        }
        investorView.showAllInvestors(investors);
    }

    void loadAllFundingApplications(User user) {
        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.showError(
                    "Please complete your investor profile first (option 1)");
            return;
        }
        List<FundingApplication> applications = BuildTogetherDB.getInstance()
                .getFundingApplicationsByInvestorId(investor.getId());
        List<Project> projects = BuildTogetherDB.getInstance().getAllProjects();
        investorView.showAllFundingApplications(applications, projects, investor);
    }

    // =========================================================================
    // APPROVE — investor.availableFunds >= application.requestedAmount
    // =========================================================================

    void approveFundingApplication(User user, String appIdInput) {

        Long appId;
        try {
            appId = Long.parseLong(appIdInput);
        } catch (NumberFormatException e) {
            investorView.onApproveFailed("Invalid ID. Please enter a number.");
            return;
        }

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.onApproveFailed("Investor profile not found");
            return;
        }

        List<FundingApplication> applications = BuildTogetherDB.getInstance()
                .getFundingApplicationsByInvestorId(investor.getId());
        FundingApplication targetApp = null;
        for (FundingApplication app : applications) {
            if (app.getId().equals(appId)) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            investorView.onApproveFailed("No application found with ID: " + appId);
            return;
        }
        if (targetApp.getStatus() != FundingApplication.ApplicationStatus.PENDING) {
            investorView.onApproveFailed(
                    "This application is already " + targetApp.getStatus());
            return;
        }

        // KEY CHECK — investor funds >= requested amount
        if (investor.getAvailableFunds() < targetApp.getRequestedAmount()) {
            investorView.onApproveFailed(
                    "Insufficient funds to approve!"
                    + "\n  Your available funds  : " + investor.getAvailableFunds()
                    + "\n  Developer requested   : " + targetApp.getRequestedAmount()
                    + "\n  You need at least     : " + targetApp.getRequestedAmount());
            return;
        }

        // approve + deduct funds
        targetApp.setStatus(FundingApplication.ApplicationStatus.APPROVED);
        BuildTogetherDB.getInstance().updateFundingApplication(targetApp);
        investor.setAvailableFunds(
                investor.getAvailableFunds() - targetApp.getRequestedAmount());

        // save investment record
        Investment investment = new Investment();
        investment.setInvestorId(investor.getId());
        investment.setProjectId(targetApp.getProjectId());
        investment.setAmount(targetApp.getRequestedAmount());
        BuildTogetherDB.getInstance().addInvestment(investment);

        // project → IN_PROGRESS
        Project project = BuildTogetherDB.getInstance()
                .getProjectById(targetApp.getProjectId());
        if (project != null) {
            project.setStatus(Project.ProjectStatus.IN_PROGRESS);
            BuildTogetherDB.getInstance().updateProject(project);

            // notify developer team leader
            User leaderUser = getTeamLeaderUser(project.getTeamId());
            if (leaderUser != null) {
                BuildTogetherDB.getInstance().addNotification(
                        leaderUser.getId(),
                        "[FUNDING APPROVED] Investor '"
                        + investor.getCompanyName()
                        + "' approved your funding request for project '"
                        + project.getTitle()
                        + "'. Amount funded: " + targetApp.getRequestedAmount()
                        + ". Project is now IN_PROGRESS — Start the work!");
            }
            investorView.onApproveSuccess(project.getTitle(),
                    targetApp.getRequestedAmount(), investor.getAvailableFunds());
        }
    }

    // =========================================================================
    // SUGGESTION 3 — REJECT funding application
    // =========================================================================

    void rejectFundingApplication(User user, String appIdInput) {

        // Step 1 — parse application ID
        Long appId;
        try {
            appId = Long.parseLong(appIdInput);
        } catch (NumberFormatException e) {
            investorView.onRejectFailed("Invalid ID. Please enter a number.");
            return;
        }

        // Step 2 — get investor profile
        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.onRejectFailed("Investor profile not found");
            return;
        }

        // Step 3 — find the application
        List<FundingApplication> applications = BuildTogetherDB.getInstance()
                .getFundingApplicationsByInvestorId(investor.getId());
        FundingApplication targetApp = null;
        for (FundingApplication app : applications) {
            if (app.getId().equals(appId)) {
                targetApp = app;
                break;
            }
        }
        if (targetApp == null) {
            investorView.onRejectFailed("No application found with ID: " + appId);
            return;
        }

        // Step 4 — check application is still PENDING
        if (targetApp.getStatus() != FundingApplication.ApplicationStatus.PENDING) {
            investorView.onRejectFailed(
                    "This application is already " + targetApp.getStatus());
            return;
        }

        // Step 5 — reject the application
        targetApp.setStatus(FundingApplication.ApplicationStatus.REJECTED);
        BuildTogetherDB.getInstance().updateFundingApplication(targetApp);

        // Step 6 — notify developer that application was rejected
        Project project = BuildTogetherDB.getInstance()
                .getProjectById(targetApp.getProjectId());
        if (project != null) {
            User leaderUser = getTeamLeaderUser(project.getTeamId());
            if (leaderUser != null) {
                BuildTogetherDB.getInstance().addNotification(
                        leaderUser.getId(),
                        "[FUNDING REJECTED] Investor '"
                        + investor.getCompanyName()
                        + "' has rejected your funding request for project '"
                        + project.getTitle()
                        + "'. Requested amount was: "
                        + targetApp.getRequestedAmount()
                        + ". You may apply to another investor.");
            }
            investorView.onRejectSuccess(project.getTitle());
        }
    }

    // =========================================================================
    // Direct invest — amount >= project budget
    // =========================================================================

    void loadAllProjectsForInvest(User user) {
        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.showError(
                    "Please complete your investor profile first (option 1)");
            return;
        }
        List<Project> projects = BuildTogetherDB.getInstance().getAllProjects();
        investorView.showAllProjectsForInvest(projects, investor);
    }

    void confirmDirectInvestment(User user, String projectIdInput, String amountInput) {

        Long projectId;
        try {
            projectId = Long.parseLong(projectIdInput);
        } catch (NumberFormatException e) {
            investorView.onInvestmentFailed("Invalid project ID. Please enter a number.");
            return;
        }

        Double amount;
        try {
            amount = Double.parseDouble(amountInput);
            if (amount <= 0) {
                investorView.onInvestmentFailed("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            investorView.onInvestmentFailed("Invalid amount. Please enter a number.");
            return;
        }

        Project project = BuildTogetherDB.getInstance().getProjectById(projectId);
        if (project == null) {
            investorView.onInvestmentFailed("No project found with ID: " + projectId);
            return;
        }

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.onInvestmentFailed("Investor profile not found");
            return;
        }

        // KEY CHECK 1 — amount >= project estimated cost
        if (amount < project.getEstimatedCost()) {
            investorView.onInvestmentFailed(
                    "Investment amount is less than project budget!"
                    + "\n  Project budget needed   : " + project.getEstimatedCost()
                    + "\n  Your entered amount     : " + amount
                    + "\n  You must invest at least: " + project.getEstimatedCost());
            return;
        }

        // KEY CHECK 2 — investor has enough funds
        if (amount > investor.getAvailableFunds()) {
            investorView.onInvestmentFailed(
                    "Insufficient funds in your account!"
                    + "\n  Your available funds : " + investor.getAvailableFunds()
                    + "\n  Amount you entered   : " + amount);
            return;
        }

        // deduct funds + save investment
        investor.setAvailableFunds(investor.getAvailableFunds() - amount);
        Investment investment = new Investment();
        investment.setInvestorId(investor.getId());
        investment.setProjectId(projectId);
        investment.setAmount(amount);
        Investment saved = BuildTogetherDB.getInstance().addInvestment(investment);

        // project → IN_PROGRESS
        project.setStatus(Project.ProjectStatus.IN_PROGRESS);
        BuildTogetherDB.getInstance().updateProject(project);

        // notify developer
        User leaderUser = getTeamLeaderUser(project.getTeamId());
        if (leaderUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    leaderUser.getId(),
                    "[INVESTOR INTEREST] Investor '"
                    + investor.getCompanyName()
                    + "' has invested " + amount
                    + " in your project '" + project.getTitle()
                    + "'. Project budget was: " + project.getEstimatedCost()
                    + ". Project is now IN_PROGRESS — Start the work!");
        }

        if (saved != null) {
            investorView.onInvestmentSuccess(saved, project.getTitle(),
                    investor.getAvailableFunds());
        } else {
            investorView.onInvestmentFailed("Something went wrong. Please try again.");
        }
    }

    void loadMyInvestments(User user) {
        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.showError(
                    "Please complete your investor profile first (option 1)");
            return;
        }
        List<Investment> investments = BuildTogetherDB.getInstance()
                .getInvestmentsByInvestorId(investor.getId());
        List<Project> projects = BuildTogetherDB.getInstance().getAllProjects();
        investorView.showMyInvestments(investments, projects);
    }

    void markPaymentDone(User user, String investmentIdInput) {

        Long investmentId;
        try {
            investmentId = Long.parseLong(investmentIdInput);
        } catch (NumberFormatException e) {
            investorView.onPaymentFailed("Invalid ID. Please enter a number.");
            return;
        }

        Investor investor = BuildTogetherDB.getInstance()
                .getInvestorByUserId(user.getId());
        if (investor == null) {
            investorView.onPaymentFailed("Investor profile not found");
            return;
        }

        List<Investment> investments = BuildTogetherDB.getInstance()
                .getInvestmentsByInvestorId(investor.getId());
        Investment target = null;
        for (Investment inv : investments) {
            if (inv.getId().equals(investmentId)) {
                target = inv;
                break;
            }
        }
        if (target == null) {
            investorView.onPaymentFailed("No investment found with ID: " + investmentId);
            return;
        }

        Project project = BuildTogetherDB.getInstance()
                .getProjectById(target.getProjectId());
        if (project == null) {
            investorView.onPaymentFailed("Project not found");
            return;
        }
        if (project.getStatus() != Project.ProjectStatus.COMPLETED) {
            investorView.onPaymentFailed(
                    "Cannot make payment — project '"
                    + project.getTitle()
                    + "' is not COMPLETED yet. Current status: "
                    + project.getStatus());
            return;
        }
        if (target.getPaymentStatus() == Investment.PaymentStatus.PAID) {
            investorView.onPaymentFailed("Payment already done for this investment.");
            return;
        }

        target.setPaymentStatus(Investment.PaymentStatus.PAID);
        BuildTogetherDB.getInstance().updateInvestment(target);

        // notify developer
        User leaderUser = getTeamLeaderUser(project.getTeamId());
        if (leaderUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    leaderUser.getId(),
                    "[PAYMENT RECEIVED] Investor '"
                    + investor.getCompanyName()
                    + "' has completed payment of " + target.getAmount()
                    + " for project '" + project.getTitle() + "'. Thank you!");
        }
        investorView.onPaymentSuccess(project.getTitle(), target.getAmount());
    }

    private User getTeamLeaderUser(Long teamId) {
        Team team = BuildTogetherDB.getInstance().getTeamById(teamId);
        if (team == null) return null;
        Developer leader = BuildTogetherDB.getInstance()
                .getDeveloperById(team.getTeamLeaderId());
        if (leader == null) return null;
        return BuildTogetherDB.getInstance().getUserById(leader.getUserId());
    }
}
