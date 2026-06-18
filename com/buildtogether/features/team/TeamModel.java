package com.buildtogether.features.team;

import com.buildtogether.dto.Developer;
import com.buildtogether.dto.Project;
import com.buildtogether.dto.Team;
import com.buildtogether.dto.TeamMember;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.ArrayList;
import java.util.List;

class TeamModel {

    private final TeamView teamView;

    TeamModel(TeamView teamView) {
        this.teamView = teamView;
    }

    // =========================================================================
    // Create team
    // =========================================================================

    void createTeam(User user, String teamName) {

        if (teamName == null || teamName.isEmpty()) {
            teamView.onTeamCreateFailed("Team name cannot be empty");
            return;
        }

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.onTeamCreateFailed(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        Team existing = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(developer.getId());
        if (existing != null) {
            teamView.onTeamCreateFailed(
                    "You already have a team: " + existing.getTeamName());
            return;
        }

        Team team = new Team();
        team.setTeamName(teamName);
        team.setTeamLeaderId(developer.getId());

        Team saved = BuildTogetherDB.getInstance().addTeam(team);
        if (saved == null) {
            teamView.onTeamCreateFailed("Something went wrong. Please try again.");
            return;
        }

        // add leader as first ACCEPTED member automatically
        TeamMember leaderMember = new TeamMember();
        leaderMember.setTeamId(saved.getId());
        leaderMember.setDeveloperId(developer.getId());
        leaderMember.setStatus(TeamMember.MemberStatus.ACCEPTED);
        BuildTogetherDB.getInstance().addTeamMember(leaderMember);

        teamView.onTeamCreated(saved);
    }

    // =========================================================================
    // Load all developers — for invite screen
    // =========================================================================

    void loadAllDevelopers(User user) {
        Developer myDeveloper = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (myDeveloper == null) {
            teamView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        Team myTeam = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(myDeveloper.getId());
        if (myTeam == null) {
            teamView.showError("Please create a team first (option 2)");
            return;
        }

        List<Developer> all = BuildTogetherDB.getInstance().getAllDevelopers();
        List<User> allUsers = BuildTogetherDB.getInstance().getAllUsers();
        teamView.showAllDevelopers(all, allUsers, myDeveloper.getId());
    }

    // =========================================================================
    // Send invite
    // =========================================================================

    void sendInvite(User user, String developerIdInput) {

        Long developerIdToInvite;
        try {
            developerIdToInvite = Long.parseLong(developerIdInput);
        } catch (NumberFormatException e) {
            teamView.onInviteFailed("Invalid ID. Please enter a number.");
            return;
        }

        Developer myDeveloper = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (myDeveloper == null) {
            teamView.onInviteFailed("Please complete your developer profile first");
            return;
        }

        Team myTeam = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(myDeveloper.getId());
        if (myTeam == null) {
            teamView.onInviteFailed(
                    "You don't have a team yet. Create a team first (option 2)");
            return;
        }

        // check max 5 accepted members
        int currentCount = BuildTogetherDB.getInstance()
                .getAcceptedMembersOfTeam(myTeam.getId()).size();
        if (currentCount >= 5) {
            teamView.onInviteFailed("Team is full. Maximum 5 members allowed.");
            return;
        }

        Developer developerToInvite = BuildTogetherDB.getInstance()
                .getDeveloperById(developerIdToInvite);
        if (developerToInvite == null) {
            teamView.onInviteFailed(
                    "No developer found with ID: " + developerIdToInvite);
            return;
        }

        boolean alreadyIn = BuildTogetherDB.getInstance()
                .isAlreadyInTeam(myTeam.getId(), developerIdToInvite);
        if (alreadyIn) {
            teamView.onInviteFailed(
                    "This developer is already invited or in your team");
            return;
        }

        // save PENDING invite
        TeamMember invite = new TeamMember();
        invite.setTeamId(myTeam.getId());
        invite.setDeveloperId(developerIdToInvite);
        invite.setStatus(TeamMember.MemberStatus.PENDING);
        BuildTogetherDB.getInstance().addTeamMember(invite);

        // notify invited developer
        User invitedUser = BuildTogetherDB.getInstance()
                .getUserById(developerToInvite.getUserId());
        if (invitedUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    invitedUser.getId(),
                    "[TEAM INVITE] You have been invited to join team '"
                    + myTeam.getTeamName()
                    + "' by " + user.getName()
                    + ". Go to option 12 (My invitations) to accept or decline.");
        }

        String inviteeName = invitedUser != null ? invitedUser.getName() : "Developer";
        teamView.onInviteSent(inviteeName);
    }

    // =========================================================================
    // Load members — only ACCEPTED
    // =========================================================================

    void loadMembers(User user) {
        Developer myDeveloper = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (myDeveloper == null) {
            teamView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        Team myTeam = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(myDeveloper.getId());
        if (myTeam == null) {
            teamView.showError(
                    "You don't have a team yet. Create a team first (option 2)");
            return;
        }

        List<TeamMember> members = BuildTogetherDB.getInstance()
                .getAcceptedMembersOfTeam(myTeam.getId());
        List<Developer> allDevelopers = BuildTogetherDB.getInstance().getAllDevelopers();
        List<User> allUsers = BuildTogetherDB.getInstance().getAllUsers();

        teamView.showMembers(members, allDevelopers, allUsers,
                myTeam.getTeamLeaderId());
    }

    // =========================================================================
    // Remove member
    // =========================================================================

    void removeMember(User user, String developerIdInput) {

        Long developerIdToRemove;
        try {
            developerIdToRemove = Long.parseLong(developerIdInput);
        } catch (NumberFormatException e) {
            teamView.onMemberRemoveFailed("Invalid ID. Please enter a number.");
            return;
        }

        Developer myDeveloper = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (myDeveloper == null) {
            teamView.onMemberRemoveFailed(
                    "Please complete your developer profile first");
            return;
        }

        if (developerIdToRemove.equals(myDeveloper.getId())) {
            teamView.onMemberRemoveFailed(
                    "You cannot remove yourself. You are the team leader.");
            return;
        }

        Team myTeam = BuildTogetherDB.getInstance()
                .getTeamByLeaderId(myDeveloper.getId());
        if (myTeam == null) {
            teamView.onMemberRemoveFailed("You don't have a team yet.");
            return;
        }

        boolean removed = BuildTogetherDB.getInstance()
                .removeTeamMember(myTeam.getId(), developerIdToRemove);
        if (removed) {
            teamView.onMemberRemoved();
        } else {
            teamView.onMemberRemoveFailed("This developer is not in your team.");
        }
    }

    // =========================================================================
    // Load my invitations — PENDING only
    // =========================================================================

    void loadMyInvitations(User user) {

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        List<TeamMember> invitations = BuildTogetherDB.getInstance()
                .getPendingInvitations(developer.getId());
        List<Team> allTeams = BuildTogetherDB.getInstance().getAllTeams();
        List<Developer> allDevelopers = BuildTogetherDB.getInstance().getAllDevelopers();
        List<User> allUsers = BuildTogetherDB.getInstance().getAllUsers();

        teamView.showMyInvitations(invitations, allTeams, allDevelopers, allUsers);
    }

    // =========================================================================
    // Accept invitation
    // =========================================================================

    void acceptInvite(User user, String teamIdInput) {

        Long teamId;
        try {
            teamId = Long.parseLong(teamIdInput);
        } catch (NumberFormatException e) {
            teamView.onInviteActionFailed("Invalid team ID. Please enter a number.");
            return;
        }

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.onInviteActionFailed("Developer profile not found");
            return;
        }

        Team team = BuildTogetherDB.getInstance().getTeamById(teamId);
        if (team == null) {
            teamView.onInviteActionFailed("No team found with ID: " + teamId);
            return;
        }

        boolean updated = BuildTogetherDB.getInstance()
                .updateTeamMemberStatus(teamId, developer.getId(),
                        TeamMember.MemberStatus.ACCEPTED);
        if (!updated) {
            teamView.onInviteActionFailed(
                    "No pending invitation found for this team");
            return;
        }

        // notify team leader
        User leaderUser = getTeamLeaderUser(team.getTeamLeaderId());
        if (leaderUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    leaderUser.getId(),
                    "[INVITE ACCEPTED] " + user.getName()
                    + " has accepted your invitation to join team '"
                    + team.getTeamName() + "'!");
        }

        teamView.onInviteAccepted(team.getTeamName());
    }

