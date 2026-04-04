package com.tianji.remark.task;

import com.tianji.remark.service.ILikedRecordService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikedTimesCheckTask {

    private static final List<String> BIZ_TYPES = List.of("QA", "NOTE");
    private static final int MAX_BIZ_SIZE = 30;

    private final ILikedRecordService recordService;

    /**
     * 检查点赞总数并发送MQ消息
     * 每20秒检查一次
     * Spring Task：每20秒执行一次
     */
    /* @Scheduled(fixedDelay = 20000)
    public void checkLikedTimes() {
        log.debug("检查点赞总数并发送MQ消息");
        for (String bizType : BIZ_TYPES) {
            recordService.readLikedTimesAndSendMessage(bizType, MAX_BIZ_SIZE);
        }
    } */

    /**
     * 检查点赞总数并发送MQ消息
     * 在XXL-JOB管理后台配置cron表达式，如：20****? （每20秒执行）
     */
    @XxlJob("checkLikedTimes")
    public void checkLikedTimes() {
        log.debug("检查点赞总数并发送MQ消息");
        for (String bizType : BIZ_TYPES) {
            try {
                recordService.readLikedTimesAndSendMessage(bizType, MAX_BIZ_SIZE);
            } catch (Exception e) {
                log.error("处理业务类型 {} 的点赞数据失败", bizType, e);
            }
        }
    }
}
