package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.PackageCodeCDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageCodeRepository extends JpaRepository<PackageCodeCDR, Long> {
    List<PackageCodeCDR> findAllByPackageDataId(Long packageDataId);

    void deleteByPackageDataId(Long id);

    @Query("from PackageCodeCDR pc inner join PackageData pd on pd.id = pc.packageDataId where pd.status <> 99")
    List<PackageCodeCDR> findAllPackageCodeNames();
}
