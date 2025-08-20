package com.tianji.learning.service;

import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.Valid;

/**
 * <p>
 * 互动提问的问题表 服务类
 * </p>
 *
 * @author zhao
 * @since 2025-08-20
 */
public interface IInteractionQuestionService extends IService<InteractionQuestion> {

    void saveQuestion(@Valid QuestionFormDTO questionDTO);
}
