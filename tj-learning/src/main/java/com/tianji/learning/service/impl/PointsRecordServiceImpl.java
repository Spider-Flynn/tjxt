package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.common.utils.DateUtils;
import com.tianji.learning.domain.po.PointsRecord;
import com.tianji.learning.enums.PointsRecordType;
import com.tianji.learning.mapper.PointsRecordMapper;
import com.tianji.learning.service.IPointsRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 学习积分记录，每个月底清零 服务实现类
 * </p>
 * @author zhao
 * @since 2025-08-26
 */
@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {

    /**
     * 新增积分明细
     * @param userId 用户id
     * @param points 积分值
     * @param type   积分类型·1
     */
    @Override
    public void addPointsRecord(Long userId, int points, PointsRecordType type) {
        LocalDateTime now = LocalDateTime.now();
        int maxPoints = type.getMaxPoints();
        // 1.判断当前方式有没有积分上限
        int realPoints = points;
        if (maxPoints > 0) {
            // 2.有，则需要判断是否超过上限
            LocalDateTime begin = DateUtils.getDayStartTime(now);
            LocalDateTime end = DateUtils.getDayEndTime(now);
            // 2.1.查询今日已得积分
            int currentPoints = queryUserPointsByTypeAndDate(userId, type, begin, end);
            // 2.2.判断是否超过上限
            if (currentPoints >= maxPoints) {
                // 2.3.超过，直接结束
                return;
            }
            // 2.4.没超过，保存积分记录
            if (currentPoints + points > maxPoints) {
                realPoints = maxPoints - currentPoints;
            }
        }
        // 3.没有，直接保存积分记录
        PointsRecord p = new PointsRecord();
        p.setPoints(realPoints);
        p.setUserId(userId);
        p.setType(type);
        save(p);
    }

    private int queryUserPointsByTypeAndDate(Long userId, PointsRecordType type, LocalDateTime begin, LocalDateTime end) {
        // 1.查询条件
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(PointsRecord::getUserId, userId)
                .eq(type != null, PointsRecord::getType, type)
                .between(begin != null && end != null, PointsRecord::getCreateTime, begin, end);
        // 2.调用mapper，查询结果
        Integer points = getBaseMapper().queryUserPointsByTypeAndDate(wrapper);
        // 3.判断并返回
        return points == null ? 0 : points;
    }
}
