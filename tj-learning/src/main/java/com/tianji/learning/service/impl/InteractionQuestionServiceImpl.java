package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.tianji.learning.mapper.InteractionQuestionMapper;
import com.tianji.learning.service.IInteractionQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 互动提问的问题表 服务实现类
 * </p>
 * @author zhao
 * @since 2025-08-20
 */
@Service
public class InteractionQuestionServiceImpl extends ServiceImpl<InteractionQuestionMapper, InteractionQuestion> implements IInteractionQuestionService {

    /**
     * 新增提问
     * @param questionDTO 问题表单
     */
    @Override
    @Transactional
    public void saveQuestion(QuestionFormDTO questionDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.数据转换
        InteractionQuestion question = BeanUtils.toBean(questionDTO, InteractionQuestion.class);
        // 3.补充数据
        question.setUserId(userId);
        // 4.保存问题
        save(question);
    }

    /**
     * 修改问题
     * @param id          问题id
     * @param questionDTO 问题表单
     */
    @Override
    public void updateQuestion(Long id, QuestionFormDTO questionDTO) {

        // 1.校验参数
        if (StringUtils.isBlank(questionDTO.getDescription()) || StringUtils.isBlank(questionDTO.getTitle()) || questionDTO.getAnonymity()) {
            throw new BadRequestException("问题描述或标题不能为空");
        }
        // 2.根据id查询问题
        InteractionQuestion question = getById(id);
        // 3.校验问题是否存在
        if (question == null) {
            throw new BadRequestException("问题不存在");
        }
        // 4.校验问题是否属于当前用户
        if (!question.getUserId().equals(UserContext.getUser())) {
            throw new BadRequestException("问题不属于当前用户");
        }
        // 5.数据转换
        BeanUtils.copyProperties(questionDTO, question);
        // 6.更新问题
        updateById(question);
    }
}
