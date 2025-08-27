package com.tianji.learning.constants;

/**
 * @Description: 签到相关的redis常量
 * @Author: zhao
 * @Since: 2025/8/27 - 10:00
 */
public class RedisConstants {
    /**
     * 签到记录的key：sign:uid:13:202508
     */
    public static final String SIGN_RECORD_KEY_PREFIX = "sign:uid:";
    /**
     * 积分板的key：boards:   uid:score
     */
    public static final String POINTS_BOARD_KEY_PREFIX = "boards:";
}
