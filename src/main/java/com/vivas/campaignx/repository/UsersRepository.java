package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.UserDTO;
import com.vivas.campaignx.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String userName);

    Optional<Users> findByUserId(Integer userId);

    List<Users> findByEmailIgnoreCase(String email);

    List<Users> findByMobileNumber(String mobileNumber);

    Page<Users> findAllByStatusIn(Pageable paging, List<Integer> lstStatus);

    Optional<Users> findByUsernameIgnoreCase(String username);

    Optional<Users> getByEmailIgnoreCase(String email);

    Boolean existsByOtpToken(String otpToken);

    Optional<Users> findByOtpTokenIgnoreCase(String otpToken);

    List<Users> findByIsRoot(Boolean isRoot);

    @Query(value = "Select u.user_id as userId, u.username as username, u.name as name, r.role_id as roleID, r.role_name as roleName, u.email as email, u.status as status from users u, user_role m, role r " +
            "where u.user_id = m.user_id and m.role_id = r.role_id " +
            " and ((LOWER(FN_CONVERT_TO_VN(u.username)) LIKE '%'||:username||'%' escape '\\') or :username is null) " +
            " and ((LOWER(FN_CONVERT_TO_VN(u.email)) LIKE '%'||:email||'%' escape '\\') or :email is null) " +
            " and ((r.role_id = TO_NUMBER(:roleId)) or :roleId is null)" +
            " and ((u.status =  TO_NUMBER(:status)) or :status is null) order by userId desc",
            countQuery = "SELECT count(*) FROM(Select u.user_id as userId, u.username, u.name, r.role_id, r.role_name as roleName, u.email, u.status from users u, user_role m, role r " +
                    " where u.user_id = m.user_id and m.role_id = r.role_id " +
                    " and ((LOWER(FN_CONVERT_TO_VN(u.username)) LIKE '%'||:username||'%' escape '\\') or :username is null) " +
                    " and ((LOWER(FN_CONVERT_TO_VN(u.email)) LIKE '%'||:email||'%' escape '\\') or :email is null)" +
                    " and ((r.role_id =  TO_NUMBER(:roleId)) or :roleId is null)" +
                    " and ((u.status =  TO_NUMBER(:status)) or :status is null) ) order by userId desc",
            nativeQuery = true)
    Page<UserDTO> findListUser (Pageable pageable, @Param("username") String username, @Param("email") String email, @Param("roleId") Long roleId, @Param("status") Long status);

    @Query(value = "Select Count(*) From Users u Where (LOWER(FN_CONVERT_TO_VN(u.username)) LIKE '%'||:username||'%' escape '\\') And status != -1",
            nativeQuery = true)
    Long countUsersByUsernameIgnoreStatusDelete(@Param("username") String username);

    @Query(value = "Select Count(*) From Users Where email = LOWER(FN_CONVERT_TO_VN(:email)) And status != -1",
            nativeQuery = true)
    Long countEmailIgnoreStatusDelete(@Param("email") String email);

    @Query(value = "Select Count(*) From Users Where email = LOWER(FN_CONVERT_TO_VN(:email)) And status != 0",
            nativeQuery = true)
    Long countEmailIgoreStatus(@Param("email") String email);

    @Query(value = "Select Count(*) From Users Where user_id != :id And email = LOWER(FN_CONVERT_TO_VN(:email)) And status != -1",
            nativeQuery = true)
    Long countEmailAndIdIgnoreStatusDelete(@Param("email") String email, @Param("id") Long id);

    @Query(value = "Select u.user_id as userId, u.username as username, u.name as name, r.role_id as roleID, r.role_name as roleName, u.email as email, u.status as status from users u, user_role m, role r " +
            "where u.user_id = m.user_id and m.role_id = r.role_id " +
            " and u.user_id =:userId",
            nativeQuery = true)
    UserDTO findDetailUser ( @Param("userId") Long userId);

    @Query(value = "Select u.user_id as userId, u.username as username, u.name as name, r.role_id as roleID, r.role_name as roleName, u.email as email, u.status as status from users u, user_role m, role r " +
            "where u.user_id = m.user_id and m.role_id = r.role_id " +
            " and ((LOWER(FN_CONVERT_TO_VN(u.username)) LIKE '%'||:username||'%' escape '\\') or :username is null) " +
            " and ((LOWER(FN_CONVERT_TO_VN(u.email)) LIKE '%'||:email||'%' escape '\\') or :email is null) " +
            " and ((r.role_id = TO_NUMBER(:roleId)) or :roleId is null)" +
            " and ((u.status =  TO_NUMBER(:status)) or :status is null) order by id desc",
            nativeQuery = true)
    List<UserDTO> findAllUser ( @Param("username") String username, @Param("email") String email, @Param("roleId") Long roleId, @Param("status") Long status);

    @Query(value = "UPDATE USERS u SET u.failed_attempt = ?1 WHERE u.username = ?2", nativeQuery = true)
    @Modifying
    void updateFailedAttempts(int failAttempts, String userName);
}
