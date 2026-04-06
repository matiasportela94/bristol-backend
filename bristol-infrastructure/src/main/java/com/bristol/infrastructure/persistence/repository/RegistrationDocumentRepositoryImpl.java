package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.RegistrationDocument;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.domain.distributor.RegistrationDocumentRepository;
import com.bristol.infrastructure.persistence.mapper.RegistrationDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the registration document repository port.
 */
@Component
@RequiredArgsConstructor
public class RegistrationDocumentRepositoryImpl implements RegistrationDocumentRepository {

    private final JpaRegistrationDocumentRepository jpaRegistrationDocumentRepository;
    private final RegistrationDocumentMapper registrationDocumentMapper;

    @Override
    public RegistrationDocument save(RegistrationDocument document) {
        var saved = jpaRegistrationDocumentRepository.save(registrationDocumentMapper.toEntity(document));
        return registrationDocumentMapper.toDomain(saved);
    }

    @Override
    public Optional<RegistrationDocument> findById(RegistrationDocumentId id) {
        return jpaRegistrationDocumentRepository.findById(id.getValue())
                .map(registrationDocumentMapper::toDomain);
    }

    @Override
    public List<RegistrationDocument> findByRegistrationRequestId(DistributorRegistrationRequestId registrationRequestId) {
        return jpaRegistrationDocumentRepository.findByRegistrationRequestIdOrderByCreatedAtAsc(
                        registrationRequestId.getValue()
                ).stream()
                .map(registrationDocumentMapper::toDomain)
                .toList();
    }

    @Override
    public List<RegistrationDocument> findByDistributorId(DistributorId distributorId) {
        return jpaRegistrationDocumentRepository.findByDistributorIdOrderByCreatedAtAsc(
                        distributorId.getValue()
                ).stream()
                .map(registrationDocumentMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(RegistrationDocumentId id) {
        jpaRegistrationDocumentRepository.deleteById(id.getValue());
    }
}
