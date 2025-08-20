package com.tianji.learning.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tianji.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum QuestionStatus implements BaseEnum {
    // 未查看状态，值为0
    UN_CHECK(0, "未查看"),
    // 已查看状态，值为1
    CHECKED(1, "已查看"),
    ;
    
    // @JsonValue: 用于指定枚举序列化时使用的值，
    // 当将枚举转换为JSON时，会使用value字段的值
    // @EnumValue: MyBatis-Plus注解，用于指定枚举与数据库交互时使用的值
    @JsonValue
    @EnumValue
    int value;
    
    // 枚举的描述信息
    String desc;

    QuestionStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // @JsonCreator: 用于指定反序列化时如何将JSON值转换为枚举实例
    // mode = DELEGATING 表示使用委托模式，即通过of方法将Integer值转换为枚举实例
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static QuestionStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        for (QuestionStatus status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
