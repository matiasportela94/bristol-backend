package com.bristol.application.distributor.usecase;

public interface DistributorRegistrationNotificationService {

    void sendApprovalEmail(String email, String razonSocial, String temporaryPassword);

    void sendRejectionEmail(String email, String razonSocial, String reason);
}