    // =========================================================================
    // Decline invitation
    // =========================================================================

    void declineInvite(User user, String teamIdInput) {

        Long teamId;
        try {
            teamId = Long.parseLong(teamIdInput);
        } catch (NumberFormatException e) {
            teamView.onInviteActionFailed("Invalid team ID. Please enter a number.");
            return;
        }

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.onInviteActionFailed("Developer profile not found");
            return;
        }

        Team team = BuildTogetherDB.getInstance().getTeamById(teamId);
        if (team == null) {
            teamView.onInviteActionFailed("No team found with ID: " + teamId);
            return;
        }

        boolean updated = BuildTogetherDB.getInstance()
                .updateTeamMemberStatus(teamId, developer.getId(),
                        TeamMember.MemberStatus.REJECTED);
        if (!updated) {
            teamView.onInviteActionFailed(
                    "No pending invitation found for this team");
            return;
        }

        // notify team leader
        User leaderUser = getTeamLeaderUser(team.getTeamLeaderId());
        if (leaderUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    leaderUser.getId(),
                    "[INVITE DECLINED] " + user.getName()
                    + " has declined your invitation to join team '"
                    + team.getTeamName() + "'.");
        }

        teamView.onInviteDeclined(team.getTeamName());
    }

    // =========================================================================
    // MY TEAMS — load all teams this developer has ACCEPTED
    // developer can be part of many teams
    // =========================================================================

    void loadMyTeams(User user) {

        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.showError(
                    "Please complete your developer profile first (option 1)");
            return;
        }

        // get ALL team member records for this developer with ACCEPTED status
        List<TeamMember> myMemberships = BuildTogetherDB.getInstance()
                .getAcceptedMembershipsByDeveloperId(developer.getId());

        // collect the actual Team objects
        List<Team> myTeams = new ArrayList<>();
        for (TeamMember membership : myMemberships) {
            Team team = BuildTogetherDB.getInstance()
                    .getTeamById(membership.getTeamId());
            if (team != null) myTeams.add(team);
        }

        List<Developer> allDevelopers = BuildTogetherDB.getInstance()
                .getAllDevelopers();
        List<User> allUsers = BuildTogetherDB.getInstance().getAllUsers();
        List<Project> allProjects = BuildTogetherDB.getInstance().getAllProjects();

        teamView.showMyTeams(myTeams, allDevelopers, allUsers, allProjects);
    }

    // =========================================================================
    // LEAVE TEAM — developer removes themselves from a team
    // cannot leave if they are the team leader
    // =========================================================================

    void leaveTeam(User user, String teamIdInput) {

        // Step 1 — parse team ID
        Long teamId;
        try {
            teamId = Long.parseLong(teamIdInput);
        } catch (NumberFormatException e) {
            teamView.onLeaveTeamFailed("Invalid team ID. Please enter a number.");
            return;
        }

        // Step 2 — get developer profile
        Developer developer = BuildTogetherDB.getInstance()
                .getDeveloperByUserId(user.getId());
        if (developer == null) {
            teamView.onLeaveTeamFailed("Developer profile not found");
            return;
        }

        // Step 3 — get the team
        Team team = BuildTogetherDB.getInstance().getTeamById(teamId);
        if (team == null) {
            teamView.onLeaveTeamFailed("No team found with ID: " + teamId);
            return;
        }

        // Step 4 — team leader cannot leave their own team
        if (team.getTeamLeaderId().equals(developer.getId())) {
            teamView.onLeaveTeamFailed(
                    "You are the team leader of '" + team.getTeamName()
                    + "'. Team leaders cannot leave their own team.");
            return;
        }

        // Step 5 — check developer is actually in this team
        boolean isMember = BuildTogetherDB.getInstance()
                .isAcceptedMember(teamId, developer.getId());
        if (!isMember) {
            teamView.onLeaveTeamFailed(
                    "You are not a member of team: " + team.getTeamName());
            return;
        }

        // Step 6 — remove from team
        boolean removed = BuildTogetherDB.getInstance()
                .removeTeamMember(teamId, developer.getId());
        if (!removed) {
            teamView.onLeaveTeamFailed("Something went wrong. Please try again.");
            return;
        }

        // Step 7 — notify team leader
        User leaderUser = getTeamLeaderUser(team.getTeamLeaderId());
        if (leaderUser != null) {
            BuildTogetherDB.getInstance().addNotification(
                    leaderUser.getId(),
                    "[MEMBER LEFT] " + user.getName()
                    + " has left your team '" + team.getTeamName() + "'.");
        }

        teamView.onLeaveTeamSuccess(team.getTeamName());
    }

    // =========================================================================
    // Helper — get team leader User from developer ID
    // =========================================================================

    private User getTeamLeaderUser(Long developerLeaderId) {
        Developer leader = BuildTogetherDB.getInstance()
                .getDeveloperById(developerLeaderId);
        if (leader == null) return null;
        return BuildTogetherDB.getInstance().getUserById(leader.getUserId());
    }
}
