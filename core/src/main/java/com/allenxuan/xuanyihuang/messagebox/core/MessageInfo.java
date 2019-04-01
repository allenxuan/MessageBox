package com.allenxuan.xuanyihuang.messagebox.core;

public class MessageInfo {
    private Class messageClass;
    private int executeThread;
    private int executeDelay;

    /**
     * executeThread:
     * mainThread = 1
     * workThread = 2
     * sync = 3
     */
    public MessageInfo(Class messageClass, int executeThread, int executeDelay) {
        this.messageClass = messageClass;
        this.executeThread = executeThread;
        this.executeDelay = executeDelay;
    }
}
