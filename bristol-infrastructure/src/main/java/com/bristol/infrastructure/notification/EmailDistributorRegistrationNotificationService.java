package com.bristol.infrastructure.notification;

import com.bristol.application.distributor.usecase.DistributorRegistrationNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDistributorRegistrationNotificationService implements DistributorRegistrationNotificationService {

    @Override
    public void sendApprovalEmail(String email, String razonSocial, String temporaryPassword) {
        log.info("Distributor approval email disabled. Email={}, razonSocial={}, temporaryPassword={}",
                email, razonSocial, temporaryPassword);
    }

    @Override
    public void sendRejectionEmail(String email, String razonSocial, String reason) {
        log.info("Distributor rejection email disabled. Email={}, razonSocial={}, reason={}",
                email, razonSocial, reason);
    }
}
