package com.tianji.learning.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 改变课程状态定时任务
 * @Author: zhao
 * @Since: 2025/7/19 - 23:13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LessonStatusCheckTask {

    private final ILearningLessonService lessonService;

    /**
     * 每分钟一次检查课程状态 V1
     * <p>性能问题：每次都会加载所有未过期的课程到内存中进行判断，即使其中只有少数课程真正过期。</p>
     * <p>冗余更新：即使课程状态未变化，也会发起更新请求，造成数据库不必要的写操作。</p>
     */
    // @Scheduled(cron = "0 * * * * ?")
    public void lessonStatusCheckV1() {
        log.info("开始检查课程状态");
        // 1. 查询状态为 未过期的 课程，无需区分用户
        List<LearningLesson> list = lessonService.list(Wrappers.<LearningLesson>lambdaQuery()
                                                               .ne(LearningLesson::getStatus, LessonStatus.EXPIRED));

        // 2.判断是否过期，过期时间是不是小于当前时间
        LocalDateTime now = LocalDateTime.now();
        list.forEach(lesson -> {
            if (now.isAfter(lesson.getExpireTime())) {
                lesson.setStatus(LessonStatus.EXPIRED);
            }
        });

        // 3.批量更新 过期课程 状态
        lessonService.updateBatchById(list);
    }

    /**
     * 每分钟一次检查课程状态 V2
     * 测试环境，先改为每1h
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void lessonStatusCheckV2() {
        log.info("开始检查课程状态");

        // 直接通过条件更新过期的课程
        lessonService.update(Wrappers.<LearningLesson>lambdaUpdate()
                                     .ne(LearningLesson::getStatus, LessonStatus.EXPIRED)
                                     .le(LearningLesson::getExpireTime, LocalDateTime.now())
                                     .set(LearningLesson::getStatus, LessonStatus.EXPIRED));
    }

}
