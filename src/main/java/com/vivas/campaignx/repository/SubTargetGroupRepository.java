package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.SubTargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SubTargetGroupRepository extends JpaRepository<SubTargetGroup,Long> {
    @Query(value = "SELECT * FROM SUB_TARGET_GROUP WHERE CAMPAIGN_ID = :campaignId ORDER BY ID ASC", nativeQuery = true)
    List<SubTargetGroup> findAllByCampaignId(Long campaignId);
    void deleteAllByCampaignId(Long campaignId);

    List<SubTargetGroup> findAllByPackageDataId(Long packageDataId);
}
