package com.buildtogether.dto;

public class Project {

    private Long id;
    private String title;
    private String description;
    private Long teamId;
    private String domain;
    private Double estimatedCost;
    private Integer timelineDays;
    private ProjectStatus status;
    private Long createdAt;

    public enum ProjectStatus {
        IDEA, IN_PROGRESS, COMPLETED
    }

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Integer getTimelineDays() {
        return timelineDays;
    }

    public void setTimelineDays(Integer timelineDays) {
        this.timelineDays = timelineDays;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
