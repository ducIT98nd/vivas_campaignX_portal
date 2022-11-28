package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query( value = "SELECT * FROM groups g WHERE (LOWER(FN_CONVERT_TO_VN(g.GROUP_NAME)) like '%'||:groupName||'%' or :groupName is null) AND (g.STATUS = TO_NUMBER(:status) or :status is null) order by g.GROUP_ID desc",
            countQuery = "SELECT COUNT(*) FROM groups g WHERE (LOWER(FN_CONVERT_TO_VN(g.GROUP_NAME)) like '%'||:groupName||'%' or :groupName is null) AND (g.STATUS = TO_NUMBER(:status) or :status is null) ",
            nativeQuery = true)
    public Page<Group> getAllGroups(@Param("groupName") String groupName, @Param("status") Integer status, Pageable pageable);

    @Query(value = "select count(*) from groups g where LOWER(g.group_name) like LOWER(:groupName)", nativeQuery = true)
    public long countByGroupName(@Param("groupName") String groupName);
    @Query(value = "select count(*) from groups g where LOWER(g.group_name) like LOWER(:groupName) and g.group_id not in(:groupID)", nativeQuery = true)
    public long countByGroupNameAndGroupIDNotIn(@Param("groupName") String groupName,@Param("groupID") Long groupID);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM GROUPS WHERE GROUP_ID =:groupID",
            nativeQuery = true)
    void deleteByGroupID(@Param("groupID") Long groupID);
}
