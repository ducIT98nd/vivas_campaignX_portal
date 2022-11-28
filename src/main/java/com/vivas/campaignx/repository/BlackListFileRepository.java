package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.BlackListFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlackListFileRepository extends JpaRepository<BlackListFile, Long> {

    @Query(value = "select * from BLACKLIST_FILE\n" +
            "where (status in (:status)) and (Lower(FN_CONVERT_TO_VN(NAME)) like '%' || Lower(FN_CONVERT_TO_VN(:name)) || '%' or :name is null) order by updated_date desc", nativeQuery = true)
    Page<BlackListFile> search(@Param("name") String name, @Param("status") List<Integer> status, Pageable pageable);

    Optional<BlackListFile> findByNameAndStatus(String name, Integer status);

    Long countByNameAndStatus(String name, Integer status);

    Long countByNameAndStatusAndIdIsNot(String name, Integer status, Long id);
}
