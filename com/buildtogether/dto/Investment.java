package com.buildtogether.dto;

public class Investment {

    private Long id;
    private Long investorId;
    private Long projectId;
    private Double amount;
    private Long investedAt;
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PENDING, PAID
    }

    public Investment() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Long getInvestedAt() { return investedAt; }
    public void setInvestedAt(Long investedAt) { this.investedAt = investedAt; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
