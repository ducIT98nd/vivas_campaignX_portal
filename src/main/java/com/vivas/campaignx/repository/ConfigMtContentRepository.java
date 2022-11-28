package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.ConfigMtContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigMtContentRepository extends JpaRepository<ConfigMtContent, Long> {

    Optional<ConfigMtContent> findByAction (Integer action);
}

