package com.ukj.freelecboard.domain.posts.dto;

import com.ukj.freelecboard.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostsListResponseDto {

    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdDate;
    private int commentsSize;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.authorName = entity.getAuthor().getUsername();
        this.createdDate = entity.getCreatedDate();
        this.commentsSize = entity.getCommentsCount();
    }
}