package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.EventCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EventCampaignRepository extends JpaRepository<EventCampaign,Long> {
    @Query(value = "select count (*) from event_campaign s where s.id_target_group = :idTargetGroup and s.status in (0,1,2,6)",nativeQuery = true)
    int countTargetGroupByStatus(Long idTargetGroup);

    List<EventCampaign> findAllByStatusIsNot(Integer status);

    List<EventCampaign> findAllByStatusIn(List<Integer> status);

    List<EventCampaign> findAllByPackageDataIdAndStatusNotIn(Long packdataId, List<Integer> status);

    @Query(value = "select * from event_campaign ec where (Lower(FN_CONVERT_TO_VN(ec.campaign_name)) like '%' || Lower(FN_CONVERT_TO_VN(:campaignName)) || '%' or :campaignName is null)\n" +
            "and (Lower(FN_CONVERT_TO_VN(ec.created_user)) like '%' || :createdUser|| '%' or :createdUser is null)\n" +
            "and ((to_date(:createdDate, 'dd/mm/yyyy') <= ec.created_date and to_date(:createdDate, 'dd/mm/yyyy') + 1 > ec.created_date)  or :createdDate is null) \n" +
            "and ((to_date(:startDate, 'dd/mm/yyyy') <= ec.start_date) or :startDate is null) and ((to_date(:endDate, 'dd/mm/yyyy') >= ec.end_date) or :endDate is null)\n" +
            "and (ec.status in (:listStatus))"
           , nativeQuery = true)
    List<EventCampaign> search(@Param("campaignName") String campaignName,
                               @Param("createdUser") String createdUser,
                               @Param("createdDate") String createdDate,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("listStatus") List<Integer> listStatus
                               );

    @Modifying
    @Transactional
    @Query(value = "update EVENT_CAMPAIGN s set s.MAIN_GROUP_SIZE = :groupSize, s.RATIO = :ratio where s.CAMPAIGN_ID = :campaignId",nativeQuery = true)
    void updateMainGroupSizeAndRatioByCampaignId(Long campaignId, Long groupSize, Double ratio);



    @Query(value = "select * from event_campaign ec where ((Lower(FN_CONVERT_TO_VN(ec.campaign_name)) like '%' || Lower(FN_CONVERT_TO_VN(:campaignName)) || '%' or :campaignName is null)\n" +
            "and ((to_date(:startDate, 'dd/mm/yyyy') <= ec.start_date) or :startDate is null) and ((to_date(:endDate, 'dd/mm/yyyy') >= ec.end_date) or :endDate is null)\n" +
            "and (ec.status in (:listStatus))) order by ec.created_date desc"
            , nativeQuery = true)
    List<EventCampaign> searchReport(@Param("campaignName") String campaignName,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("listStatus") List<Integer> listStatus
    );


    @Query(value = "select * from event_campaign ec where"+
            "(ec.status in (:listStatus)) order by ec.created_date desc"
            , nativeQuery = true)
    List<EventCampaign> changeReport(@Param("listStatus") List<Integer> listStatus
    );

    List<EventCampaign> findAllByIdTargetGroup(Long targetGroupId);

    List<EventCampaign> findAllByIdTargetGroupAndStatusIn(Long targetGroupId, List<Integer> status);
}
