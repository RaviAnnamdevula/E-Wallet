package org.jocata.UserService.repository;

import org.jocata.UserService.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<MyUser, Integer> {

    UserDetails findByPhoneNo(String phoneNo);
}
