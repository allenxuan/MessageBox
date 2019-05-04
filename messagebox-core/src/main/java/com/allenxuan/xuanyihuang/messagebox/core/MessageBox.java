package com.allenxuan.xuanyihuang.messagebox.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
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
        mWorkHandlerThread.start();
        mWorkHandler = new Handler(mWorkHandlerThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());

        mReentrantReadWriteLock = new ReentrantReadWriteLock(true);
        mReadLock = mReentrantReadWriteLock.readLock();
        mWriteLock = mReentrantReadWriteLock.writeLock();

        mReceiverMap = new ConcurrentHashMap<Object, ArrayList<IMessageReceiver>>();
        mMessageMap = new ConcurrentHashMap<Class, HashMap<IMessageReceiver, MessageInfo>>();
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
                    String generatedClassName = String.format("%s$$$$MessageReceiver", it);
                    try {
                        Class<?> generatedClass = Class.forName(generatedClassName);
                        Class<?> originalClass = Class.forName(it);
                        Constructor generatedClassConstructor = generatedClass.getDeclaredConstructor(originalClass);
                        generatedClassConstructor.setAccessible(true);
                        Object generatedClassObj = generatedClassConstructor.newInstance(observer);
                        if (generatedClassObj instanceof IMessageReceiver) {
                            mWriteLock.lock();

                            IMessageReceiver messageReceiver = (IMessageReceiver) generatedClassObj;
                            ArrayList<IMessageReceiver> messageReceivers = mReceiverMap.get(observer);
                            if (messageReceivers == null) {
                                messageReceivers = new ArrayList<IMessageReceiver>();
                                mReceiverMap.put(observer, messageReceivers);
                            }
                            messageReceivers.add(messageReceiver);

                            List<MessageInfo> messageInfos = messageReceiver.messageInfos();
                            for (MessageInfo messageInfo : messageInfos) {
                                HashMap<IMessageReceiver, MessageInfo> specificMessageMap = mMessageMap.get(messageInfo.getMessageClass());
                                if (specificMessageMap == null) {
                                    specificMessageMap = new HashMap<IMessageReceiver, MessageInfo>();
                                    mMessageMap.put(messageInfo.getMessageClass(), specificMessageMap);
                                }
                                specificMessageMap.put(messageReceiver, messageInfo);
                            }

                            mWriteLock.unlock();
                        } else {
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
        mReadLock.lock();
        ArrayList<IMessageReceiver> readMessageReceivers = mReceiverMap.get(observer);
        mReadLock.unlock();

        if (readMessageReceivers != null) {
            mWriteLock.lock();

            ArrayList<IMessageReceiver> messageReceivers = mReceiverMap.remove(observer);
            for (IMessageReceiver messageReceiver : messageReceivers) {
                messageReceiver.invalidateTarget();
                List<MessageInfo> messageInfos = messageReceiver.messageInfos();
                for (MessageInfo messageInfo : messageInfos) {
                    HashMap<IMessageReceiver, MessageInfo> specificMessageMap = mMessageMap.get(messageInfo.getMessageClass());
                    if (specificMessageMap != null) {
                        specificMessageMap.remove(messageReceiver);
                    }
                }
                messageInfos.clear();
            }
            messageReceivers.clear();

            mWriteLock.unlock();

            return true;
        }


        return false;
    }

    /**
     * executeThread:
     * <li>mainThread = 1</li>
     * <li>workThread = 2</li>
     * <li>sync = 3</li>
     */
    public boolean sendMessage(final MessageCarrier messageCarrier) {
        mReadLock.lock();

        HashMap<IMessageReceiver, MessageInfo> specificMessageMap = mMessageMap.get(messageCarrier.getClass());
        if (specificMessageMap != null) {
            Iterator<Map.Entry<IMessageReceiver, MessageInfo>> iterator = specificMessageMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<IMessageReceiver, MessageInfo> entry = iterator.next();
                switch (entry.getValue().getExecuteThread()) {
                    case 1:
                        //mainThread
                        Message mainThreadMessage = Message.obtain(mMainHandler, new Runnable() {
                            @Override
                            public void run() {
                                entry.getKey().dispatchMessage(messageCarrier);
                            }
                        });
                        if(entry.getValue().getExecuteDelay() > 0){
                            mMainHandler.sendMessageDelayed(mainThreadMessage, entry.getValue().getExecuteDelay());
                        }else {
                            mMainHandler.sendMessage(mainThreadMessage);
                        }
                        break;
                    case 2:
                        //workThread
                        Message workThreadMessage = Message.obtain(mWorkHandler, new Runnable() {
                            @Override
                            public void run() {
                                entry.getKey().dispatchMessage(messageCarrier);
                            }
                        });
                        if(entry.getValue().getExecuteDelay() > 0){
                            mMainHandler.sendMessageDelayed(workThreadMessage, entry.getValue().getExecuteDelay());
                        }else {
                            mMainHandler.sendMessage(workThreadMessage);
                        }
                        break;
                    case 3:
                        //sync
                        entry.getKey().dispatchMessage(messageCarrier);
                        break;
                    default:
                        mReadLock.unlock();
                        return false;
                }
            }

            mReadLock.unlock();
            return true;
        }

        mReadLock.unlock();
        return false;
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
