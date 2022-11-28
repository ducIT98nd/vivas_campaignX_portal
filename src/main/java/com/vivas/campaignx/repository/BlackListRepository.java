package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    @Transactional
    void deleteAllByFileId(Long fileId);
}
