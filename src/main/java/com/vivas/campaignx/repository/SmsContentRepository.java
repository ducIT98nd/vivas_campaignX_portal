package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.ViewSmsContentDTO;
import com.vivas.campaignx.entity.SmsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
@Transactional
@Repository
public interface SmsContentRepository extends JpaRepository<SmsContent, Long> {

    Optional<SmsContent> findById (Long contentId);
    void deleteById(Long contentId);
    @Query(value = "select sc.message as message from sms_content sc where sc.id = :contentId",nativeQuery = true)
    ViewSmsContentDTO findByIdToView(Long contentId);
}

