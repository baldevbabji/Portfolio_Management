package com.sonata.portfoliomanagement.interfaces;

import com.sonata.portfoliomanagement.model.MD_Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MD_AccountsRepository extends JpaRepository<MD_Accounts, Integer> {
}
