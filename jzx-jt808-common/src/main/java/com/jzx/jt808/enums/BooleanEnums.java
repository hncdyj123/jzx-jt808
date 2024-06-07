package com.jzx.jt808.enums;

/**
 * Boolean枚举类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
public enum BooleanEnums {
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    /**
     * 失败
     */
    FAIL(0, "失败");

    private Integer key;
    private String value;

    private BooleanEnums(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
