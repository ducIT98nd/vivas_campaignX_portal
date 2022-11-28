package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.CmsReportByCamptype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmsReportByCamptypeRepository extends JpaRepository<CmsReportByCamptype, Long> {

}
