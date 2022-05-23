package com.example.notificationservice.repository;

import com.example.notificationservice.model.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository  extends JpaRepository<ConfirmationCode,Long> {
    Optional<ConfirmationCode> findByUserId(Long userId);
}
