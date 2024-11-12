package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Slf4j // 로깅 기능을 위한 어노테이션 추가
@Controller
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form) { // 폼 데이터를 DTO로 받기
        log.info(form.toString()); // 로깅 코드 추가
        //System.out.println(form.toString()); // DTO에 폼 데이터가 잘 담겼는지 확인

        // 1. DTO를 엔티티로 변환
        Article article = form.toEntity();
        log.info(article.toString());
        //System.out.println(article.toString());
        // 2. 리파지터리로 엔티티를 DB에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        //System.out.println(saved.toString());

        return "redirect:/articles/" + saved.getId(); // 리다이렉트를 작성(id 값을 가져오기 위해 saved 객체 이용)
    }

    @GetMapping("/articles/{id}") // 데이터 조회 요청 접수
    public String show(@PathVariable Long id, Model model) { // 매개변수로 id, model 받아오기
        log.info("id = " + id); // id를 잘 받았는지 확인하는 로그 찍기
        // 1. id를 조회해 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null); // findById(): 엔티티의 id 값을 기준으로 데이터를 찾아 Optional 타입으로 반환
        // 2. 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity); // article이라는 이름으로 articleEntity 객체 등록
        // 3. 뷰 페이지 반환하기
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model) {
        // 1. 모든 데이터 가져오기
        ArrayList<Article> articleEntityList = articleRepository.findAll();
        // 2. 모델에 데이터 등록하기
        model.addAttribute("articleList", articleEntityList);
        // 3. 뷰 페이지 설정하기
        return "articles/index";
    }

    @GetMapping("/articles/{id}/edit") // URL 요청 접수
    public String edit(@PathVariable Long id, Model model) { // id를 매개변수로 받아오기, model 객체 받아오기
        // 수정할 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null); // DB에서 수정할 데이터 가져오기
        // 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity); // articleEntity를 article로 등록
        // 뷰 페이지 설정하기
        return "articles/edit";
    }

    @PostMapping("/articles/update") // URL 요청 접수
    public String update(ArticleForm form) { // 매개변수로 DTO 받아 오기
        log.info(form.toString()); // 수정 데이터를 잘 받았는지 확인
        // 1. DTO를 엔티티로 변환하기
        Article articleEntity = form.toEntity(); // ArticleForm 클래스에서 만든 DTO를 엔티티로 변환하는 toEntity() 메서드 호출
        log.info(articleEntity.toString()); // 엔티티로 잘 변환됐는지 로그 찍기
        // 2. 엔티티를 DB에 저장하기
        // 2-1. DB에서 기존 데이터 가져오기
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null); // findById(): 리파지터리가 자동으로 제공하는 메서드, 괄호 안에는 찾는 id 값을 작성. 앞에서 가져온 articleEntity에 getId() 메서드를 호출해 id 값을 집어넣는다. 메서드를 호출해 반환받은 데이터를 Article 타입의 target 변수에 저장.
        // 2-2. 기존 데이터 값을 갱신하기
        if (target != null) {
            articleRepository.save(articleEntity); // 엔티티를 DB에 저장(갱신)
        }
        // 3. 수정 결과 페이지로 리다이렉트하기
        return "redirect:/articles/" + articleEntity.getId(); // URL의 id 부분은 엔티티에 따라 매번 바뀜
    }

    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr) { // id를 매개변수로 가져오기
        log.info("삭제 요청이 들어왔습니다!!");
        // 1. 삭제할 대상 가져오기
        Article target = articleRepository.findById(id).orElse(null); // 데이터 찾기
        log.info(target.toString());
        // 2. 대상 엔티티 삭제하기
        if (target != null) { // 삭제할 대상이 있는지 확인
            articleRepository.delete(target); // delete() 메서드로 대상 삭제
            rttr.addFlashAttribute("msg","삭제됐습니다!"); // RedirectAttributes는 리다이렉트 페이지에서 사용할 일회성 데이터를 관리하는 객체. 이 객체의 addFlashAttribute() 메서드로 리다이렉트된 페이지에서 사용할 일회성 데이터를 등록.
        }
        // 3. 결과 페이지로 리다이렉트하기
        return "redirect:/articles";
    }

}