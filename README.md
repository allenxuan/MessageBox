# MessageBox
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/index.html)
[![License](https://img.shields.io/badge/Version-1.9.4-blue.svg)](https://jcenter.bintray.com/com/github/allenxuan/)
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](http://www.apache.org/licenses/LICENSE-2.0)

#### Messagebox is a lightweight message/event framework for Android.

### Screenshot
![demo1](/screenshot/demo.gif)

### Dependencies:
```groovy
//Kotlin
dependencies {
    implementation 'com.github.allenxuan.MessageBox:messagebox-annotation:1.9.4'
    implementation 'com.github.allenxuan.MessageBox:messagebox-core:1.9.4'
    kapt 'com.github.allenxuan.MessageBox:messagebox-compiler:1.9.4'
}

//Java
dependencies {
    implementation 'com.github.allenxuan.MessageBox:messagebox-annotation:1.9.4'
    implementation 'com.github.allenxuan.MessageBox:messagebox-core:1.9.4'
    annotationProcessor 'com.github.allenxuan.MessageBox:messagebox-compiler:1.9.4'
}
```

### Usages
To obtain the ability of receiving messages, subscribe and unSubscribe the target object at appropriate places, usually within onCreate() and onDestroy() of Activity and Fragment.
Make sure that a target object is unSubscribe or you may get memory leaked.
```kotlin
class DemoMainFragmentA : Fragment() {
    ...
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.subscribe(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.unSubscribe(this)
    }
    
    ...
}
```


Define your custom message class which extends MessageCarrier.
```kotlin
class Message1(val text: String) : MessageCarrier()
```

Annotate a pubic method with @MessageReceive. This method will be invoked when a message is sent from somewhere.
```kotlin
class DemoMainFragmentA : Fragment() {
     ...
     
    @MessageReceive
    fun onReceiveMessage1(message1: Message1) {
        //textReceiver could be a TextView
        textReceiver?.text = message1.text
    }
    ...
}
```

You can also specify the execution thread and delay through parameters in @MessageReceive. For more details, please refer to 
```kotlin
com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
```
and
```kotlin
com.allenxuan.xuanyihuang.messagebox.others.MessageScheduler
```

```kotlin
class DemoMainFragmentA : Fragment() {
     ...
     
     //This method will be invoked on main thread after 3000 milliseconds since Message1 is sent from somewhere.
    @MessageReceive(executeThread = MessageScheduler.mainThread, executeDelay = 3000)
    fun onReceiveMessage1(message1: Message1) {
        textReceiver?.text = message1.text
    }
    ...
}
```

Finally, send a message from anywhere you like.
```kotlin
class DemoMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        
        findViewById<View>(R.id.sendMessage1)?.setOnClickListener {
            MessageBox.sendMessage(Message1("Message1 received"))
        }
        
        ...
    }
}
```



# License
```
Copyright 2022 Xuanyi Huang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
