package com.ukj.freelecboard.domain.posts.controller;

import com.ukj.freelecboard.domain.posts.SubjectType;
import com.ukj.freelecboard.domain.posts.service.PostsService;
import com.ukj.freelecboard.domain.posts.dto.PostsResponseDto;
import com.ukj.freelecboard.domain.posts.dto.PostsSaveRequestDto;
import com.ukj.freelecboard.domain.posts.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @GetMapping
    public String postsList(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
                            @RequestParam(value = "sType", required = false, name = "sType") SubjectType sType,
                            @RequestParam(value = "keyword", required = false) String keyword) {

        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("sType", sType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("posts", postsService.searchPagingList(pageNumber, sType, keyword));

        return "postsList";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @ModelAttribute(name = "errorComment") String errorMessage) {
        model.addAttribute("posts", postsService.findById(id));
        return "postsDetail";
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute(name = "posts") PostsSaveRequestDto requestDto) {
        return "createPostsForm";
    }

    @PostMapping("/new")
    public String create(@Validated @ModelAttribute(name = "posts") PostsSaveRequestDto requestDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "createPostsForm";
        }

        PostsSaveRequestDto saveRequestDto = PostsSaveRequestDto.builder()
                .authorName(principal.getName())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        Long postId = postsService.save(saveRequestDto);

        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/posts/{postId}";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model, Principal principal) {
        PostsResponseDto responseDto = postsService.findById(id);

        if (responseDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물은 존재하지 않습니다.");
        }

        // 페치조인으로 연관관계를 전부 끌고 오는 문제
        if (!principal.getName().equals(responseDto.getAuthorName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        model.addAttribute("posts", responseDto);
        return "editPostsForm";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Validated @ModelAttribute(name = "posts") PostsUpdateRequestDto requestDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {

        // 권한 검사
        PostsResponseDto responseDto = postsService.findById(id);
        if (responseDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물은 존재하지 않습니다.");
        }
        if (!principal.getName().equals(responseDto.getAuthorName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        // 데이터 검사
        if (bindingResult.hasErrors()){
            return "editPostsForm";
        }

        postsService.update(id, requestDto);
        redirectAttributes.addAttribute("postId", id);

        return "redirect:/posts/{postId}";
    }

    @GetMapping("/{id}/vote")
    public String vote(@PathVariable Long id, Principal principal) {
        postsService.vote(id, principal.getName());
        return "redirect:/posts/{id}";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        PostsResponseDto responseDto = postsService.findById(id);

        if (responseDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물은 존재하지 않습니다.");
        }

        if (!principal.getName().equals(responseDto.getAuthorName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        postsService.delete(id);
        return "redirect:/posts";
    }

    @ModelAttribute("subjectTypes")
    public SubjectType[] subjectTypes() {
        return SubjectType.values();
    }
}
