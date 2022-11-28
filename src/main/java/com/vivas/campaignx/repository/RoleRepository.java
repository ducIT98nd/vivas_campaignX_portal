package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.Role;
import com.vivas.campaignx.entity.TargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(value = "SELECT * FROM ROLE r WHERE r.status = 1", nativeQuery = true)
    List<Role> findAllRoleActive();
}
