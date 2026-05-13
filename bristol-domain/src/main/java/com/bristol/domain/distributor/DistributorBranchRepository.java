package com.bristol.domain.distributor;

import java.util.List;
import java.util.Optional;

public interface DistributorBranchRepository {

    DistributorBranch save(DistributorBranch branch);

    Optional<DistributorBranch> findById(DistributorBranchId id);

    List<DistributorBranch> findByDistributorId(DistributorId distributorId);

    List<DistributorBranch> findAll();

    void delete(DistributorBranchId id);

    boolean existsById(DistributorBranchId id);
}
