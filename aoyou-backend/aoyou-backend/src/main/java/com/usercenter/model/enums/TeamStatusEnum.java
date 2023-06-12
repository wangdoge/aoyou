package com.usercenter.model.enums;

/**
 * Date 2023/6/1 10:05
 * author:wyf
 */
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SCRECT(2,"加密");

    private int value;

    private String text;

    public static TeamStatusEnum getEnumById(Integer value){
        if(value==null){
            return null;
        }
        TeamStatusEnum[]values=TeamStatusEnum.values();
        for(TeamStatusEnum teamStatusEnum:values){
            if(teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value,String text){
        this.text=text;
        this.value=value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
