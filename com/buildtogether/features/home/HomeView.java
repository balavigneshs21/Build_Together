package com.buildtogether.features.home;

import com.buildtogether.dto.User;
import com.buildtogether.features.contact.ContactView;
import com.buildtogether.features.developer.DeveloperView;
import com.buildtogether.features.discovery.ProjectSearchView;
import com.buildtogether.features.funding.FundingView;
import com.buildtogether.features.investors.InvestorView;
import com.buildtogether.features.notifications.NotificationsView;
import com.buildtogether.features.project.ProjectView;
import com.buildtogether.features.team.TeamView;
import com.buildtogether.repository.BuildTogetherDB;
import com.buildtogether.util.ConsoleInput;

import java.util.Scanner;

public class HomeView {

    private final HomeModel homeModel;
    private final User user;
    private final Scanner scanner;

    public HomeView(User user) {
        this.homeModel = new HomeModel(this);
        this.user = user;
        this.scanner = ConsoleInput.getScanner();
    }

    public void init() {
        homeModel.init(user);
    }

    void showUnauthorized() {
        System.out.println("Your account role is not set. Please contact support.");
    }

    // =========================================================================
    // DEVELOPER MENU
    // =========================================================================

    void showDeveloperMenu() {
        while (true) {
            long unread = BuildTogetherDB.getInstance()
                    .countUnreadNotifications(user.getId());
            String notifBadge = unread > 0 ? " (" + unread + " new)" : "";

            long pendingInvites = BuildTogetherDB.getInstance()
                    .countPendingInvitations(user.getId());
            String inviteBadge = pendingInvites > 0
                    ? " (" + pendingInvites + " pending)" : "";

            System.out.println();
            System.out.println("=== Developer Home — " + user.getName() + " ===");
            System.out.println("1.  My profile");
            System.out.println("2.  Create team");
            System.out.println("3.  Manage team members");
            System.out.println("4.  Post project idea");
            System.out.println("5.  Add tech stack to project");
            System.out.println("6.  Update project status");
            System.out.println("7.  View my projects");
            System.out.println("8.  Search investors");
            System.out.println("9.  Apply for funding");
            System.out.println("10. My funding applications");
            System.out.println("11. Messages from investors");
            System.out.println("12. My invitations" + inviteBadge);
            System.out.println("13. My teams");
            System.out.println("14. Notifications" + notifBadge);
            System.out.println("15. Sign out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    new DeveloperView(user).initViewOrCreate();
                    break;
                case "2":
                    new TeamView(user).init();
                    break;
                case "3":
                    new TeamView(user).initManageMembers();
                    break;
                case "4":
                    new ProjectView(user).init();
                    break;
                case "5":
                    new ProjectView(user).initAddTechStack();
                    break;
                case "6":
                    new ProjectView(user).initUpdateStatus();
                    break;
                case "7":
                    new ProjectView(user).initViewMyProjects();
                    break;
                case "8":
                    new InvestorView(user).initViewAllInvestors();
                    break;
                case "9":
                    new FundingView(user).init();
                    break;
                case "10":
                    new FundingView(user).initMyApplications();
                    break;
                case "11":
                    new ContactView(user).initViewIncomingMessages();
                    break;
                case "12":
                    new TeamView(user).initMyInvitations();
                    break;
                case "13":
                    new TeamView(user).initMyTeams();
                    break;
                case "14":
                    new NotificationsView(user).init();
                    break;
                case "15":
                    System.out.println("You have been signed out. Goodbye, "
                            + user.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // =========================================================================
    // INVESTOR MENU
    // =========================================================================

    void showInvestorMenu() {
        while (true) {
            long unread = BuildTogetherDB.getInstance()
                    .countUnreadNotifications(user.getId());
            String notifBadge = unread > 0 ? " (" + unread + " new)" : "";

            System.out.println();
            System.out.println("=== Investor Home — " + user.getName() + " ===");
            System.out.println("1.  My profile");
            System.out.println("2.  View all projects");
            System.out.println("3.  Filter projects");
            System.out.println("4.  View team details");
            System.out.println("5.  Contact team leader");
            System.out.println("6.  View funding applications");
            System.out.println("7.  Invest in project");
            System.out.println("8.  Track my investments");
            System.out.println("9.  Notifications" + notifBadge);
            System.out.println("10. Sign out");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    new InvestorView(user).initProfile();
                    break;
                case "2":
                    new ProjectSearchView(user).initViewAllProjects();
                    break;
                case "3":
                    new ProjectSearchView(user).initFilterProjects();
                    break;
                case "4":
                    new ProjectSearchView(user).initViewTeamDetails();
                    break;
                case "5":
                    new ContactView(user).init();
                    break;
                case "6":
                    new InvestorView(user).initViewFundingApplications();
                    break;
                case "7":
                    new InvestorView(user).initInvest();
                    break;
                case "8":
                    new InvestorView(user).initTrackInvestments();
                    break;
                case "9":
                    new NotificationsView(user).init();
                    break;
                case "10":
                    System.out.println("You have been signed out. Goodbye, "
                            + user.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
