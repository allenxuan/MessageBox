package com.allenxuan.xuanyihuang.messagebox.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *mainThread = 1
 *workThread = 2
 *sync = 3
 */
public class MessageBox {
    private HandlerThread mWorkHandlerThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    private ReentrantReadWriteLock mReentrantReadWriteLock;
    private ReentrantReadWriteLock.ReadLock mReadLock;
    private ReentrantReadWriteLock.WriteLock mWriteLock;

    private ConcurrentHashMap<Object, ArrayList<IMessageReceiver>> mReceiverMap;
    private ConcurrentHashMap<Class, HashMap<IMessageReceiver, MessageInfo>> mMessageMap;

    private MessageBox(){
        mWorkHandlerThread = new HandlerThread("WorkHandlerThread");
        mWorkHandler = new Handler(mWorkHandler.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());

        mReentrantReadWriteLock = new ReentrantReadWriteLock(true);
        mReadLock = mReentrantReadWriteLock.readLock();
        mWriteLock = mReentrantReadWriteLock.writeLock();

        mReceiverMap = new ConcurrentHashMap<Object, ArrayList<IMessageReceiver>>();
        mMessageMap = new ConcurrentHashMap<Class, HashMap<IMessageReceiver, MessageInfo>>();

        mWorkHandlerThread.start();
    }

    public boolean subscribe(Object observer)
    {
        return true;
    }

    public boolean unSubscribe(Object observer){
        return true;
    }

    public boolean sendMessage(MessageCarrier messageCarrier){
        return true;
    }


    public static MessageBox INSTANCE(){
        return SingletonHolder.singleton;
    }

    private static class SingletonHolder{
        private static MessageBox singleton = new MessageBox();
    }
}
