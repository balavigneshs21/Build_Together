package com.buildtogether.features.investors;

import com.buildtogether.dto.FundingApplication;
import com.buildtogether.dto.Investment;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class InvestorView {

    private final InvestorModel investorModel;
    private final User user;
    private final Scanner scanner;

    public InvestorView(User user) {
        this.user = user;
        this.investorModel = new InvestorModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    public void initProfile() {
        System.out.println();
        System.out.println("=== Investor Profile ===");
        investorModel.loadProfile(user);
    }

    public void initViewFundingApplications() {
        System.out.println();
        System.out.println("=== Funding Applications Received ===");
        investorModel.loadAllFundingApplications(user);
    }

    public void initInvest() {
        System.out.println();
        System.out.println("=== Invest in a Project ===");
        investorModel.loadAllProjectsForInvest(user);
    }

    public void initTrackInvestments() {
        System.out.println();
        System.out.println("=== My Investments ===");
        investorModel.loadMyInvestments(user);
    }

    public void initViewAllInvestors() {
        System.out.println();
        System.out.println("=== All Investors ===");
        investorModel.loadAllInvestors();
    }

    // =========================================================================
    // Callbacks
    // =========================================================================

    void showProfileSetup() {
        System.out.print("Enter your company name: ");
        String companyName = scanner.nextLine().trim();
        System.out.print("Enter your available funds: ");
        String fundsInput = scanner.nextLine().trim();
        investorModel.createProfile(user, companyName, fundsInput);
    }

    void showExistingProfile(Investor investor) {
        System.out.println();
        System.out.println("=== Your Investor Profile ===");
        System.out.println("Company        : " + investor.getCompanyName());
        System.out.println("Available Funds: " + investor.getAvailableFunds());
    }

    void onProfileCreated(Investor investor) {
        System.out.println();
        System.out.println("Profile created successfully!");
        System.out.println("Company        : " + investor.getCompanyName());
        System.out.println("Available Funds: " + investor.getAvailableFunds());
    }

    void onProfileFailed(String message) {
        System.out.println("Profile creation failed: " + message);
    }

    void showAllInvestors(List<Investor> investors) {
        if (investors.isEmpty()) {
            System.out.println("No investors found.");
            return;
        }
        System.out.println();
        System.out.println("Available Investors:");
        System.out.println("──────────────────────────────────────────");
        for (Investor investor : investors) {
            System.out.println("ID             : " + investor.getId());
            System.out.println("Company        : " + investor.getCompanyName());
            System.out.println("Available Funds: " + investor.getAvailableFunds());
            System.out.println("──────────────────────────────────────────");
        }
    }

    void showNoInvestorsFound() {
        System.out.println("No investors found.");
    }

    // ── SUGGESTION 3 — approve OR reject funding applications ─────────────
    void showAllFundingApplications(List<FundingApplication> applications,
                                    List<Project> projects,
                                    Investor investor) {
        if (applications.isEmpty()) {
            System.out.println("No funding applications received yet.");
            return;
        }

        System.out.println();
        System.out.println("Your Available Funds: " + investor.getAvailableFunds());
        System.out.println();
        System.out.println("Funding Applications:");
        System.out.println("──────────────────────────────────────────");
        for (FundingApplication app : applications) {
            String projectTitle = "Unknown";
            Double projectBudget = 0.0;
            for (Project project : projects) {
                if (project.getId().equals(app.getProjectId())) {
                    projectTitle = project.getTitle();
                    projectBudget = project.getEstimatedCost();
                    break;
                }
            }
            String canAfford = investor.getAvailableFunds()
                    >= app.getRequestedAmount() ? "YES" : "NO (insufficient funds)";

            System.out.println("Application ID  : " + app.getId());
            System.out.println("Project         : " + projectTitle);
            System.out.println("Project Budget  : " + projectBudget);
            System.out.println("Requested Amount: " + app.getRequestedAmount());
            System.out.println("Can You Afford  : " + canAfford);
            System.out.println("Status          : " + app.getStatus());
            System.out.println("──────────────────────────────────────────");
        }

        System.out.println();
        System.out.println("1. Approve a PENDING application");
        System.out.println("2. Reject a PENDING application");
        System.out.println("3. Go back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter Application ID to approve: ");
                String approveId = scanner.nextLine().trim();
                investorModel.approveFundingApplication(user, approveId);
                break;
            case "2":
                System.out.print("Enter Application ID to reject: ");
                String rejectId = scanner.nextLine().trim();
                investorModel.rejectFundingApplication(user, rejectId);
                break;
            case "3":
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    void onApproveSuccess(String projectTitle, Double amount, Double remainingFunds) {
        System.out.println();
        System.out.println("Funding approved successfully!");
        System.out.println("Project         : " + projectTitle);
        System.out.println("Amount Funded   : " + amount);
        System.out.println("Remaining Funds : " + remainingFunds);
        System.out.println("Project Status  : IN_PROGRESS");
        System.out.println("Developer has been notified to start work.");
    }

    void onApproveFailed(String message) {
        System.out.println("Approval failed: " + message);
    }

    // SUGGESTION 3 — reject callbacks
    void onRejectSuccess(String projectTitle) {
        System.out.println();
        System.out.println("Funding application rejected.");
        System.out.println("Project   : " + projectTitle);
        System.out.println("Developer has been notified.");
    }

    void onRejectFailed(String message) {
        System.out.println("Rejection failed: " + message);
    }

    void showAllProjectsForInvest(List<Project> projects, Investor investor) {
        if (projects.isEmpty()) {
            System.out.println("No projects available to invest in.");
            return;
        }
        System.out.println();
        System.out.println("Your Available Funds: " + investor.getAvailableFunds());
        System.out.println();
        System.out.println("Available Projects:");
        System.out.println("──────────────────────────────────────────");
        for (Project project : projects) {
            String canAfford = investor.getAvailableFunds()
                    >= project.getEstimatedCost() ? "YES" : "NO (insufficient funds)";
            System.out.println("ID            : " + project.getId());
            System.out.println("Title         : " + project.getTitle());
            System.out.println("Domain        : " + project.getDomain());
            System.out.println("Budget Needed : " + project.getEstimatedCost());
            System.out.println("Timeline      : " + project.getTimelineDays() + " days");
            System.out.println("Status        : " + project.getStatus());
            System.out.println("Can You Afford: " + canAfford);
            System.out.println("──────────────────────────────────────────");
        }
        System.out.print("Enter Project ID to invest (or 0 to cancel): ");
        String projectIdInput = scanner.nextLine().trim();
        if (projectIdInput.equals("0")) return;
        System.out.print("Enter amount (must be >= project budget): ");
        String amountInput = scanner.nextLine().trim();
        investorModel.confirmDirectInvestment(user, projectIdInput, amountInput);
    }

    void onInvestmentSuccess(Investment investment, String projectTitle,
                              Double remainingFunds) {
        System.out.println();
        System.out.println("Investment confirmed successfully!");
        System.out.println("Investment ID  : " + investment.getId());
        System.out.println("Project        : " + projectTitle);
        System.out.println("Amount Invested: " + investment.getAmount());
        System.out.println("Remaining Funds: " + remainingFunds);
        System.out.println("Project Status : IN_PROGRESS");
        System.out.println("Developer has been notified to start work.");
    }

    void onInvestmentFailed(String message) {
        System.out.println("Investment failed: " + message);
    }

    void showMyInvestments(List<Investment> investments, List<Project> projects) {
        if (investments.isEmpty()) {
            System.out.println("You have not made any investments yet.");
            return;
        }
        System.out.println();
        System.out.println("Your Investments:");
        System.out.println("──────────────────────────────────────────");
        for (Investment investment : investments) {
            String projectTitle = "Unknown";
            String projectStatus = "Unknown";
            for (Project project : projects) {
                if (project.getId().equals(investment.getProjectId())) {
                    projectTitle = project.getTitle();
                    projectStatus = project.getStatus().toString();
                    break;
                }
            }
            System.out.println("Investment ID  : " + investment.getId());
            System.out.println("Project        : " + projectTitle);
            System.out.println("Amount Invested: " + investment.getAmount());
            System.out.println("Project Status : " + projectStatus);
            System.out.println("Payment Status : " + investment.getPaymentStatus());
            System.out.println("──────────────────────────────────────────");
        }
        System.out.println();
        System.out.println("1. Mark payment done for a completed project");
        System.out.println("2. Go back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            System.out.print("Enter Investment ID to mark payment done: ");
            String investmentIdInput = scanner.nextLine().trim();
            investorModel.markPaymentDone(user, investmentIdInput);
        }
    }

    void onPaymentSuccess(String projectTitle, Double amount) {
        System.out.println();
        System.out.println("Payment marked as DONE!");
        System.out.println("Project : " + projectTitle);
        System.out.println("Amount  : " + amount);
        System.out.println("Developer has been notified about your payment.");
    }

    void onPaymentFailed(String message) {
        System.out.println("Payment failed: " + message);
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
