# MessageBox
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/index.html)
[![License](https://img.shields.io/badge/Version-0.8.0-blue.svg)](https://jcenter.bintray.com/com/github/allenxuan/)
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](http://www.apache.org/licenses/LICENSE-2.0)

#### Messagebox is a message/event framework for Android/Java.

### Screenshot
![demo1](/screenshot/demo.gif)

### Dependencies:
```
//Java
dependencies {
    compileOnly 'com.github.allenxuan:messagebox-annotation:0.8.0'
    implementation 'com.github.allenxuan:messagebox-core:0.8.0'
    annotationProcessor 'com.github.allenxuan:messagebox-compiler:0.8.0'
}


//Kotlin
dependencies {
    compileOnly 'com.github.allenxuan:messagebox-annotation:0.8.0'
    implementation 'com.github.allenxuan:messagebox-core:0.8.0'
    kapt 'com.github.allenxuan:messagebox-compiler:0.8.0'
}
```

### Usages
define your custom message class which extends MessageCarrier
```kotlin
class Message1(val text: String) : MessageCarrier()
```


# License
```
Copyright 2019 Xuanyi Huang

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
