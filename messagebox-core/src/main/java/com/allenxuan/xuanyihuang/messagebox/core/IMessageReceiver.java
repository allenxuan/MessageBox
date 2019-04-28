package com.allenxuan.xuanyihuang.messagebox.core;

import java.util.List;

public interface IMessageReceiver {
    public abstract void dispatchMessage(MessageCarrier message);

    public abstract List<MessageInfo> messageInfos();

    public abstract void invalidateTarget();
}
