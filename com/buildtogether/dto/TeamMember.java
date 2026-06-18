package com.buildtogether.dto;

public class TeamMember {

    private Long id;
    private Long teamId;
    private Long developerId;
    private MemberStatus status;

    public enum MemberStatus {
        PENDING, ACCEPTED, REJECTED
    }

    public TeamMember() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getDeveloperId() { return developerId; }
    public void setDeveloperId(Long developerId) { this.developerId = developerId; }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }
}
