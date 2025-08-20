package com.tianji.learning.controller;


import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 互动提问的问题表 前端控制器
 * </p>
 * @author zhao
 * @since 2025-08-20
 */
@Api(tags = "互动提问相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class InteractionQuestionController {

    private final IInteractionQuestionService questionService;


    @ApiOperation("新增提问")
    @PostMapping
    public void saveQuestion(@Valid @RequestBody QuestionFormDTO questionDTO) {
        questionService.saveQuestion(questionDTO);
    }

    @ApiOperation("修改问题")
    @PostMapping("/{id}")
    public void updateQuestion(@PathVariable Long id, @RequestBody QuestionFormDTO questionDTO) {
        questionService.updateQuestion(id, questionDTO);
    }
}
