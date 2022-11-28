package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.MappingCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface MappingCriteriaRepository extends JpaRepository<MappingCriteria,Long> {

    @Query(value = "select * from mapping_criteria s where s.ID_TARGET_GROUP = :idTargetGroup",nativeQuery = true)
    Optional<MappingCriteria> getByIdTargetGroup(Long idTargetGroup);

    @Query(value = "select * from mapping_criteria s where s.ID_TARGET_GROUP = TO_NUMBER(:idTargetGroup) ORDER by level_criteria ASC, position ASC",nativeQuery = true)
    List<MappingCriteria> getListByIdTargetGroup(Long idTargetGroup);

    List<MappingCriteria> findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(Long idTargetGroup);

    List<MappingCriteria> findAllByIdSubTargetGroupOrderByLevelCriteriaAscPositionAsc(Long idSubTargetGroup);

    List<MappingCriteria> findAllByParentId(Long parentId);

    List<MappingCriteria> findAllByIdSubTargetGroup(Long idSubTargetGroup);

    void deleteAllByIdTargetGroup(Long idTargetGroup);

    void deleteAllByIdSubTargetGroup(Long idTargetGroup);

    @Query(value = "select * from mapping_criteria s where s.ID_TARGET_GROUP = :idTargetGroup and s.LEVEL_CRITERIA = 1 order by s.POSITION asc",nativeQuery = true)
    List<MappingCriteria> getAllCriteriaLevel1ByIdTargetGroup(Long idTargetGroup);
    @Query(value = "select * from mapping_criteria s where s.ID_TARGET_GROUP = :idTargetGroup and s.LEVEL_CRITERIA = 2 order by s.POSITION asc",nativeQuery = true)
    List<MappingCriteria> getAllCriteriaLevel2ByIdTargetGroup(Long idTargetGroup);
    @Query(value = "select * from mapping_criteria s where s.ID_TARGET_GROUP = :idTargetGroup and s.LEVEL_CRITERIA = 3 order by s.POSITION asc",nativeQuery = true)
    List<MappingCriteria> getAllCriteriaLevel3ByIdTargetGroup(Long idTargetGroup);

}
