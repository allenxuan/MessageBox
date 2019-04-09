package com.allenxuan.xuanyihuang.messagebox.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * mainThread = 1
 * workThread = 2
 * sync = 3
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

    private MessageBox() {
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

    public boolean subscribe(Object observer) {
        if (mReceiverMap.get(observer) != null || !annotatedWithMessageReceiver(observer)) {
            return false;
        } else {
            Method[] methods = observer.getClass().getMethods();
            if (methods != null) {
                HashSet<String> declaringClassNames = new HashSet<String>();
                for (Method method : methods) {
                    if (method.getAnnotation(MessageReceive.class) != null) {
                        String declaringClassName = method.getDeclaringClass().getName();
                        if (declaringClassName != null && declaringClassName.length() > 0) {
                            declaringClassNames.add(declaringClassName);
                        }
                    }
                }
                for (String it : declaringClassNames) {
                    String generatedClassName = String.format("%s$$$MessageReceiver", it);
                    try {
                        Class<?> generatedClass = Class.forName(generatedClassName);
                        Class<?> originalClass = Class.forName(it);
                        Constructor generatedClassConstructor = generatedClass.getDeclaredConstructor(originalClass);
                        generatedClassConstructor.setAccessible(true);
                        Object generatedClassObj = generatedClassConstructor.newInstance(observer);
                        if(generatedClassObj instanceof IMessageReceiver){
                            mWriteLock.lock();

                            IMessageReceiver messageReceiver = (IMessageReceiver) generatedClassObj;
                            ArrayList<IMessageReceiver> messageReceivers = mReceiverMap.get(observer);
                            if(messageReceivers == null){
                                messageReceivers = new ArrayList<IMessageReceiver>();
                                mReceiverMap.put(observer, messageReceivers);
                            }
                            messageReceivers.add(messageReceiver);

                            List<MessageInfo> messageInfos = messageReceiver.messageInfos();
                            for(MessageInfo messageInfo: messageInfos){
                                HashMap<IMessageReceiver, MessageInfo> specificMessageMap = mMessageMap.get(messageInfo.getMessageClass());
                                if(specificMessageMap == null){
                                    specificMessageMap = new HashMap<IMessageReceiver, MessageInfo>();
                                    mMessageMap.put(messageInfo.getMessageClass(), specificMessageMap);
                                }
                                specificMessageMap.put(messageReceiver, messageInfo);
                            }

                            mWriteLock.unlock();
                        }else {
                            return false;
                        }
                    } catch (Throwable throwable) {
                        Log.e("MessageBox", "subscribe() -> access generated class "
                                + generatedClassName + " error, cause: " + throwable.getCause() + ", message: " + throwable.getMessage(), throwable);
                        throwable.printStackTrace();

                        return false;
                    }
                }

                return true;
            }
        }


        return false;
    }

    public boolean unSubscribe(Object observer) {
        return true;
    }

    public boolean sendMessage(MessageCarrier messageCarrier) {
        return true;
    }

    private boolean annotatedWithMessageReceiver(Object observer) {
        Method[] methods = observer.getClass().getMethods();
        if (methods != null) {
            for (Method method : methods) {
                if (method.getAnnotation(MessageReceive.class) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static MessageBox INSTANCE() {
        return SingletonHolder.singleton;
    }

    private static class SingletonHolder {
        private static MessageBox singleton = new MessageBox();
    }
}
