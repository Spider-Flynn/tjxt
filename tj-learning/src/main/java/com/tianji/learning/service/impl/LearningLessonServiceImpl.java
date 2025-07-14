package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 * @author zhao
 * @since 2025-07-14
 */
@Service
@RequiredArgsConstructor
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    final CourseClient courseClient;

    /**
     * 添加用户课程
     * @param userId    用户 ID
     * @param courseIds 课程 ID
     */
    @Override
    public void addUserLessons(Long userId, List<Long> courseIds) {
        // 1.根据课程 ID 查询课程信息--feign调用course服务接口
        List<CourseSimpleInfoDTO> courseInfos = courseClient.getSimpleInfoList(courseIds);

        // 2.封装用户课表PO实体类，填充过期时间
        List<LearningLesson> lessons = new ArrayList<>(courseInfos.size());
        for (CourseSimpleInfoDTO courseInfo : courseInfos) {
            LearningLesson lesson = new LearningLesson().setUserId(userId).setCourseId(courseInfo.getId());

            // 获取课程有效期
            Integer validDuration = courseInfo.getValidDuration();
            if (validDuration != null && validDuration > 0) {
                LocalDateTime now = LocalDateTime.now();
                lesson.setCreateTime(now).setExpireTime(now.plusMonths(validDuration));
            }

            lessons.add(lesson);
        }
        // 3.批量保存
        this.saveBatch(lessons);
    }
}
