package com.zhangyiwen.caviar.protocol.enu;

/**
 * Created by zhangyiwen on 2017/12/14.
 */
public enum CodecTypeEnum {

    JSON(1),
    XML(2);

    private int code;

    CodecTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CodecTypeEnum getByCode(int code){
        for(CodecTypeEnum codecTypeEnum:CodecTypeEnum.values()){
            if(codecTypeEnum.getCode() == code){
                return codecTypeEnum;
            }
        }
        return null;
    }

}
