package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DistributorBranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDistributorBranchRepository extends JpaRepository<DistributorBranchEntity, UUID> {

    List<DistributorBranchEntity> findByDistributorId(UUID distributorId);
}
