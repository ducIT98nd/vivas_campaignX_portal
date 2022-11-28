package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.Notify;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface NotifyRepository extends JpaRepository<Notify,Long> {
    @Query("SELECT a FROM NOTIFY a WHERE a.notifyToUserId = :notifyToUserId AND a.status = 0 ORDER BY a.id DESC")
    List<Notify> findAllNotifyNotReadToUserId(Long notifyToUserId);

    @Query("SELECT a FROM NOTIFY a WHERE a.notifyToUserId = :notifyToUserId AND a.status = 1 ORDER BY a.id DESC")
    List<Notify> findAllNotifyReadToUserId(Pageable pageable, Long notifyToUserId);

    @Modifying
    @Transactional
    @Query(value = "Update Notify set status = 1 where notify_to_user_id =:userId", nativeQuery = true)
    void updateMarkAsReadByUserID(Long userId);
}
