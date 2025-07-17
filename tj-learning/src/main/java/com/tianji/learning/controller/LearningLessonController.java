package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 * @author zhao
 * @since 2025-07-14
 */
@Api(tags = "我的课表相关接口")
@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LearningLessonController {

    private final ILearningLessonService lessonService;

    /**
     * 查询我课程
     * @param query 查询
     * @return {@link PageDTO }<{@link LearningLessonVO }>
     */
    @ApiOperation("查询我的课表，排序字段 latest_learn_time:学习时间排序，create_time:购买时间排序")
    @GetMapping("/page")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        return lessonService.queryMyLessons(query);
    }


    /**
     * 查询最近学习的课程
     * @return {@link LearningLessonVO }
     */
    @ApiOperation("查询最近学习的课程")
    @GetMapping("/now")
    public LearningLessonVO queryMyCurrentLesson() {
        return lessonService.queryMyCurrentLesson();
    }

    /**
     * 删除用户课程
     * @param courseId 课程 ID
     */
    @ApiOperation("删除已学习完的课程")
    @DeleteMapping("/{courseId}")
    public void removeUserLessons(@ApiParam(value = "课程 ID", example = "1") @PathVariable Long courseId) {
        lessonService.removeUserLessons(null, courseId);
    }
}
