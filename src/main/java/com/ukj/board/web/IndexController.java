package com.ukj.board.web;

import com.ukj.board.service.PostsService;
import com.ukj.board.web.dto.PostsResponseDto;
import com.ukj.board.web.dto.PostsSaveRequestDto;
import com.ukj.board.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/posts")
    public String postsList(Model model) {
        model.addAttribute("posts", postsService.findAllDesc());
        return "postsList";
    }

    @GetMapping("/posts/new")
    public String createForm(Model model) {
        model.addAttribute("requestDto", new PostsUpdateRequestDto());
        return "createPostsForm";
    }

    @PostMapping("/posts/new")
    public String create(@ModelAttribute PostsSaveRequestDto updateRequestDto) {
        PostsSaveRequestDto saveRequestDto = PostsSaveRequestDto.builder()
                .author(updateRequestDto.getAuthor())
                .title(updateRequestDto.getTitle())
                .content(updateRequestDto.getContent())
                .build();

        postsService.save(saveRequestDto);
        return "redirect:/";
    }

    @GetMapping("/posts/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);
        return "editPostsForm";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute PostsUpdateRequestDto requestDto) {
        postsService.update(id, requestDto);
        return "redirect:/posts";
    }

    @DeleteMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
        postsService.delete(id);
        return "redirect:/posts";
    }
}
