package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermission, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM GROUP_PERMISSION WHERE GROUP_ID =:groupID",
            nativeQuery = true)
    void deleteByGroupID(@Param("groupID") Long groupID);

    List<GroupPermission> getAllByPermission(Permission permission);

    List<GroupPermission> findAllByGroup_GroupID(Long groupId);
}
