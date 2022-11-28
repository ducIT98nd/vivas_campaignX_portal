package com.vivas.campaignx.repository;

import com.vivas.campaignx.dto.SendingAccountDTO;
import com.vivas.campaignx.entity.SendingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SendingAccountRepository extends JpaRepository<SendingAccount, Long> {

    @Query("select sa.id as id, sa.senderAccount as senderAccount, sa.mediaChannel as mediaChannel, sa.status as status, sa.createdBy as createdBy, sa.createdDate as createdDate " +
            "from SendingAccount sa " +
            "where sa.status <> 99 " +
            "and (sa.mediaChannel = :mediaChannel or :mediaChannel is null) " +
            "and (upper(sa.senderAccount) like upper(concat('%',trim(:senderAccount),'%')) escape '\\' or :senderAccount is null) " +
            "and (sa.status = :status or :status is null) " +
            "order by sa.createdDate desc nulls last ")
    Page<SendingAccountDTO> findAll(@Param(value = "mediaChannel") Integer mediaChannel,
                                    @Param(value = "senderAccount") String senderAccount,
                                    @Param(value = "status") Integer status,
                                    Pageable pageable);

    Optional<SendingAccount> findBySenderAccountIgnoreCaseAndStatusNot(String senderAccount, Integer status);

    Optional<SendingAccount> findBySenderAccountIgnoreCaseAndIdNotAndStatusNot(String senderAccount, Long id, Integer status);

    List<SendingAccount> findByStatus (Integer status);

    Optional<SendingAccount> findById (Integer id);

}
