package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.BigdataEvent;
import com.vivas.campaignx.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BigdataEventRepository extends JpaRepository<BigdataEvent, Long> {

    List<BigdataEvent> findByStatus(Integer status);

    Optional<BigdataEvent> findById(Long id);
}
