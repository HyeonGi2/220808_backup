package com.team.chatproject.controller;

import com.team.chatproject.domain.Article;

import com.team.chatproject.domain.ResultData;
import com.team.chatproject.domain.Rq;
import com.team.chatproject.form.ArticleForm;
import com.team.chatproject.form.CommentForm;
import com.team.chatproject.service.ArticleService;

import com.team.chatproject.util.Ut;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private Rq rq;


    // 전체 조회
    @RequestMapping("/list")
    public String showList(Model model) {
        List<Article> articles = articleService.getList();
        model.addAttribute("articles", articles);
        log.info(articles.toString());
        return "/article/article_list";
    }

    // 상세 조회
    @RequestMapping("/detail/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Article article = articleService.getDetail(id);
        model.addAttribute("article", article);
        return "/article/article_detail";
    }

    // 게시글 생성
    @GetMapping("/new")
    public String newArticle() {
        return "/article/article_new";
    }

    @PostMapping("/create")
    public String createArticle(Model model, @Validated ArticleForm articleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> validationErrors = bindingResult.getFieldErrors()
                    .stream().collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            if (!validationErrors.isEmpty()) {
                model.addAttribute("validationErrors", validationErrors);
            }
            return "/article/article_new";
        }
        this.articleService.create(articleForm.getTitle(), articleForm.getBody());
        return "redirect:/article/list";
    }
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable Long id) {
        Article article = this.articleService.getDetail(id);
        this.articleService.delete(article);
        return "redirect:/article/list";
    }

    @GetMapping("/modify/{id}")
    public  String  showModifyArticle (Model model,@PathVariable Long id ){
        Article article = articleService.getDetail(id);
        if(article==null){
            return Ut.jsHistoryBack("게시물이 없습니다.");
        }
        model.addAttribute("article", article);
        return "/article/article_modify";
    }

    @PostMapping("/modify/{id}")
    public String doModifyArticle(Model model, @PathVariable Long id ,@Validated ArticleForm articleForm, BindingResult bindingResult) {
        Article article = articleService.getDetail(id);
        if (bindingResult.hasErrors()) {
            Map<String, String> validationErrors = bindingResult.getFieldErrors()
                    .stream().collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            if (!validationErrors.isEmpty()) {
                model.addAttribute("validationErrors", validationErrors);
            }
            return "/article/article_modify";
        }
        this.articleService.modify(id, articleForm.getTitle(), articleForm.getBody());
        return "redirect:/article/list";

    }
}
