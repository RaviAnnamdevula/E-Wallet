package org.jocata.WalletService.repository;

import org.jocata.WalletService.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByContact(String contact);

    @Transactional
    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance +:amount WHERE w.contact = :contact")
    void updateWallet(String contact, double amount);
}
