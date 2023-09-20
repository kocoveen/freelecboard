package com.ukj.freelecboard.web.dto.comments;

import com.ukj.freelecboard.domain.comments.Comments;
import com.ukj.freelecboard.domain.posts.Posts;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentsSaveRequestDto {

    private Long postsId;

    private String content;
    private String author;
    private Posts posts;

    @Builder
    public CommentsSaveRequestDto(String content, String author, Long postsId) {
        this.content = content;
        this.author = author;
        this.postsId = postsId;
    }

    public Comments toEntity() {
        return Comments.builder()
                .content(content)
                .author(author)
                .posts(posts)
                .build();
    }
}