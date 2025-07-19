package com.tianji.learning.controller;


import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.dto.LearningRecordFormDTO;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 * @author zhao
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/learning-records")
@Api(tags = "学习记录的相关接口")
@RequiredArgsConstructor
public class LearningRecordController {

    private final ILearningRecordService learningRecordService;

    /**
     * 查询用户指定课程 学习进度
     * @param courseId
     * @return
     */
    @ApiOperation("查询指定课程的学习记录")
    @GetMapping("/course/{courseId}")
    public LearningLessonDTO queryLearningRecordByCourse(@ApiParam(value = "课程id", example = "2") @PathVariable Long courseId) {
        return learningRecordService.queryLearningRecordByCourse(courseId);
    }

    /**
     * 提交学习记录
     * @param recordDTO
     */
    @ApiOperation("提交学习记录")
    @PostMapping
    public void addLearningRecord(@RequestBody LearningRecordFormDTO recordDTO) {
        learningRecordService.addLearningRecord(recordDTO);
    }
}
