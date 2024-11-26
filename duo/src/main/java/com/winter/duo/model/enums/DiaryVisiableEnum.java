package com.winter.duo.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日记可见性枚举  0 - 私密 1 - 伴侣可见 2 - 公开
 */
@Getter
public enum DiaryVisiableEnum {

    PRIVATE("私密", 0),
    PARTNER("伴侣可见", 1),
    PUBLIC("公开", 2);

    private final String text;

    private final Integer value;

    DiaryVisiableEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static DiaryVisiableEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DiaryVisiableEnum anEnum : DiaryVisiableEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
