package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.DbException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tianji.common.constants.ErrorInfo.Msg.OPERATE_FAILED;

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

    private final CourseClient courseClient;
    private final CatalogueClient catalogueClient;

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


    /**
     * 分页查询我课程
     * @param query 查询
     * @return {@link PageDTO }<{@link LearningLessonVO }>
     */
    @Override
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();
        // 2.分页查询
        // select * from learning_lesson where user_id = #{userId} order by latest_learn_time limit 0, 5
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        // 3.查询课程信息
        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);

        // 4.封装VO返回
        List<LearningLessonVO> list = new ArrayList<>(records.size());
        // 4.1.循环遍历，把LearningLesson转为VO
        for (LearningLesson r : records) {
            // 4.2.拷贝基础属性到vo
            LearningLessonVO vo = BeanUtils.copyBean(r, LearningLessonVO.class);
            // 4.3.获取课程信息，填充到vo
            CourseSimpleInfoDTO cInfo = cMap.get(r.getCourseId());
            vo.setCourseName(cInfo.getName());
            vo.setCourseCoverUrl(cInfo.getCoverUrl());
            vo.setSections(cInfo.getSectionNum());
            list.add(vo);
        }
        return PageDTO.of(page, list);
    }


    private Map<Long, CourseSimpleInfoDTO> queryCourseSimpleInfoList(List<LearningLesson> records) {
        // 3.1.获取课程id
        Set<Long> cIds = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        // 3.2.查询课程信息
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(cIds);
        if (CollUtils.isEmpty(cInfoList)) {
            // 课程不存在，无法添加
            throw new BadRequestException("课程信息不存在！");
        }
        // 3.3.把课程集合处理成Map，key是courseId，值是course本身
        Map<Long, CourseSimpleInfoDTO> cMap = cInfoList.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        return cMap;
    }

    /**
     * 查询我当前课程
     * @return {@link LearningLessonVO }
     */

    @Override
    public LearningLessonVO queryMyCurrentLesson() {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询正在学习的课程 select * from xx where user_id = #{userId} AND status = 1 order by latest_learn_time limit 1
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if (lesson == null) {
            return null;
        }
        // 3.拷贝PO基础属性到VO
        LearningLessonVO vo = BeanUtils.copyBean(lesson, LearningLessonVO.class);
        // 4.查询课程信息
        CourseFullInfoDTO cInfo = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (cInfo == null) {
            throw new BadRequestException("课程不存在");
        }
        vo.setCourseName(cInfo.getName());
        vo.setCourseCoverUrl(cInfo.getCoverUrl());
        vo.setSections(cInfo.getSectionNum());
        // 5.统计课表中的课程数量 select count(1) from xxx where user_id = #{userId}
        Integer courseAmount = lambdaQuery().eq(LearningLesson::getUserId, userId).count();
        vo.setCourseAmount(courseAmount);
        // 6.查询小节信息
        List<CataSimpleInfoDTO> cataInfos =
                catalogueClient.batchQueryCatalogue(CollUtils.singletonList(lesson.getLatestSectionId()));
        if (!CollUtils.isEmpty(cataInfos)) {
            CataSimpleInfoDTO cataInfo = cataInfos.get(0);
            vo.setLatestSectionName(cataInfo.getName());
            vo.setLatestSectionIndex(cataInfo.getCIndex());
        }
        return vo;
    }

    /**
     * 删除用户课程
     * @param userId    用户 ID
     * @param courseIds 课程 ID
     */
    @Override
    public void removeUserLessons(Long userId, List<Long> courseIds) {
        if (CollUtils.isEmpty(courseIds)) {
            throw new BadRequestException("课程 ID 列表不能为空");
        }

        // 1.判断删除类型执行不同删除策略
        boolean isTrue;
        if (userId == null) {
            // 说明为用户主动删除，获取用户id
            userId = UserContext.getUser();
            // 2.批量删除 delete from learning_lesson where user_id = #{userId} and course_id in (#{courseIds}) and status = 2
            isTrue = remove(buildDeleteCondition(userId, courseIds, LessonStatus.FINISHED.getValue()));
        } else {
            // 说明为订单退款后的删除课程
            // 2.批量删除 delete from learning_lesson where course_id in (#{courseIds}) and user_id = #{userId}
            isTrue = remove(buildDeleteCondition(userId, courseIds, null));
        }

        if (!isTrue) {
            throw new DbException(OPERATE_FAILED);
        }
    }

    private LambdaQueryWrapper<LearningLesson> buildDeleteCondition(Long userId, List<Long> courseIds, Integer status) {
        LambdaQueryWrapper<LearningLesson> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(LearningLesson::getCourseId, courseIds).eq(userId != null, LearningLesson::getUserId, userId);
        if (status != null) {
            wrapper.eq(LearningLesson::getStatus, status);
        }
        return wrapper;
    }

}
