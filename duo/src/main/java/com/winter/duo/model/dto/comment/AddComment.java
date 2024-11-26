package com.winter.duo.model.dto.comment;

import lombok.Data;

/**
 * @author winter
 * @create 2024-03-12 11:20
 */
@Data
public class AddComment {
    private String content;
    private Long postId;

}
