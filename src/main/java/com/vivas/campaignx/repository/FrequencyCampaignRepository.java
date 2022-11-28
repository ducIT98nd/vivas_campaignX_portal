package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.CampaignDTO;
import com.vivas.campaignx.entity.FrequencyCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface FrequencyCampaignRepository extends JpaRepository<FrequencyCampaign, Long> {
    @Query(value = "select count (*) from frequency_campaign s where s.id_target_group = :idTargetGroup and s.status in (0,1,2,6)", nativeQuery = true)
    int countTargetGroupByStatus(Long idTargetGroup);

    List<FrequencyCampaign> findAllByStatusIsNot(Integer status);
    List<FrequencyCampaign> findAllByStatusIn(List<Integer> status);


    List<FrequencyCampaign> findAllByPackageDataIdAndStatusNotIn(Long packdataId, List<Integer> status);

    @Query(value = "select * from frequency_campaign ec where (Lower(FN_CONVERT_TO_VN(ec.campaign_name)) like '%' || Lower(FN_CONVERT_TO_VN(:campaignName)) || '%' or :campaignName is null)\n" +
            "and (Lower(FN_CONVERT_TO_VN(ec.created_user)) like '%' || :createdUser|| '%' or :createdUser is null)\n" +
            "and ((to_date(:createdDate, 'dd/mm/yyyy') <= ec.created_date and to_date(:createdDate, 'dd/mm/yyyy') + 1 > ec.created_date)  or :createdDate is null) \n" +
            "and ((to_date(:startDate, 'dd/mm/yyyy') <= ec.start_date) or :startDate is null) and ((to_date(:endDate,'dd/mm/yyyy') >= ec.end_date) or :endDate is null)\n" +
            "and (ec.status in (:listStatus))"
            , nativeQuery = true)
    List<FrequencyCampaign> search(@Param("campaignName") String campaignName,
                                   @Param("createdUser") String createdUser,
                                   @Param("createdDate") String createdDate,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate,
                                   @Param("listStatus") List<Integer> listStatus

    );

    @Query(value = "SELECT * FROM frequency_campaign WHERE LOWER(campaign_name) = :name AND status != -99", nativeQuery = true)
    Optional<FrequencyCampaign> findCampaignByNameCaseSensitive(String name);

    @Query(value = "select * from frequency_campaign ec where ((Lower(FN_CONVERT_TO_VN(ec.campaign_name)) like '%' || Lower(FN_CONVERT_TO_VN(:campaignName)) || '%' or :campaignName is null)\n" +
            "and ((to_date(:startDate, 'dd/mm/yyyy') <= ec.start_date) or :startDate is null) and ((to_date(:endDate,'dd/mm/yyyy') >= ec.end_date) or :endDate is null)\n" +
            "and (ec.status in (:listStatus))) order by ec.created_date desc"
            , nativeQuery = true)
    List<FrequencyCampaign> searchReport(@Param("campaignName") String campaignName,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate,
                                     @Param("listStatus") List<Integer> listStatus
    );

    @Query(value = "select * from frequency_campaign ec where " +
            " (ec.status in (:listStatus)) order by ec.created_date desc"
            , nativeQuery = true)
    List<FrequencyCampaign> changeReport(
            @Param("listStatus") List<Integer> listStatus
    );

    @Modifying
    @Transactional
    @Query(value = "update FREQUENCY_CAMPAIGN s set s.MAIN_GROUP_SIZE = :groupSize, s.RATIO = :ratio where s.CAMPAIGN_ID = :campaignId",nativeQuery = true)
    void updateMainGroupSizeAndRatioByCampaignId(Long campaignId, Long groupSize, Double ratio);
    
    List<FrequencyCampaign> findAllByIdTargetGroup(Long targetGroupId);

    List<FrequencyCampaign> findAllByIdTargetGroupAndStatusIn(Long targetGroupId, List<Integer> status);

}
