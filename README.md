#亿连对讲 SDK Android 接入说明


---------------
##目录
[一、配置开发环境](#配置开发环境)

[二、SDK接入步骤](#SDK接入步骤)

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
TalkieManager.init(activity);
```
**Tips：建议放置第一个Activity的onCreate方法里**

2、 摧毁服务
```java
TalkieManager.destroy();
```

3、 用户授权
```java
/**
 * 用户授权
 * @param appid(String)  应用唯一标识
 * @param secret(String) 安全码
 * @param userid(String) 业务系统中的用户唯一标识
 */
TalkieManager.login(appid, secret, userid, new TalkieClient.ConnectCallback(){
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
**Tips：业务系统需要自己唯护userid和openid的对应关系，本文档中的其它方法中会使用openid作为用户的唯一标识**

4、创建频道
```java
/**
 * 创建频道
 * @param roomId(String)  频道ID
 * @param name(String)    频道名称
 */
TalkieManager.create(roomId, name, new TalkieClient.OperationCallback(){
      /**
       * 创建频道成功
       */
      @Override
      public void onSuccess() {

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

5、退出频道
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

6、频道列表刷新事件
```java
TalkieManager.setRoomListListener(new TalkieClient.RoomListListener<List<RoomInfo>>(){
      /**
       * 返回频道列表，每5秒回调一次 
       */
      public void onResult(List<RoomInfo> list){
      
      }
})
```


7、 进入频道
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

8、 离开频道
```java
TalkieManager.offline();
```
**Tips：离开频道后不能请求发言、更新位置、收到其它用户的发言和位置**

9、获得频道信息和设置
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

10、获得频道成员列表
```java
//TODU
这里需要分页
TalkieManager.getUserList(roomId, new TalkieClient.ResultCallback<List <UserInfo>>(){
      public void onResult(List <UserInfo> list){
      
      }
})
```

11、获得成员信息和设置
```java
TalkieManager.getUserInfo(userId, new TalkieClient.ResultCallback<UserInfo>(){
      public void onResult(UserInfo userInfo){
      
      }
})
```

12、 请求发言
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

13、 结束发言
```java
TalkieManager.stopSpeak();
```

14、 更新位置
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

15、 发言超过30秒服务器结束发言通知事件
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

16、 其它用户开始发言事件
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

~~15、 其它用户结束发言事件~~
```java
TalkieManager.setOtherStopSpeakListener(new TalkieClient.StopSpeakListener(){

```

17、 其它用户位置变更事件
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

18、 连接状态变更事件
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

19、 设置对讲播放音量
```java
TalkieManager.setTalkieVolume(volume);
```
20、修改房间名称
```java
TalkieManager.setRoomName(roomId, name, new TalkieClient.OperationCallback(){});
```

21、静音开关
```java
TalkieManager.setGlobalMute(isMute, new TalkieClient.OperationCallback(){});
```

22、位置共享开关
```java
TalkieManager.setLocationSharing(roomId, isSharing, new TalkieClient.OperationCallback(){});
```

23、增加/修改管理员
```java
TalkieManager.setRoomAdmin(roomId, userId, new TalkieClient.OperationCallback(){});
```

24、踢人
```java
TalkieManager.kickUser(roomId, userId, hour, new TalkieClient.OperationCallback(){});
```

25、禁言
```java
TalkieManager.silenced(roomId, userId, hour, new TalkieClient.OperationCallback(){});
```

26、获得发言状态
```java
TalkieManager.getSpeakState();
```
