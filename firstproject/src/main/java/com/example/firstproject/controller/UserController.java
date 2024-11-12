// UserController.java
package com.example.firstproject.controller;

import com.example.firstproject.dto.UserForm;
import com.example.firstproject.entity.UserInfo;
import com.example.firstproject.repository.UserRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j // 로깅 기능을 위한 어노테이션 추가
@Controller
public class UserController {
    @Autowired // 스프링 부트가 미리 생성해 놓은 리파지터리 객체 주입(DI)
    private UserRepository userRepository;

    // 메인 페이지
    @GetMapping("/main")
    public String mainPage() {
        return "user/main";
    }

    // 회원가입 페이지
    @GetMapping("/user/signup")
    public String signupForm() {
        return "user/signup";
    }

    // 회원가입 처리
    @PostMapping("/user/create")
    public String signup(UserForm form, RedirectAttributes rttr) { // 폼 데이터를 DTO로 받는다
        log.info(form.toString());
        // System.out.println(form.toString());

        // 아이디와 비밀번호가 입력되지 않았을 때 처리
        if (form.getUsername() == null || form.getUsername().isEmpty() ||
            form.getPwd() == null || form.getPwd().isEmpty()) {
            rttr.addFlashAttribute("error", "아이디와 비밀번호를 입력해주세요.");
            return "redirect:/user/signup";
        }
        // 아이디가 중복됐을 때 처리
        if (userRepository.findByUsername(form.getUsername()) != null) {
            rttr.addFlashAttribute("error", "이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
            return "redirect:/user/signup";
        }

        // 1. DTO를 엔티티로 변환
        UserInfo userInfo = form.toEntity();
        log.info(userInfo.toString());
        // System.out.println(userInfo.toString()); // DTO가 엔티티로 잘 변환되는지 확인 출력

        // 2. 리파지터리로 엔티티를 DB에 저장
        UserInfo saved = userRepository.save(userInfo); // userInfo 엔티티를 저장해 saved 객체에 반환
        log.info(saved.toString());
        // System.out.println(saved.toString()); // userInfo가 DB에 잘 저장되는지 확인 출력

        rttr.addFlashAttribute("success","가입 완료!"); // RedirectAttributes는 리다이렉트 페이지에서 사용할 일회성 데이터를 관리하는 객체.

        // 3. 로그인 페이지로 리다이렉트하기
        return "redirect:/user/login";
    }

    // 로그인 페이지
    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";
    }

    // 로그인 처리
    @PostMapping("/user/check")
    public String login(UserForm form, RedirectAttributes rttr) {
        UserInfo userInfo = userRepository.findByUsername(form.getUsername()); // 입력된 아이디로 조회
        log.info(userInfo.toString());

        if (userInfo != null && userInfo.getPwd().equals(form.getPwd())) {
            rttr.addFlashAttribute("success","로그인 완료!");
            return "redirect:/articles"; // 게시판 페이지로 리다이렉트하기
        }
        else {
            rttr.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/user/login"; // 일치하지 않으면 에러 메세지 띄우고 로그인 페이지로 리다이렉트
        }
    }
}
