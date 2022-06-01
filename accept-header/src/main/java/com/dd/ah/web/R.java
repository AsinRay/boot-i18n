package com.dd.ah.web;

import com.dd.ah.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;

@Slf4j
public final class R implements Serializable {


    //@ApiModelProperty(value = "业务响应码",example="0")
    private int code;

    //@ApiModelProperty(value = "业务响应消息",example="Ok")
    private String msg;

    //@ApiModelProperty(allowEmptyValue = true)
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private R(){}

    public static R ok = new R(0,"ok",null);


    private R(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private R(String msg){
        this(-1,msg,null);
    }

    //成功操作 操作码默认为0 无参
    public static R ok() {
        return ok;
    }

    /**
     * Return ok status with data
     */
    public static R ok(Object data){
        return new R(0,"ok",data);
    }

    public static final R fail(String message){
        return new R(message);
    }

    public static R fail(){
        return wrap(9999);
    }
    public static R fail(int code){
        return wrap(code);
    }
    public static R fail(int code ,Object... args){
        return wrap(code,args);
    }


    private static R wrap(int code,Object... args){
        R r = new R();
        r.setCode(code);
        r.setMsg(msg(code+"",args));
        return r;
    }

    /**
     * 通过code 和 message 取i18n message.
     * 如果code配置的有值，则使用args 来格式化占位符，如果没有占位符，则忽略args
     * 如果code没有配置，则直接返回args参数集合的toString.
     * @param code
     * @param args
     * @return
     */
    public static String msg(String code, Object... args) {
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        String msg="";
        try {
            msg = messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException nsme) {
            //msg = Arrays.asList(args).stream().map(Object::toString).reduce(String::concat).get();
            log.warn("code: {} conf missing. please check your messages file.",code);
            msg = "UNKNOWN";
        }
        return msg;
    }
}
