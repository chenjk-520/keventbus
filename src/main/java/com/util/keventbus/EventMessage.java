package com.util.keventbus;

/**
 * 文件描述：处理回调
 * 作者：陈景坤
 * 创建时间：2019/4/30
 * 更改时间：2019/4/30
 */
public class EventMessage<T>{
    //发送处理的消息
   private T msg;
   //事件异常
   private PostEventExeption eventExeption;
   //事件的eventKey
   private String event;
   //同一event，可以按照arg再去分类
   private int arg;
   //处理通知回调
    private CallBack callBack;

    public EventMessage(T msg, String event) {
        this.msg = msg;
        this.event = event;
    }

    public EventMessage(T msg, String event, int arg) {
        this.msg = msg;
        this.event = event;
        this.arg = arg;
    }

    public EventMessage(T msg, String event, CallBack callBack) {
        this.msg = msg;
        this.event = event;
        this.callBack = callBack;
    }

    public EventMessage(T msg, String event, int arg, CallBack callBack) {
        this.msg = msg;
        this.event = event;
        this.arg = arg;
        this.callBack = callBack;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public int getArg() {
        return arg;
    }

    public void setArg(int arg) {
        this.arg = arg;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public T getMsg() {
        return msg;
    }

    public PostEventExeption getEventExeption() {
        return eventExeption;
    }

    public void setEventExeption(PostEventExeption eventExeption) {
        this.eventExeption = eventExeption;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    public static class PostEventExeption{
        int code ; //异常代码
        String des; // 异常描述

        public PostEventExeption(int code, String des) {
            this.code = code;
            this.des = des;
        }
    }

    //回调支持
    public static interface CallBack<H>{
        void onCall(H result, int arg);
    }
}
