package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.TargetGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TargetGroupRepository extends JpaRepository<TargetGroup, Long> {
    @Query(value = "Select * from TARGET_GROUP s "
            + "where (Lower(FN_CONVERT_TO_VN(s.name)) like '%' || :targetName|| '%' or :targetName is null) "
            + "and (Lower(FN_CONVERT_TO_VN(s.created_user)) like '%' || :createdUser|| '%' or :createdUser is null) "
            + "and ((to_date(:createdDate, 'dd/mm/yyyy') <= s.created_date and to_date(:createdDate, 'dd/mm/yyyy') + 1 > s.created_date)  or :createdDate is null)  "
            + "and s.status != -99 "
            + "and s.type != 1 "
            + "order by s.CREATED_DATE desc ",
            countQuery = "select count(*) from ("
                    + "Select * from TARGET_GROUP s "
                    + "where (Lower(FN_CONVERT_TO_VN(s.name)) like '%' || :targetName|| '%' or :targetName is null) "
                    + "and (Lower(FN_CONVERT_TO_VN(s.created_user)) like '%' || :createdUser|| '%' or :createdUser is null) "
                    + "and ((to_date(:createdDate, 'dd/mm/yyyy') <= s.created_date and to_date(:createdDate, 'dd/mm/yyyy') + 1 > s.created_date)  or :createdDate is null)  "
                    + "and s.status != -99 "
                    + "and s.type != 1 "
                    + "order by s.CREATED_DATE desc "
                    + ")"
            , nativeQuery = true)
    Page<TargetGroup> getTargetGroup(@Param("targetName") String targetName,@Param("createdDate") String createdDate,@Param("createdUser") String createdUser, Pageable paging);

    @Query(value = "select * from target_group s where s.status != 99 and s.type != 1 order by s.created_date",nativeQuery = true)
    List<TargetGroup> exportAll();

    @Query(value = "Select * from TARGET_GROUP s "
            + "where (Lower(FN_CONVERT_TO_VN(s.name)) like '%' || :targetName|| '%' or :targetName is null) "
            + "and (Lower(FN_CONVERT_TO_VN(s.created_user)) like '%' || :createdUser|| '%' or :createdUser is null) "
            + "and ((to_date(:createdDate, 'dd/mm/yyyy') <= s.created_date and to_date(:createdDate, 'dd/mm/yyyy') + 1 > s.created_date)  or :createdDate is null) "
            + "and s.status != -99 "
            + "and s.type != 1 "
            + "order by s.CREATED_DATE desc"
            , nativeQuery = true)
    List<TargetGroup> exportTargetGroup(@Param("targetName") String targetName,@Param("createdDate") String createdDate,@Param("createdUser") String createdUser);

    @Modifying
    @Query(value = "update target_group s set s.status = -99 where s.id = :id",nativeQuery = true)
    void deleteTargetGroup(Long id);

    @Query(value = "Select * from TARGET_GROUP s "
            + "where s.status != -99 "
            + "and s.type != 1 "
            + "order by s.CREATED_DATE desc ",
            countQuery = "select count(*) from ("
                    + "Select * from TARGET_GROUP s "
                    + "where s.status != -99 "
                    + "and s.type != 1 "
                    + "order by s.CREATED_DATE desc "
                    + ")"
            , nativeQuery = true)
    Page<TargetGroup> getAll(Pageable paging);
    @Query(value = "select count(*) from target_group tg WHERE LOWER(FN_CONVERT_TO_VN(tg.name)) like LOWER(FN_CONVERT_TO_VN(:name ))  and tg.status not in(-99)", nativeQuery = true)
    Long countByName(String name);

    @Query(value = "select count(*) from target_group tg WHERE LOWER(FN_CONVERT_TO_VN(tg.name)) like LOWER(FN_CONVERT_TO_VN(:name )) and tg.id not in (:id)  and tg.status not in(-99)", nativeQuery = true)
    Long countByNameNotInIds(String name, Long id);

    @Query(value = "select * from target_group s where s.status != -99 and type = 0",nativeQuery = true)
    List<TargetGroup> findAllByStatusAndType();

    Optional<TargetGroup> findByIdAndStatusNot(Long id, Integer status);


}
