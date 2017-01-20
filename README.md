#亿连对讲 SDK Android 接入说明


---------------
##目录
[一、配置开发环境](#配置开发环境)

[二、SDK接入步骤](#SDK接入步骤)

------------------

<h2 id="配置开发环境">一、配置开发环境</h2>

在Android工程中加入SDK所需的lib文件，以android-studio为例，如下图所示

在AndroidManifest.xml文件中加入下面内容：
```java
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
   <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.RECORD_AUDIO"/>

   <service android:name="net.easyconn.sdk.talkie.ImService"
            android:enabled="true"
            android:exported="true" />
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
 * @param userid(String) 业务系统中的用户唯一标识
 */
TalkieManager.login(appid, userid, new TalkieClient.ConnectCallback(){
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
    });
```
**Tips：业务系统需要自己唯护userid和openid的对应关系，本文档中的其它方法中会使用openid作为用户的唯一标识**

4、 进入频道
```java
TalkieManager.online(new TalkieClient.OnlineCallback(){
      /**
       * 进入房间成功
       */
      @Override
      public void onSuccess() {

      }

      /**
       * 进入房间失败
       * @param errorCode 错误码，查看错误码对应的注释
       */
      @Override
      public void onError(int errorCode, String errorMsg) {

      }
    });
```
**Tips：只有进入频道后才能请求发言、更新位置、收到其它用户的发言和位置**

5、 离开频道
```java
TalkieManager.offline();
```
**Tips：离开频道后不能请求发言、更新位置、收到其它用户的发言和位置**

6、 请求发言
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

7、 结束发言
```java
TalkieManager.stopSpeak();
```

8、 更新位置
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

9、 发言超过30秒服务器结束发言通知事件
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

10、 其它用户开始发言事件
```java
TalkieManager.setOtherStartSpeakListener(new TalkieClient.StartSpeakListener(){
      /**
       * 其它用户开始发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onStartSpeak(String openid) {

      }
    });
```

11、 其它用户结束发言事件
```java
TalkieManager.setOtherStopSpeakListener(new TalkieClient.StopSpeakListener(){
      /**
       * 其它用户结束发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onStopSpeak() {

      }
    });
```

12、 其它用户位置变更事件
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

13、 连接状态变更事件
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
