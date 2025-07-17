package com.tianji.learning.mq;

import com.tianji.api.dto.trade.OrderBasicDTO;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description: MQ消息监听器
 * @Author: zhao
 * @Since: 2025/7/14 - 17:49
 */
@Component
@Slf4j
// 使用构造器 Lombok是在编译期生成相应的方法
@RequiredArgsConstructor
public class LessonChangeListener {

    private final ILearningLessonService lessonService;

    /**
     * 监听课程订单支付消息
     * <p>用于将购买的课程加入“我的课程”中</p>
     * @param dto
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "learning.lesson.pay.queue", durable = "true"),
                                             exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE,
                                                                  type = ExchangeTypes.TOPIC),
                                             key = MqConstants.Key.ORDER_PAY_KEY))
    public void listenLessonPay(OrderBasicDTO dto) {
        log.info("LessonChangeListener 接收到了消息 用户{},添加课程{}", dto.getUserId(), dto.getCourseIds());

        // 1.健壮性-校验
        if (dto.getUserId() == null || dto.getOrderId() == null || CollUtils.isEmpty(dto.getCourseIds())) {
            // 不能抛异常，业务逻辑错误，抛异常会出发MQ重试机制
            return;
        }

        // 2.调用service，保存课程到课表
        lessonService.addUserLessons(dto.getUserId(), dto.getCourseIds());
    }

    /**
     * 监听课程订单取消消息
     * <p>用于将取消的课程从“我的课程”中移除</p>
     * @param dto
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "learning.lesson.refund.queue", durable = "true"),
                                             exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE,
                                                                  type = ExchangeTypes.TOPIC),
                                             key = MqConstants.Key.ORDER_REFUND_KEY))
    public void listenLessonRefund(OrderBasicDTO dto) {
        log.info("LessonChangeListener 监听到课程订单取消消息，用户{},课程{}", dto.getUserId(), dto.getCourseIds());

        // 1.健壮性-校验
        if (dto.getUserId() == null || dto.getOrderId() == null || CollUtils.isEmpty(dto.getCourseIds())) {
            // 不能抛异常，业务逻辑错误，抛异常会出发MQ重试机制
            return;
        }

        // 2.调用service，删除课程
        lessonService.removeUserLessons(dto.getUserId(), dto.getCourseIds());
    }
}
