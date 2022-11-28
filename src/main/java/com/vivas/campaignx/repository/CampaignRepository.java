package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.FrequencyCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<FrequencyCampaign, Long> {

    @Query(value = "select campaign_id from campaign c where lower(c.campaign_name) = lower(:campaignName) and c.status <> :status", nativeQuery = true)
    List<Long> findByCampaignNameIgnoreCaseAndStatusNot(String campaignName, Integer status);
}
