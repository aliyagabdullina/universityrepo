package org.example.repositories;

import org.example.data.AccountData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountData, Integer> {

}
