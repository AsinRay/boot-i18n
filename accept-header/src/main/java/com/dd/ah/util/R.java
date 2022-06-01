package com.dd.ah.util;

import java.io.Serializable;

public final class R implements Serializable {


    private String code;
    private String msg;


    private R(){}
    private R(String msg){
        this("0",msg);
    }
    private R(String code ,String msg){
        this.code = code;
        this.msg = msg;
    }


    public static final R fail(String message){
        return new R(message);
    }
}
