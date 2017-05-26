#亿连对讲 SDK Android 接入说明


---------------
##目录
[一、配置开发环境](#配置开发环境)

[二、SDK接入步骤](#SDK接入步骤)

[三、Server授权](#Server授权)

------------------

<h2 id="配置开发环境">一、配置开发环境</h2>

在Android工程中加入TalkieSdk_1.0.jar和所需的jniLibs文件。

在AndroidManifest.xml文件中加入下面内容：
```java
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
   <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.RECORD_AUDIO"/>

   <service android:name="net.easyconn.talkie.im.ImService"
            android:enabled="true"
            android:exported="true" />
```

还必需引入两个Jar库
```
    compile('io.socket:socket.io-client:0.7.0') {
        exclude group: 'org.json', module: 'json'
    }
    compile 'com.squareup.okhttp:okhttp:+'
```

<h2 id="SDK接入步骤">二、SDK接入步骤</h2>

1、 初始化
```java
TalkieManager.init(context);
```

2、 摧毁服务
```java
TalkieManager.destroy();
```

3、 用户授权

```java
/**
 * 用户授权
 * @param appId(String)  应用唯一标识
 * @param appSecret(String) 安全码
 * @param userid(String) 业务系统中的用户唯一标识
 */
TalkieManager.login(appId, appSecret, userid, new TalkieClient.ConnectCallback(){
      /**
       * 用户授权成功
       * @param openid 对讲服务器为授权用户分配的唯一标识
       */
      @Override
      public void onSuccess(String openid) {

      }

      /**
       * 用户授权失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
    }
})
```
**Tips：**

**1.appSecret是应用接口的安全码，泄漏后将可能导致应用数据泄漏、应用的用户数据泄漏等高风险后果；存储在客户端，极有可能被恶意窃取。建议使用[Server授权](#Server授权)方法；**

**2.业务系统需要自己唯护userid和openid的对应关系，本文档中的其它方法中会使用openid作为用户的唯一标识**

4、注销授权
```java
TalkieManager.logOut()
```
**Tips：注销授权后不能使用后面的接口**

5、创建频道
```java
/**
 * 创建频道
 * @param name(String)    频道名称
 */
TalkieManager.create(name, new TalkieClient.CreateRoomCallback(){
      /**
       * 创建频道成功
       */
      @Override
      public void onSuccess(String roomId) {

      }

      /**
       * 创建频道失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
})
```

6、退出频道
```java
/**
 * 创建频道
 * @param roomId(String)  频道ID
 */
TalkieManager.leave(roomId, new TalkieClient.OperationCallback(){
      /**
       * 退出频道成功
       */
      @Override
      public void onSuccess() {

      }

      /**
       * 退出频道失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
})
```

7、设置频道列表轮询事件
```java
/**
 * 设置频道列表轮询事件
 * @param interval(int)  轮询间隔，单位秒，但不能小于服务器配置(5秒)
 */
TalkieManager.setRoomListPollingListener(interval, new TalkieClient.RoomListPollingListener<List<RoomInfo>>(){
      /**
       * 返回频道列表
       * @param list 频道列表
       */
      public void onResult(List<RoomInfo> list){
      
      }
})
```

8、停止频道列表轮询
```java
TalkieManager.stopRoomListPolling()
```

9、 进入频道
```java
/**
 * 进入频道
 * @param roomId(String)  频道ID
 */
TalkieManager.online(roomId, new TalkieClient.OperationCallback(){
      /**
       * 进入频道成功
       */
      @Override
      public void onSuccess() {

      }

      /**
       * 进入频道失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
});
```
**Tips：只有进入频道后才能请求发言、更新位置、收到其它用户的发言和位置**

10、 离开频道
```java
TalkieManager.offline();
```
**Tips：离开频道后不能请求发言、更新位置、收到其它用户的发言和位置**

11、获得频道信息和设置
```java
/**
 * 获得频道信息和设置
 * @param roomId(String)  频道ID
 */
TalkieManager.getRoomInfo(roomId, new TalkieClient.ResultCallback<RoomInfo>(){
      public void onResult(RoomInfo roomInfo){
      
      }
})
```

12、获得频道成员列表
```java
//TODU
这里需要分页
TalkieManager.getUserList(roomId, new TalkieClient.ResultCallback<List <UserInfo>>(){
      public void onResult(List <UserInfo> list){
      
      }
})
```

13、获得成员信息和设置
```java
TalkieManager.getUserInfo(openId, new TalkieClient.ResultCallback<UserInfo>(){
      public void onResult(UserInfo userInfo){
      
      }
})
```

14、 请求发言
```java
TalkieManager.reqSpeak(new TalkieClient.ReqSpeakCallback(){
      /**
       * 请求发言成功
       */
      @Override
      public void onSuccess() {

      }

      /**
       * 请求发言失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
    });
```

15、 结束发言
```java
TalkieManager.stopSpeak();
```

16、 更新位置
```java
/**
 * 更新位置
 * @param lat(float) 纬度
 * @param lon(float) 经度
 * @param speed(int) 速度
 * @param direction(int) 方向
 */
TalkieManager.location(lat, lon, speed, direction);
```

17、 发言超过30秒服务器结束发言通知事件
```java
TalkieManager.setStopSpeakNtfListener(new TalkieClient.StopSpeakNtfListener(){
      /**
       * 发言超过30秒服务器结束发言通知事件
       */
      @Override
      public void onStopSpeakNtfListener() {

      }
   });
```

18、 其它用户开始发言事件
```java
TalkieManager.setOtherSpeakListener(new TalkieClient.StartSpeakListener(){
      /**
       * 其它用户开始发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onStartSpeak(String openid) {

      }
    });
    
    /**
       * 其它用户结束发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onStopSpeak() {

      }
    });
```

19、 其它用户位置变更事件
```java
TalkieManager.setOtherLocationListener(new TalkieClient.LocationListener(){
      /**
       * 其它用户位置变更事件
       * @param openid 授权用户唯一标识
       * @param lat 纬度
       * @param lon 经度
       * @param speed 速度
       * @param direction 方向
       */
      @Override
      public void onLocationListener(String openid, float lat, float lon, int speed, int direction) {

      }
    });
```

20、 连接状态变更事件
```java
TalkieManager.setConnectStateListener(new TalkieClient.ConnectStateListener(){
      /**
       * 连接状态变更事件
       * @param connectionStatus 状态变更
       */
      @Override
      public void onStateChange(ConnectionStatus connectionStatus){
          case CONNECTED://连接成功。

              break;
          case DISCONNECTED://断开连接。

              break;
      }
    });
```

21、 设置对讲播放音量
```java
TalkieManager.setTalkieVolume(volume);
```

22、修改房间名称
```java
TalkieManager.setRoomName(roomId, name, new TalkieClient.OperationCallback(){});
```

23、静音开关
```java
TalkieManager.setGlobalMute(isMute, new TalkieClient.OperationCallback(){});
```

24、位置共享开关
```java
TalkieManager.setLocationSharing(roomId, isSharing, new TalkieClient.OperationCallback(){});
```

25、增加/修改管理员
```java
TalkieManager.setRoomAdmin(roomId, openId, new TalkieClient.OperationCallback(){});
```

26、踢人
```java
TalkieManager.kickUser(roomId, openId, hour, new TalkieClient.OperationCallback(){});
```

27、禁言
```java
TalkieManager.silenced(roomId, openId, hour, new TalkieClient.OperationCallback(){});
```

28、获得发言状态
```java
TalkieManager.getSpeakState();
```

<h2 id="Server授权">三、Server授权</h2>

Server授权整体流程：
```
1. 第三方Server发起授权登录请求, 对讲服务器向第三方Server指定URL发送回调请求, 并且带上授权临时票据code参数;
2. 通过code参数加上appId、appSecret、userId等，通过API向对讲服务器换取openId和token;
3. 将openId和token设置给SDK, 实现其他操作。
```

### 第一步：请求CODE

第三方Server访问如下链接：

https://open.carbit.com.cn/talkie/oauth2/connect?appid=APPID&callback_url=CALLBACK_URL&response_type=code&scope=SCOPE&state=STATE

** 参数说明 **

|参数    | 是否必须 | 说明   |
|--------|:------------:|-------|
|appid  | 是　　　  | 应用唯一标识 |
|callback_url | 是　　　 | 回调地址, 需要进行UrlEncode |
|response_type| 是　　　 | 填code  |
|scope| 是　　　 | 填snsapi_login  |
|state| 否　　　 | 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验  |

** 返回说明 **

用户允许授权后，将会回调到callback_url地址上，并且带上code和state参数
```
  callback_url?code=CODE&state=STATE
```
若用户禁止授权，则回调不会带上code参数，仅会带上stage参数
```
  callback_url?state=STATE
```

### 第二步：通过code申请openId和token

第三方Server访问如下链接：

https://open.carbit.com.cn/talkie/oauth2/login?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

** 参数说明 **

|参数    | 是否必须 | 说明   |
|--------|:------------:|-------|
|appid  | 是　　　  | 应用唯一标识 |
|secret | 是　　　 | 安全码 |
|code| 是　　　 | 填写第一步获取的code参数  |
|grant_type| 是　　　 | 填snsapi_login  |

** 返回说明 **

正确的返回：
```
  {
    "code": 0,
    "context":{
      "openId": "3249234792347023",
      "token": "20c1ab1ff1b4fb93b79395c449090d8a"
    }
  }
```

错误返回：
```
  {
    "code": -400
  }
```

### 第三步：将openId和token设置给SDK

```java
/**
 * 用户授权
 * @param appId(String)  应用唯一标识
 * @param appSecret(String) 安全码
 * @param userid(String) 业务系统中的用户唯一标识
 */
TalkieManager.setToken(openId, token, new TalkieClient.OperationCallback(){
      /**
       * token验证成功
       */
      @Override
      public void onSuccess() {

      }

      /**
       * token验证失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      })
```
