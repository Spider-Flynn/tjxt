package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 学习记录表 服务实现类
 * </p>
 * @author zhao
 * @since 2025-07-18
 */
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    private final LearningLessonServiceImpl lessonService;

    /**
     * 查询指定课程的学习记录
     * @param courseId
     * @return
     */
    @Override
    public LearningLessonDTO queryLearningRecordByCourse(Long courseId) {
        // 1.获取用户id
        Long userId = UserContext.getUser();

        // 2.查询课程信息 - 前端给的courseId，record表中没有，只有课表id，所以需要查询课表信息
        LearningLesson lesson = lessonService.queryByUserAndCourseId(userId, courseId);
        if (lesson == null) {
            throw new BizIllegalException("该课程未加入到课表");
        }

        // 3.查询学习记录
        List<LearningRecord> records = lambdaQuery().eq(LearningRecord::getLessonId, lesson.getId()).list();

        // 4.封装VO
        return new LearningLessonDTO()
                .setId(lesson.getId())
                .setLatestSectionId(lesson.getLatestSectionId())
                .setRecords(BeanUtils.copyList(records, LearningRecordDTO.class));
    }
}
