package com.winter.duo.model.dto.comment;

import com.winter.duo.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentPageRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}