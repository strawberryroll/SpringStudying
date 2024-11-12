// UserRepository.java
package com.example.firstproject.repository;

import com.example.firstproject.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUsername(String username); // username으로 UserInfo 조회
}
