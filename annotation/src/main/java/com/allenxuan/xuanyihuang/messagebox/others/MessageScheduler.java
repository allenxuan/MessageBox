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

package com.allenxuan.xuanyihuang.messagebox.others;

/**
 * MessageScheduler specifies which thread a method, which is annotated with @MessageReceive, will be executed on.
 * <li>mainThread indicates the execution of the method will be post in main thread queue.</li>
 * <li>workThread indicates the execution of the method will be post in work thread queue.</li>
 * <li>sync indicate the method executes immediately in current thread.</li>
 *
 */
public class MessageScheduler {
    public static final int mainThread = 1;
    public static final int workThread = 2;
    public static final int sync = 3;
}