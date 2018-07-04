# XinFramework

        根据工作总结和三方开源代码，符合国内设计风格，做一个简单、快速的开发框架，可以拿来就搭建新的项目。
    由于工作比较忙只能闲暇时间来做，2017年6月第一次提交，基本满足小型项目的使用，之后会开发组件化版本，供大型项目使用。
    精简版去掉了一些三方库，对该版本重构，目前可以以module形式引入Android项目，地址：https://github.com/wzx54321/XinFrameworkLib
             
    

## 基本处理

### Application
![Application 做了如下处理](https://github.com/wzx54321/XinFramework/blob/dev/image/app.png)

### 整体结构
 整体结构: MVP + OkHttp + Glide + ObjectBox
 
 
## 使用
 
  本版本不使用组件封装提交远程仓库，使用时请取下源码，参考demo包里的代码实现和自定制。
        
  基本使用起来比较简单，有丰富的工具类。已经集成了crash报告处理，根据具体项目需要修改。使用的bugly SDK 需要申请APPID等如果不使用的话可以去掉。
    
  部分包中有readme.txt包功能说明和部分使用方法，目前满足HTTP请求、下载、上传文件、图片加载、Activity基类、Fragment基类、数据库操作，res包中有COLOR  和UI风格规范（根据具体需求处理）。 
   
  集成美团瓦力的多渠道打包，注意：[引用瓦力360加固失效处理](https://github.com/Meituan-Dianping/walle/wiki/360%E5%8A%A0%E5%9B%BA%E5%A4%B1%E6%95%88%EF%BC%9F)。   
   
   
   
   
#### TODO

##### ————————————————————————————— 基础框架 ————————————————————————————  
- [x] 权限处理
- [x] 通用颜色，风格
- [x] Activity和fragment基类
- [x] 通用工具类，不断更新
- [X] 多渠道打包配置. 使用美团的瓦力
- [X] 图片加载
- [X] MVP
- [X] RxJava
- [X] 异常处理
- [X] 文件系统
- [X] 网络请求OkGo
- [X] 图片加载进度处理
- [X] 数据库：ObjectBox
- [X] 数据库结合网络请求缓存处理
- [X] 下载，上传模块,断点续传,结合RxJAVA
##### ————————————————————————————— UI及组件————————————————————————————— 
- [x] 通用titleBar
- [X] webView  sonic-android
- [X] webView离线加载

后续的业务模块不再添加，作为单独的组件引用和扩展，之后会扩展到[组件化框架](https://github.com/wzx54321/XinFramework-component)，敬请期待！
- [ ] ui控件
- [ ] 引导页面
- [ ] 欢迎页面
- [ ] 三方登陆
- [ ] 支付
- [ ] 定位模块
- [ ] 二维码处理
- [ ] 图片选择器
- [ ] 查看大图
- [ ] 换肤
- [ ] ...

#### 感谢
感谢开源




#### About Me

炤鑫

个人主页：http://www.shindong.xin

网易博客：http://blog.163.com/ittfxin@126

邮    箱： Get_sugar@hotmail.com  
邮    箱： ittfxin@126.com    

```

  Copyright  2017 [炤鑫]

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions 
  and limitations under the License.

```


[1]: https://github.com/YoKeyword/Fragmentation
