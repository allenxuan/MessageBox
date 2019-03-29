/*
 * Copyright (C) 2019 Xuanyi Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allenxuan.xuanyihuang.messagebox.annotation;

import com.allenxuan.xuanyihuang.messagebox.others.MessageScheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the method annotated with @MessageReceive will be invoked after a specific message is sent via MessageBox.sendMessage()
 * <li>executeThread specifies which thread this method will be executed on. for more details, please refer to {@link com.allenxuan.xuanyihuang.messagebox.others.MessageScheduler}</li>
 * <li>executeDelay specifies the delay time before this method is executed</li>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MessageReceive {
    int executeThread() default MessageScheduler.sync;

    int executeDelay() default 0;
}
