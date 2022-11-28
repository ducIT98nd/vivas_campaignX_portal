package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.PackageDataDTO;
import com.vivas.campaignx.dto.PackageNameDTO;
import com.vivas.campaignx.entity.PackageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageDataRepository extends JpaRepository<PackageData, Long> {

    @Query(value = "SELECT" +
            "  pd.ID as id," +
            "  pd.PACKAGE_NAME as packageName, " +
            "  pd.PACKAGE_GROUP as packageGroup, " +
            "  pd.REGISTRATION_SYNTAX as registrationSyntax, " +
            "  pd.PACKAGE_PRICE as packagePrice, " +
            "  pd.CYCLE as cycle, " +
            "  pd.STATUS as status, " +
            "  LISTAGG ( pc.PACKAGE_CODE_NAME, '; ' ) WITHIN GROUP ( ORDER BY pc.PACKAGE_CODE_NAME ) as packageCodeNames " +
            "FROM" +
            "  PACKAGE_DATA pd" +
            "  LEFT JOIN PACKAGE_CODE_CDR pc ON pd.ID = pc.PACKAGE_DATA_ID " +
            "where pd.STATUS <> 99 " +
            "and (upper(pd.PACKAGE_NAME) like upper('%'||trim(:packageDataName)||'%') escape '\\' or :packageDataName is null) " +
            "and (pd.PACKAGE_GROUP = TO_NUMBER(:packageGroup) or :packageGroup is null) " +
            "and (upper(pc.PACKAGE_CODE_NAME) like upper('%'||trim(:packageCodeName)||'%') escape '\\' or :packageCodeName is null) " +
            "and (pd.STATUS = TO_NUMBER(:status) or :status is null) " +
            "GROUP BY" +
            "  pd.ID," +
            "  pd.PACKAGE_NAME, " +
            "  pd.PACKAGE_GROUP, " +
            "  pd.REGISTRATION_SYNTAX, " +
            "  pd.PACKAGE_PRICE, " +
            "  pd.CYCLE, " +
            "  pd.STATUS, " +
            "  pd.CREATED_DATE " +
            "ORDER BY" +
            "  pd.CREATED_DATE DESC NULLS LAST ",
            countQuery = "SELECT COUNT(*) from (SELECT" +
                    "  pd.ID," +
                    "  pd.PACKAGE_NAME," +
                    "  pd.PACKAGE_GROUP," +
                    "  pd.REGISTRATION_SYNTAX," +
                    "  pd.PACKAGE_PRICE," +
                    "  pd.CYCLE," +
                    "  pd.STATUS," +
                    "  LISTAGG ( pc.PACKAGE_CODE_NAME, '; ' ) WITHIN GROUP ( ORDER BY pc.PACKAGE_CODE_NAME ) AS packageCodeNames " +
                    "FROM" +
                    "  PACKAGE_DATA pd" +
                    "  LEFT JOIN PACKAGE_CODE_CDR pc ON pd.ID = pc.PACKAGE_DATA_ID " +
                    "where pd.STATUS <> 99 " +
                    "and (upper(pd.PACKAGE_NAME) like upper('%'||trim(:packageDataName)||'%') escape '\\' or :packageDataName is null) " +
                    "and (pd.PACKAGE_GROUP = TO_NUMBER(:packageGroup) or :packageGroup is null) " +
                    "and (upper(pc.PACKAGE_CODE_NAME) like upper('%'||trim(:packageCodeName)||'%') escape '\\' or :packageCodeName is null) " +
                    "and (pd.STATUS = TO_NUMBER(:status) or :status is null) " +
                    "GROUP BY" +
                    "  pd.ID," +
                    "  pd.PACKAGE_NAME," +
                    "  pd.PACKAGE_GROUP," +
                    "  pd.REGISTRATION_SYNTAX," +
                    "  pd.PACKAGE_PRICE," +
                    "  pd.CYCLE," +
                    "  pd.STATUS," +
                    "  pd.CREATED_DATE " +
                    "ORDER BY" +
                    "  pd.CREATED_DATE DESC NULLS LAST ) ",
            nativeQuery = true)
    Page<PackageDataDTO> findAllPackageData(@Param(value = "packageDataName") String packageDataName,
                                            @Param(value = "packageGroup") Integer packageGroup,
                                            @Param(value = "packageCodeName") String packageCodeName,
                                            @Param(value = "status") Integer status,
                                            Pageable pageable);


    Optional<PackageData> findByPackageNameIgnoreCaseAndStatusNot(String packageName, Integer statusDeleted);

    Optional<PackageData> findByPackageNameIgnoreCaseAndIdNotAndStatusNot(String packageName, Long id, Integer status);

    List<PackageNameDTO> findAllByStatusAndPackageGroup(Integer status, Integer packageGroup, Sort sort);

    Optional<PackageData> findById (Long id);

    List<PackageData> findAllByStatusNotOrderByCreatedDateDesc(Integer status);
}
