package com.buildtogether.features.funding;

import com.buildtogether.dto.FundingApplication;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class FundingView {

    private final FundingModel fundingModel;
    private final User user;
    private final Scanner scanner;

    public FundingView(User user) {
        this.user = user;
        this.fundingModel = new FundingModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView Developer case "8" ────────────────────────────
    public void init() {
        System.out.println();
        System.out.println("=== Apply for Funding ===");
        fundingModel.loadMyProjectsAndInvestors(user);
    }

    // ── called from HomeView Developer case "9" ────────────────────────────
    public void initMyApplications() {
        System.out.println();
        System.out.println("=== My Funding Applications ===");
        fundingModel.loadMyApplications(user);
    }

    // =========================================================================
    // Callbacks — FundingModel calls these back
    // =========================================================================

    void showProjectsAndInvestors(List<Project> projects, List<Investor> investors) {

        if (projects.isEmpty()) {
            System.out.println("You have no projects yet.");
            System.out.println("Please post a project first (option 4).");
            return;
        }

        System.out.println();
        System.out.println("Your Projects:");
        System.out.println("──────────────────────────────────────────");
        for (Project project : projects) {
            System.out.println("ID     : " + project.getId());
            System.out.println("Title  : " + project.getTitle());
            System.out.println("Budget : " + project.getEstimatedCost());
            System.out.println("Status : " + project.getStatus());
            System.out.println("──────────────────────────────────────────");
        }

        System.out.print("Enter Project ID to apply funding for (or 0 to cancel): ");
        String projectIdInput = scanner.nextLine().trim();
        if (projectIdInput.equals("0")) return;

        if (investors.isEmpty()) {
            System.out.println("No investors available at the moment.");
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

        System.out.print("Enter Investor ID to send funding request (or 0 to cancel): ");
        String investorIdInput = scanner.nextLine().trim();
        if (investorIdInput.equals("0")) return;

        System.out.print("Enter requested amount: ");
        String amountInput = scanner.nextLine().trim();

        fundingModel.applyForFunding(user, projectIdInput, investorIdInput, amountInput);
    }

    void onApplicationSuccess(FundingApplication application) {
        System.out.println();
        System.out.println("Funding application sent successfully!");
        System.out.println("Application ID : " + application.getId());
        System.out.println("Project ID     : " + application.getProjectId());
        System.out.println("Investor ID    : " + application.getInvestorId());
        System.out.println("Requested      : " + application.getRequestedAmount());
        System.out.println("Status         : " + application.getStatus());
    }

    void onApplicationFailed(String message) {
        System.out.println("Funding application failed: " + message);
    }

    // ── My applications — shows all applications with full details ─────────
    void showMyApplications(List<FundingApplication> applications,
                            List<Project> projects,
                            List<Investor> investors) {
        if (applications.isEmpty()) {
            System.out.println("You have not applied for any funding yet.");
            return;
        }

        System.out.println();
        System.out.println("Your Funding Applications:");
        System.out.println("──────────────────────────────────────────");
        for (FundingApplication app : applications) {

            // find project title
            String projectTitle = "Unknown";
            for (Project project : projects) {
                if (project.getId().equals(app.getProjectId())) {
                    projectTitle = project.getTitle();
                    break;
                }
            }

            // find investor company name
            String companyName = "Unknown";
            for (Investor investor : investors) {
                if (investor.getId().equals(app.getInvestorId())) {
                    companyName = investor.getCompanyName();
                    break;
                }
            }

            System.out.println("Application ID  : " + app.getId());
            System.out.println("Project         : " + projectTitle);
            System.out.println("Investor        : " + companyName);
            System.out.println("Requested Amount: " + app.getRequestedAmount());
            System.out.println("Status          : " + app.getStatus());
            System.out.println("──────────────────────────────────────────");
        }
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
