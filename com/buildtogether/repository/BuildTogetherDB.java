package com.buildtogether.repository;

import com.buildtogether.dto.ContactRequest;
import com.buildtogether.dto.Developer;
import com.buildtogether.dto.FundingApplication;
import com.buildtogether.dto.Investment;
import com.buildtogether.dto.Investor;
import com.buildtogether.dto.Notification;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.TechStack;
import com.buildtogether.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuildTogetherDB {

    private BuildTogetherDB() {}
    private static BuildTogetherDB instance = null;
    public static BuildTogetherDB getInstance() {
        if (instance == null) instance = new BuildTogetherDB();
        return instance;
    }

    private final List<User>               users               = new ArrayList<>();
    private final List<Developer>          developers          = new ArrayList<>();
    private final List<Investor>           investors           = new ArrayList<>();
    private final List<Team>               teams               = new ArrayList<>();
    private final List<TeamMember>         teamMembers         = new ArrayList<>();
    private final List<Project>            projects            = new ArrayList<>();
    private final List<TechStack>          techStacks          = new ArrayList<>();
    private final List<FundingApplication> fundingApplications = new ArrayList<>();
    private final List<Investment>         investments         = new ArrayList<>();
    private final List<ContactRequest>     contactRequests     = new ArrayList<>();
    private final List<Notification>       notifications       = new ArrayList<>();

    private long userPk = 0L, developerPk = 0L, investorPk = 0L, teamPk = 0L;
    private long teamMemberPk = 0L, projectPk = 0L, techStackPk = 0L;
    private long fundingApplicationPk = 0L, investmentPk = 0L;
    private long contactRequestPk = 0L, notificationPk = 0L;

    // =========================================================================
    // USER
    // =========================================================================
    public User addUser(User user) {
        if (user == null) return null;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return null;
        userPk++;
        user.setId(userPk);
        if (user.getCreatedAt() == null) user.setCreatedAt(System.currentTimeMillis());
        if (user.getStatus() == null) user.setStatus(User.UserStatus.ACTIVE);
        users.add(user);
        return user;
    }
    public User getUserByEmail(String email) {
        if (email == null) return null;
        String key = email.trim().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) return null;
        for (User u : users)
            if (u.getEmail() != null && u.getEmail().trim().toLowerCase(Locale.ROOT).equals(key)) return u;
        return null;
    }
    public User getUserById(Long id) {
        if (id == null) return null;
        for (User u : users) if (id.equals(u.getId())) return u;
        return null;
    }
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) return null;
        if (password == null || !password.equals(user.getPassword())) return null;
        return user;
    }
    public boolean isEmailTaken(String email) { return getUserByEmail(email) != null; }
    public List<User> getAllUsers() { return new ArrayList<>(users); }

    // =========================================================================
    // DEVELOPER
    // =========================================================================
    public Developer addDeveloper(Developer developer) {
        if (developer == null) return null;
        developerPk++;
        developer.setId(developerPk);
        developers.add(developer);
        return developer;
    }
    public Developer getDeveloperByUserId(Long userId) {
        if (userId == null) return null;
        for (Developer d : developers) if (userId.equals(d.getUserId())) return d;
        return null;
    }
    public Developer getDeveloperById(Long id) {
        if (id == null) return null;
        for (Developer d : developers) if (id.equals(d.getId())) return d;
        return null;
    }
    public List<Developer> getAllDevelopers() { return new ArrayList<>(developers); }

    // =========================================================================
    // INVESTOR
    // =========================================================================
    public Investor addInvestor(Investor investor) {
        if (investor == null) return null;
        investorPk++;
        investor.setId(investorPk);
        investors.add(investor);
        return investor;
    }
    public Investor getInvestorByUserId(Long userId) {
        if (userId == null) return null;
        for (Investor i : investors) if (userId.equals(i.getUserId())) return i;
        return null;
    }
    public Investor getInvestorById(Long id) {
        if (id == null) return null;
        for (Investor i : investors) if (id.equals(i.getId())) return i;
        return null;
    }
    public List<Investor> getAllInvestors() { return new ArrayList<>(investors); }

    // =========================================================================
    // TEAM
    // =========================================================================
    public Team addTeam(Team team) {
        if (team == null) return null;
        teamPk++;
        team.setId(teamPk);
        if (team.getCreatedAt() == null) team.setCreatedAt(System.currentTimeMillis());
        teams.add(team);
        return team;
    }
    public Team getTeamById(Long id) {
        if (id == null) return null;
        for (Team t : teams) if (id.equals(t.getId())) return t;
        return null;
    }
    public Team getTeamByLeaderId(Long leaderId) {
        if (leaderId == null) return null;
        for (Team t : teams) if (leaderId.equals(t.getTeamLeaderId())) return t;
        return null;
    }
    public List<Team> getAllTeams() { return new ArrayList<>(teams); }

    // =========================================================================
    // TEAM MEMBER
    // =========================================================================
    public TeamMember addTeamMember(TeamMember m) {
        if (m == null) return null;
        teamMemberPk++;
        m.setId(teamMemberPk);
        if (m.getStatus() == null) m.setStatus(TeamMember.MemberStatus.PENDING);
        teamMembers.add(m);
        return m;
    }
    public List<TeamMember> getMembersOfTeam(Long teamId) {
        List<TeamMember> r = new ArrayList<>();
        if (teamId == null) return r;
        for (TeamMember m : teamMembers) if (teamId.equals(m.getTeamId())) r.add(m);
        return r;
    }
    public List<TeamMember> getAcceptedMembersOfTeam(Long teamId) {
        List<TeamMember> r = new ArrayList<>();
        if (teamId == null) return r;
        for (TeamMember m : teamMembers)
            if (teamId.equals(m.getTeamId()) && m.getStatus() == TeamMember.MemberStatus.ACCEPTED) r.add(m);
        return r;
    }
    public List<TeamMember> getPendingInvitations(Long developerId) {
        List<TeamMember> r = new ArrayList<>();
        if (developerId == null) return r;
        for (TeamMember m : teamMembers)
            if (developerId.equals(m.getDeveloperId()) && m.getStatus() == TeamMember.MemberStatus.PENDING) r.add(m);
        return r;
    }

    // ── MyTeams — all ACCEPTED memberships for a developer (can be many teams)
    public List<TeamMember> getAcceptedMembershipsByDeveloperId(Long developerId) {
        List<TeamMember> r = new ArrayList<>();
        if (developerId == null) return r;
        for (TeamMember m : teamMembers)
            if (developerId.equals(m.getDeveloperId()) && m.getStatus() == TeamMember.MemberStatus.ACCEPTED) r.add(m);
        return r;
    }

    // ── LeaveTeam — check developer is an accepted member of a specific team
    public boolean isAcceptedMember(Long teamId, Long developerId) {
        if (teamId == null || developerId == null) return false;
        for (TeamMember m : teamMembers)
            if (teamId.equals(m.getTeamId()) && developerId.equals(m.getDeveloperId())
                    && m.getStatus() == TeamMember.MemberStatus.ACCEPTED) return true;
        return false;
    }

    public boolean updateTeamMemberStatus(Long teamId, Long developerId, TeamMember.MemberStatus status) {
        if (teamId == null || developerId == null || status == null) return false;
        for (TeamMember m : teamMembers)
            if (teamId.equals(m.getTeamId()) && developerId.equals(m.getDeveloperId())) {
                m.setStatus(status);
                return true;
            }
        return false;
    }
    public boolean isAlreadyInTeam(Long teamId, Long developerId) {
        if (teamId == null || developerId == null) return false;
        for (TeamMember m : teamMembers)
            if (teamId.equals(m.getTeamId()) && developerId.equals(m.getDeveloperId())
                    && m.getStatus() != TeamMember.MemberStatus.REJECTED) return true;
        return false;
    }
    public boolean removeTeamMember(Long teamId, Long developerId) {
        if (teamId == null || developerId == null) return false;
        return teamMembers.removeIf(m ->
                teamId.equals(m.getTeamId()) && developerId.equals(m.getDeveloperId()));
    }
    public int getTeamMemberCount(Long teamId) { return getMembersOfTeam(teamId).size(); }
    public long countPendingInvitations(Long userId) {
        if (userId == null) return 0;
        Developer dev = getDeveloperByUserId(userId);
        if (dev == null) return 0;
        long count = 0;
        for (TeamMember m : teamMembers)
            if (dev.getId().equals(m.getDeveloperId()) && m.getStatus() == TeamMember.MemberStatus.PENDING) count++;
        return count;
    }

    // =========================================================================
    // PROJECT
    // =========================================================================
    public Project addProject(Project project) {
        if (project == null) return null;
        projectPk++;
        project.setId(projectPk);
        if (project.getCreatedAt() == null) project.setCreatedAt(System.currentTimeMillis());
        if (project.getStatus() == null) project.setStatus(Project.ProjectStatus.IDEA);
        projects.add(project);
        return project;
    }
    public Project getProjectById(Long id) {
        if (id == null) return null;
        for (Project p : projects) if (id.equals(p.getId())) return p;
        return null;
    }
    public Project updateProject(Project project) {
        if (project == null || project.getId() == null) return null;
        for (int i = 0; i < projects.size(); i++)
            if (project.getId().equals(projects.get(i).getId())) { projects.set(i, project); return project; }
        return null;
    }
    public List<Project> getProjectsByTeamId(Long teamId) {
        List<Project> r = new ArrayList<>();
        if (teamId == null) return r;
        for (Project p : projects) if (teamId.equals(p.getTeamId())) r.add(p);
        return r;
    }
    public List<Project> getAllProjects() { return new ArrayList<>(projects); }
    public List<Project> getProjectsByDomain(String domain) {
        List<Project> r = new ArrayList<>();
        if (domain == null || domain.trim().isEmpty()) return r;
        String key = domain.trim().toLowerCase(Locale.ROOT);
        for (Project p : projects)
            if (p.getDomain() != null && p.getDomain().trim().toLowerCase(Locale.ROOT).contains(key)) r.add(p);
        return r;
    }
    public List<Project> getProjectsByMaxBudget(Double maxBudget) {
        List<Project> r = new ArrayList<>();
        if (maxBudget == null) return r;
        for (Project p : projects)
            if (p.getEstimatedCost() != null && p.getEstimatedCost() <= maxBudget) r.add(p);
        return r;
    }

    // =========================================================================
    // TECH STACK
    // =========================================================================
    public TechStack addTechStack(TechStack t) {
        if (t == null) return null;
        techStackPk++;
        t.setId(techStackPk);
        techStacks.add(t);
        return t;
    }
    public List<TechStack> getTechStackByProjectId(Long projectId) {
        List<TechStack> r = new ArrayList<>();
        if (projectId == null) return r;
        for (TechStack t : techStacks) if (projectId.equals(t.getProjectId())) r.add(t);
        return r;
    }
    public List<Project> getProjectsByTechnology(String technology) {
        List<Project> r = new ArrayList<>();
        if (technology == null || technology.trim().isEmpty()) return r;
        String key = technology.trim().toLowerCase(Locale.ROOT);
        for (TechStack t : techStacks)
            if (t.getTechnology() != null && t.getTechnology().trim().toLowerCase(Locale.ROOT).contains(key)) {
                Project p = getProjectById(t.getProjectId());
                if (p != null && !r.contains(p)) r.add(p);
            }
        return r;
    }

    // =========================================================================
    // FUNDING APPLICATION
    // =========================================================================
    public FundingApplication addFundingApplication(FundingApplication app) {
        if (app == null) return null;
        fundingApplicationPk++;
        app.setId(fundingApplicationPk);
        if (app.getAppliedAt() == null) app.setAppliedAt(System.currentTimeMillis());
        if (app.getStatus() == null) app.setStatus(FundingApplication.ApplicationStatus.PENDING);
        fundingApplications.add(app);
        return app;
    }
    public FundingApplication updateFundingApplication(FundingApplication app) {
        if (app == null || app.getId() == null) return null;
        for (int i = 0; i < fundingApplications.size(); i++)
            if (app.getId().equals(fundingApplications.get(i).getId())) { fundingApplications.set(i, app); return app; }
        return null;
    }
    public List<FundingApplication> getFundingApplicationsByProjectId(Long projectId) {
        List<FundingApplication> r = new ArrayList<>();
        if (projectId == null) return r;
        for (FundingApplication a : fundingApplications) if (projectId.equals(a.getProjectId())) r.add(a);
        return r;
    }
    public List<FundingApplication> getFundingApplicationsByInvestorId(Long investorId) {
        List<FundingApplication> r = new ArrayList<>();
        if (investorId == null) return r;
        for (FundingApplication a : fundingApplications) if (investorId.equals(a.getInvestorId())) r.add(a);
        return r;
    }
    public boolean hasPendingApplication(Long projectId, Long investorId) {
        if (projectId == null || investorId == null) return false;
        for (FundingApplication a : fundingApplications)
            if (projectId.equals(a.getProjectId()) && investorId.equals(a.getInvestorId())
                    && a.getStatus() == FundingApplication.ApplicationStatus.PENDING) return true;
        return false;
    }

    // =========================================================================
    // INVESTMENT
    // =========================================================================
    public Investment addInvestment(Investment inv) {
        if (inv == null) return null;
        investmentPk++;
        inv.setId(investmentPk);
        if (inv.getInvestedAt() == null) inv.setInvestedAt(System.currentTimeMillis());
        if (inv.getPaymentStatus() == null) inv.setPaymentStatus(Investment.PaymentStatus.PENDING);
        investments.add(inv);
        return inv;
    }
    public Investment updateInvestment(Investment inv) {
        if (inv == null || inv.getId() == null) return null;
        for (int i = 0; i < investments.size(); i++)
            if (inv.getId().equals(investments.get(i).getId())) { investments.set(i, inv); return inv; }
        return null;
    }
    public List<Investment> getInvestmentsByInvestorId(Long investorId) {
        List<Investment> r = new ArrayList<>();
        if (investorId == null) return r;
        for (Investment inv : investments) if (investorId.equals(inv.getInvestorId())) r.add(inv);
        return r;
    }
    public List<Investment> getInvestmentsByProjectId(Long projectId) {
        List<Investment> r = new ArrayList<>();
        if (projectId == null) return r;
        for (Investment inv : investments) if (projectId.equals(inv.getProjectId())) r.add(inv);
        return r;
    }

    // =========================================================================
    // CONTACT REQUEST
    // =========================================================================
    public ContactRequest addContactRequest(ContactRequest cr) {
        if (cr == null) return null;
        contactRequestPk++;
        cr.setId(contactRequestPk);
        if (cr.getSentAt() == null) cr.setSentAt(System.currentTimeMillis());
        if (cr.getStatus() == null) cr.setStatus(ContactRequest.ContactStatus.PENDING);
        contactRequests.add(cr);
        return cr;
    }
    public ContactRequest updateContactRequest(ContactRequest cr) {
        if (cr == null || cr.getId() == null) return null;
        for (int i = 0; i < contactRequests.size(); i++)
            if (cr.getId().equals(contactRequests.get(i).getId())) { contactRequests.set(i, cr); return cr; }
        return null;
    }
    public List<ContactRequest> getContactRequestsByTeamLeaderId(Long teamLeaderId) {
        List<ContactRequest> r = new ArrayList<>();
        if (teamLeaderId == null) return r;
        for (ContactRequest cr : contactRequests) if (teamLeaderId.equals(cr.getTeamLeaderId())) r.add(cr);
        return r;
    }
    public List<ContactRequest> getContactRequestsByInvestorId(Long investorId) {
        List<ContactRequest> r = new ArrayList<>();
        if (investorId == null) return r;
        for (ContactRequest cr : contactRequests) if (investorId.equals(cr.getInvestorId())) r.add(cr);
        return r;
    }
    public boolean hasAlreadyContacted(Long investorId, Long teamLeaderId) {
        if (investorId == null || teamLeaderId == null) return false;
        for (ContactRequest cr : contactRequests)
            if (investorId.equals(cr.getInvestorId()) && teamLeaderId.equals(cr.getTeamLeaderId())
                    && cr.getStatus() == ContactRequest.ContactStatus.PENDING) return true;
        return false;
    }

    // =========================================================================
    // NOTIFICATION
    // =========================================================================
    public Notification addNotification(Long userId, String message) {
        if (userId == null || message == null) return null;
        notificationPk++;
        Notification n = new Notification();
        n.setId(notificationPk);
        n.setUserId(userId);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(System.currentTimeMillis());
        notifications.add(n);
        return n;
    }
    public List<Notification> getNotificationsByUserId(Long userId) {
        List<Notification> r = new ArrayList<>();
        if (userId == null) return r;
        for (Notification n : notifications) if (userId.equals(n.getUserId())) r.add(n);
        return r;
    }
    public void markAllNotificationsRead(Long userId) {
        if (userId == null) return;
        for (Notification n : notifications) if (userId.equals(n.getUserId())) n.setRead(true);
    }
    public long countUnreadNotifications(Long userId) {
        if (userId == null) return 0;
        long count = 0;
        for (Notification n : notifications) if (userId.equals(n.getUserId()) && !n.isRead()) count++;
        return count;
    }
}
