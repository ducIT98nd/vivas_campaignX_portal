package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.BigdataCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface BigdataCriteriaRepository extends JpaRepository<BigdataCriteria,Long> {

}
