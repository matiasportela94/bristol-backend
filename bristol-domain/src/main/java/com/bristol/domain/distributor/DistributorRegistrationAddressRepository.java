package com.bristol.domain.distributor;

import java.util.List;

public interface DistributorRegistrationAddressRepository {

    DistributorRegistrationAddress save(DistributorRegistrationAddress address);

    List<DistributorRegistrationAddress> findByRegistrationRequestId(DistributorRegistrationRequestId registrationRequestId);
}
