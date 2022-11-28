package com.vivas.campaignx.repository;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vivas.campaignx.entity.CampaignTargetTag;
import com.vivas.campaignx.entity.GroupPermission;
import com.vivas.campaignx.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Repository
public interface CampaignTargetTagRepository extends JpaRepository<CampaignTargetTag, Long> {

    @Query(value = "select target_text from CAMPAIGN_TARGET_TAG ",nativeQuery = true)
    List<String> findAllTargetText();

    @Query(value = "select * from CAMPAIGN_TARGET_TAG s where s.target_text = :targetText",nativeQuery = true)
    CampaignTargetTag findByTargetText(String targetText);
}
