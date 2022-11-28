package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.UserRole;
import com.vivas.campaignx.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

   /* @Modifying
    @Transactional
    @Query("delete from user_role b where b.user=:user")
    int deleteByUser(Users user);*/
    @Modifying
    @Transactional
    @Query(value = "Update user_role set role_Id = :roleId where user_id =:userId", nativeQuery = true)
    void updateByUserID(Long roleId, Long userId);

    @Query(value = "select * from user_role b where TO_NUMBER(b.user_id)=:userId",nativeQuery = true)
	UserRole findUserRoledByUserId(Integer userId);
    
    List<UserRole> findByRoleRoleId(Long roleId);
}
