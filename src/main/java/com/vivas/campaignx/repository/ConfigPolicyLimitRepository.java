package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.ConfigPolicyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigPolicyLimitRepository extends JpaRepository<ConfigPolicyLimit, String> {
}
