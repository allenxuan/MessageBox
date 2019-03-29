package com.allenxuan.xuanyihuang.messagebox.core;

public class MessageInfo {
    private Class messageClass;
    private boolean executeOnMainThread;
    private boolean executeSynchronously;

    MessageInfo(Class messageClass, boolean executeOnMainThread, boolean executeSynchronously) {
        this.messageClass = messageClass;
        this.executeOnMainThread = executeOnMainThread;
        this.executeSynchronously = executeSynchronously;
    }
}
