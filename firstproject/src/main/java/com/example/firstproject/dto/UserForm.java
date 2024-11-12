//UserForm.java
package com.example.firstproject.dto;

import com.example.firstproject.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class UserForm {
    private String username; // 아이디를 받을 필드
    private String pwd; // 비밀번호를 받을 필드

    public UserInfo toEntity() {
        return new UserInfo(null, username, pwd);
    }
}