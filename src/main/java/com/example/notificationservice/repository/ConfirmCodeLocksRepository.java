package com.example.notificationservice.repository;

import com.example.notificationservice.model.ConfirmCodeLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmCodeLocksRepository extends JpaRepository<ConfirmCodeLock, UUID> {

   Optional<ConfirmCodeLock> findByUserIdAndCodeType(UUID userId, String codeType);
}
