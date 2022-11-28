package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.CmsReportByCampTypeCampCodeDto;
import com.vivas.campaignx.entity.CmsReportByCampTypeCampCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CmsReportByCampTypeCampCodeRepository extends JpaRepository<CmsReportByCampTypeCampCode, Long> {

    @Query(value = "select * from CMS_REPORT_BY_CAMPTYPE_CAMPCODE\n" +
            "where CAMPAIGN_TYPE in (:listType) and CAMPAIGN_STATUS in(:listStatus) and (CAMPAIGN_NAME = :name or :name is null) and REPORTED_DATE>=TO_DATE(:startDate,'dd/mm/yyyy') and REPORTED_DATE<=TO_DATE(:endDate,'dd/mm/yyyy')", nativeQuery = true)
    List<CmsReportByCampTypeCampCode> export(@Param("listType") List<Integer> listType,
                                             @Param("listStatus") List<Integer> listStatus,
                                             @Param("name") String name,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate);

    @Query(value = "select *  from CMS_REPORT_BY_CAMPTYPE_CAMPCODE", nativeQuery = true)
    List<CmsReportByCampTypeCampCode> exportAll();

    @Query(value = "select * from CMS_REPORT_BY_CAMPTYPE_CAMPCODE\n" +
            "where CAMPAIGN_TYPE in (:listType) and CAMPAIGN_STATUS in(:listStatus) and (CAMPAIGN_NAME = :name or :name is null) and REPORTED_DATE>=TO_DATE(:startDate,'dd/mm/yyyy') and REPORTED_DATE<=TO_DATE(:endDate,'dd/mm/yyyy')", nativeQuery = true)
    Page<CmsReportByCampTypeCampCode> findByReportOverview(@Param("listType") List<Integer> listType,
                                                           @Param("listStatus") List<Integer> listStatus,
                                                           @Param("name") String name,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate, Pageable pageable);

    @Query(value = "select * from CMS_REPORT_BY_CAMPTYPE_CAMPCODE\n" +
            "where CAMPAIGN_TYPE in (:listType) and CAMPAIGN_STATUS in(:listStatus) and (CAMPAIGN_NAME = :name or :name is null) and REPORTED_DATE>=TO_DATE(:startDate,'mm/yyyy') and REPORTED_DATE<= LAST_DAY(TO_DATE(:endDate,'mm/yyyy'))", nativeQuery = true)
    Page<CmsReportByCampTypeCampCode> findByReportOverviewMonth(@Param("listType") List<Integer> listType,
                                                           @Param("listStatus") List<Integer> listStatus,
                                                           @Param("name") String name,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate, Pageable pageable);

    @Query(value = "select *  from CMS_REPORT_BY_CAMPTYPE_CAMPCODE", nativeQuery = true)
    Page<CmsReportByCampTypeCampCode> findAllOverview(Pageable pageable);

    @Query(value = "select SUM(INVITATION_MESSAGE) as \"SUM_INVITATION_MESSAGE\", SUM(SUCCESS_MESSAGE) as \"SUM_SUCCESS_MESSAGE\", SUM(REGISTER_MESSAGE) as \"SUM_REGISTER_MESSAGE\", SUM(SUCCESS_REGISTER_MESSAGE) as \"SUM_SUCCESS_REGISTER_MESSAGE\", SUM(revenue) as \"SUM_REVENUE\" from CMS_REPORT_BY_CAMPTYPE_CAMPCODE", nativeQuery = true)
    CmsReportByCampTypeCampCodeDto sumAll();

    @Query(value = "select SUM(INVITATION_MESSAGE) as \"SUM_INVITATION_MESSAGE\", SUM(SUCCESS_MESSAGE) as \"SUM_SUCCESS_MESSAGE\", SUM(REGISTER_MESSAGE) as \"SUM_REGISTER_MESSAGE\", SUM(SUCCESS_REGISTER_MESSAGE) as \"SUM_SUCCESS_REGISTER_MESSAGE\", SUM(revenue) as \"SUM_REVENUE\" from CMS_REPORT_BY_CAMPTYPE_CAMPCODE\n" +
            "where CAMPAIGN_TYPE in (:listType) and CAMPAIGN_STATUS in(:listStatus) and (CAMPAIGN_NAME = :name or :name is null) and REPORTED_DATE>=TO_DATE(:startDate,'dd/mm/yyyy') and REPORTED_DATE<=TO_DATE(:endDate,'dd/mm/yyyy')", nativeQuery = true)
    CmsReportByCampTypeCampCodeDto sum(@Param("listType") List<Integer> listType,
                                       @Param("listStatus") List<Integer> listStatus,
                                       @Param("name") String name,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);

    @Query(value = "select SUM(INVITATION_MESSAGE) as \"SUM_INVITATION_MESSAGE\", SUM(SUCCESS_MESSAGE) as \"SUM_SUCCESS_MESSAGE\", SUM(REGISTER_MESSAGE) as \"SUM_REGISTER_MESSAGE\", SUM(SUCCESS_REGISTER_MESSAGE) as \"SUM_SUCCESS_REGISTER_MESSAGE\" from CMS_REPORT_BY_CAMPTYPE_CAMPCODE\n" +
            "where CAMPAIGN_TYPE in (:listType) and CAMPAIGN_STATUS in(:listStatus) and (CAMPAIGN_NAME = :name or :name is null) and REPORTED_DATE>=TO_DATE(:startDate,'mm/yyyy') and REPORTED_DATE<= LAST_DAY(TO_DATE(:endDate,'mm/yyyy'))", nativeQuery = true)
    CmsReportByCampTypeCampCodeDto sumMonth(@Param("listType") List<Integer> listType,
                                       @Param("listStatus") List<Integer> listStatus,
                                       @Param("name") String name,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);
}
