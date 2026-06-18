package com.buildtogether.dto;

public class FundingApplication {

    private Long id;
    private Long projectId;
    private Long investorId;
    private Double requestedAmount;
    private ApplicationStatus status;
    private Long appliedAt;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    public FundingApplication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Long getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Long appliedAt) {
        this.appliedAt = appliedAt;
    }
}
