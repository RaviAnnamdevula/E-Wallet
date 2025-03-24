package org.jocata.TxnService.repository;

import org.jocata.TxnService.model.Txn;
import org.jocata.TxnService.model.TxnStatus;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TxnRepository extends JpaRepository<Txn, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE Txn t SET t.txnStatus =:status ,t.message =:message WHERE t.txnId = :txnId")
    void updateTxnStatus(String txnId, TxnStatus status, String message);


    Page<Txn> findBySenderId(String sender , Pageable pageable);
}
