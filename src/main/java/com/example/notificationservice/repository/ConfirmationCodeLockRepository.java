package com.example.notificationservice.repository;

import com.example.notificationservice.model.ConfirmationCodeLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeLockRepository extends JpaRepository<ConfirmationCodeLock, UUID> {

  Optional<ConfirmationCodeLock> findByUserIdAndCodeType(UUID userId, String codeType);
}
