package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.PermissionDTO;
import com.vivas.campaignx.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = "SELECT p.ACTION_KEY as permissionKey FROM PERMISSION p",
            nativeQuery = true)
    List<String> findAllpermissionKey();

    @Query(value = "select p.action_key " +
            "from permission p " +
            "inner join mapping_permission mp on mp.permission_id = p.permission_id " +
            "inner join permission_group pg on mp.permission_group_id = pg.id " +
            "inner join role_permission rp on rp.permission_id_group = pg.id " +
            "inner join role ro on ro.role_id = rp.role_id " +
            "inner join user_role ur on ur.role_id = ro.role_id " +
            "inner join users u on u.user_id = ur.user_id " +
            "where u.status = 1 " +
            "  and pg.status = 1 " +
            "  and ro.status = 1 " +
            "  and u.username = :username",
            nativeQuery = true)
    List<String> getAllPrivilegesByUsername(@Param("username") String username);

}
