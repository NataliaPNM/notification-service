package com.example.notificationservice.repository;

import com.example.notificationservice.model.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodeRepository extends JpaRepository<ConfirmationCode, UUID> {

  List<Optional<ConfirmationCode>> findByPersonIdAndLockCode(UUID personId, boolean lockCode);

  Optional<ConfirmationCode> findByOperationIdAndLockCode(UUID operationId, boolean lockCode);

  Optional<ConfirmationCode> findByPersonId(UUID personId);

  Optional<ConfirmationCode> findByOperationIdAndCodeType(UUID operationId, String codeType);
}
