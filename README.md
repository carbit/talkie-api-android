#亿连对讲 SDK Android 接入说明


---------------
##目录
[一、配置开发环境](#配置开发环境)

[二、SDK接入步骤](#SDK接入步骤)

[三、Server授权](#Server授权)

[四、类说明](#类说明)

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

   <service android:name="net.easyconn.talkie.sdk.ImService"
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
/**
 * @param projectid(String)  应用唯一标识
 */
TalkieManager.init(context, projectid);
```

2、 摧毁服务
```java
TalkieManager.destroy();
```

3、 设置请求超时 默认5000 最小5000最大30000
```java
TalkieManager.setHttpTimeOut(long connectTime, long readTime);
```

4、 用户登录

```java
/**
 * @param appSecret(String) 安全码
 * @param userid(String) 业务系统中的用户唯一标识
 */
TalkieManager.login(appSecret, userid, new TalkieClient.ConnectCallback(){
	@Override
	public void onSuccess(String openId, String token) {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```
**Tips：**

**1.appSecret是应用接口的安全码，泄漏后将可能导致应用数据泄漏、应用的用户数据泄漏等高风险后果；存储在客户端，极有可能被恶意窃取。建议使用[Server授权](#Server授权)方法；**

**2.业务系统需要自己唯护userid和openid的对应关系，本文档中的其它方法中会使用openid作为用户的唯一标识**

5、App登出时调用
```java
TalkieManager.logout()
```
**Tips：登出后不能使用后面的接口**

6、获取全局设置 （包含全局静音设置）
```java
TalkieManager.getGlobalSetting(new TalkieClient.ResultCallback<GlobalSetting>() {
	@Override
	public void onResult(GlobalSetting globalSetting) {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

7、创建频道
```java
/**
 * @param name(String)    频道名称
 */
TalkieManager.create(name, new TalkieClient.ResultCallback<RoomInfo>(){
	@Override
	public void onResult(RoomInfo room) {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```

8、退出频道
```java
/**
 * @param roomId(String)  频道ID
 */
TalkieManager.leave(roomId, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```

9、开始频道列表轮询
```java
/**
 * @param interval(int)  轮询间隔，单位秒，但不能小于服务器配置(5秒)
 */
TalkieManager.startRoomListPolling(interval, new TalkieClient.ResultCallback<List<RoomInfo>>(){
	@Override
	public void onResult(<List<RoomInfo>> rooms) {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

10、停止频道列表轮询
```java
TalkieManager.stopRoomListPolling();
```

11、 进入频道
```java
/**
 * @param roomId(String)  频道ID
 */
TalkieManager.online(roomId, new TalkieClient.ResultCallback<RoomInfo>(){
	@Override
	public void onResult(RoomInfo room) {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```
**Tips：只有进入频道后才能请求发言、更新位置、收到其它用户的发言和位置**

12、 离开频道
```java
TalkieManager.offline();
```
**Tips：离开频道后不能请求发言、更新位置、收到其它用户的发言和位置**

13、主动获取频道信息
```java
/**
 * @param roomId(String)  频道ID
 */
TalkieManager.getRoomInfo(roomId, new TalkieClient.ResultCallback<RoomInfo>(){
	public void onResult(RoomInfo room){

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```

14、获得频道成员列表
```java
/**
 * @param roomId(String)  频道ID
 * @param page(int) 页数 1开始
 * @param size(int) 每页的数量
 */
TalkieManager.getUserList(roomId, page, size, new TalkieClient.PageResultCallback<List<UserInfo>>(){
	/**
	* @param users 成员数据
	* @param total 总数量
	*/
	public void onResult(List<UserInfo> users, int total){

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```

15、获得成员信息和设置
```java
TalkieManager.getUserInfo(roomId, openId, new TalkieClient.ResultCallback<UserInfo>(){
	public void onResult(UserInfo userInfo){

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
})
```

16、获得发言状态
```java
TalkieManager.getSpeakState();
```

17、 请求发言
```java
TalkieManager.reqSpeak(new TalkieClient.ReqSpeakCallback(){
	/**
	* 准备开始请求发言 状态回调（请求中）
	*/
	@Override
	public void onReady() {

	}
	/**
	* 请求发言成功 （发言中）
	*/
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
    });
```

18、 结束发言
```java
TalkieManager.stopSpeak();
```

19、 更新位置
```java
/**
 * @param lat(float) 纬度
 * @param lon(float) 经度
 * @param speed(int) 速度
 * @param direction(int) 方向
 */
TalkieManager.location(lat, lon, speed, direction);
```

20、 设置对讲播放音量（0.01-0.99）
```java
TalkieManager.setTalkieVolume(volume);
```

21、修改房间名称
```java
TalkieManager.setRoomName(roomId, name, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

22、设置全局静音设置
```java
TalkieManager.setGlobalMute(isGlobalMute, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

23、位置共享开关
```java
TalkieManager.setLocationSharing(roomId, isSharing, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

24、设置房间管理角色
```java
/**
* @param role 角色 参照RoomRole枚举
*/
TalkieManager.setRoomRole(roomId, openId, role, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

25、踢人
```java
TalkieManager.kickUser(roomId, openId, hour, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

26、禁言
```java
TalkieManager.silenced(roomId, openId, hour, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

27、取消禁言（暂未实现）
```java
TalkieManager.unSilenced(roomId, openId, new TalkieClient.OperationCallback(){
	@Override
	public void onSuccess() {

	}
	@Override
	public void onError(int errorCode, String errorMsg) {

	}
});
```

28、设置自己的事件监听 （只对自己）
```java
TalkieManager.setSelfEventListener(new TalkieClient.SelfEventListener(){
      /**
       * 停止说话时回调
       * @param type 参照StopSpeakType枚举
       */
      @Override
      public void onStopSpeak(StopSpeakType type) {

      }
      /**
       * 自己的角色被改变时回调
       */
      public void onRoleChange(RoomRole role){

      }
      /**
       * 自己被踢出当前房间时回调
       */
      public void onKickedOut(){

      }
      /**
       * 自己被禁言时回调
       */
      public void onSilenced(int hour){

      }
      /**
       * 自己恢复禁言时回调
       */
      public void onUnSilenced(){

      }
      /**
       * 对讲服务连接成功后回调 表示可以正常收听语音消息
       */
      public void onTalkieServerConnected(int onlineSize, int totalSize){

      }
      /**
       * 对讲服务断开后回调 表示自己已离线（无法收听语音消息） 
       * 如果是offline或logout或登录冲突则不会自动连接 需手动执行online操作才能再次连接
       */
      public void onTalkieServerDisconnected(){

      }
});
```

29、 设置其他成员事件监听（其他成员的操作事件）
```java
TalkieManager.MemberEventListener(new TalkieClient.MemberEventListener(){
      /**
       * 其它用户开始发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onMemberStartSpeak(String openid) {

      }
      /**
       * 其它用户结束发言事件
       * @param openid 授权用户唯一标识
       */
      @Override
      public void onMemberStopSpeak(String openid) {

      }

      /**
       * 其它用户位置变更事件
       * @param openid 授权用户唯一标识
       * @param lat 纬度
       * @param lon 经度
       * @param speed 速度
       * @param direction 方向
       */
      @Override
      public void onMemberLocationChange(String openid, float lat, float lon, int speed, int direction) {

      }
      /**
       * 其他用户角色改变时
       */
      @Override
      public void onMemberRoleChange(String openid, RoomRole role) {

      }
      /**
       * 其他用户上线时
       */
      @Override
      public void onMemberOnline(String openid, int onlineSize, int totalSize) {

      }
      /**
       * 其他用户下线时
       */
      @Override
      public void onMemberOffline(String openid, int onlineSize, int totalSize) {

      }
      /**
       * 其他用户退出房间时
       */
      @Override
      public void onMemberLeave(String openid, int onlineSize, int totalSize) {

      }
      /**
       * 其他用户更改房间名称时
       */
      @Override
      public void onMemberChangeRoomName(String openid, String roomName) {

      }
      /**
       * 其他用户更改自己人位置共享开关时
       */
      @Override
      public void onMemberLocationSharingChange(String openid, String isLocationSharing) {

      }
});
```

<h2 id="Server授权">三、Server授权</h2>

Server授权整体流程：
```
1. 第三方Server发起授权登录请求, 对讲服务器向第三方Server指定URL发送回调请求, 并且带上授权临时票据code参数;
2. 通过code参数加上projectid、appSecret、userId等，通过API向对讲服务器换取openId和token;
3. 将openId和token设置给SDK, 实现其他操作。
```

### 第一步：请求CODE

第三方Server访问如下链接：

https://openapi.carbit.com.cn/talkie/oauth2/connect?projectid=PROJECTID&callback_url=CALLBACK_URL&response_type=code&scope=SCOPE&state=STATE

* 参数说明

|参数    | 是否必须 | 说明   |
|--------|:------------:|-------|
|projectid  | 是　　　  | 应用唯一标识 |
|callback_url | 是　　　 | 回调地址, 需要进行UrlEncode |
|response_type| 是　　　 | 填code  |
|scope| 是　　　 | 填snsapi_login  |
|state| 否　　　 | 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验  |

* 返回说明

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

https://openapi.carbit.com.cn/talkie/oauth2/login?projectid=PROJECTID&secret=SECRET&code=CODE&userid=USERIDgrant_type=authorization_code

* 参数说明

|参数    | 是否必须 | 说明   |
|--------|:------------:|-------|
|projectid  | 是　　　  | 应用唯一标识 |
|secret | 是　　　 | 安全码 |
|code| 是　　　 | 填写第一步获取的code参数  |
|userid| 是　　　 | 第三方服务器的用户唯一标识 |
|grant_type| 是　　　 | 填snsapi_login  |

* 返回说明

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

### 第三步：授权

```java
/**
 * 用户授权
 * @param openId(String)
 * @param token(String)
 */
TalkieManager.oauth(openId, token, new TalkieClient.OperationCallback(){
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

<h2 id="类说明">四、类说明</h2>


<h3>实体类</h3>

1、房间信息
```java
public class RoomInfo {
	private String id; 			//唯一标识

	private String name; 			//名称

	private int onlineSize; 		//在线人数

	private int totalSize; 			//总人数

	private boolean locationSharing;	//位置共享开关状态
	
	private UserInfo self;			//自己的数据
}
```
2、用户信息
```java
public class UserInfo {
	private String id;			//唯一标识

	private RoomRole role;			//角色

	private boolean isOnline;		//是否在线

	private Permission permission;		//权限

	private boolean isSelf;			//是否是自己
}
```

<h3>枚举</h3>

1、房间角色
```java
public enum RoomRole {
	OWNER,					//群主
	
	ADMINISTRATOR,				//管理员
	
	GENERAL_MEMBER				//普通成员
}
```
2、发言状态
```java
public enum MicrophoneState {
	ERROR,                      		//错误(检查是否配置了ImService)

	LEISURE,                     		//空闲

	REQUEST_SPEAKING,            		//正在请求发言中

	SELF_SPEAKING,               		//自己正在发言

	MEMBER_SPEAKING             		//其他人员正在发言
}
```
3、 停止说话类型
```java
public enum StopSpeakType {
	BY_HAND,                                 //手动丢麦

	BY_HIGHER_PERMISSION,                    //更高的请求发言的权限打断

	BY_SERVER_NO_RECEIVER_AUDIO,             //服务端一段时间未收到语音包 强制打断

	BY_SPEAK_TIME_OUT,                       //发言时长达到最大值 服务端强制打断

	BY_AUTO,                                 //抢麦3秒无发言自动丢麦(群主除外)

	BY_PHONE,                                //发言中 打电话或来电 强制打断

	BY_SOCKET_SERVER_DISCONNECT              //网络不稳定时 Socket服务断开时 强制打断
}
```

<h3>其他</h3>

1、用户权限说明
```java
public class Permission {
    public boolean allowSpeak();			//允许发言

    public boolean allowEditRoomName();			//允许编辑群名

    public boolean allowKickRoomUser();			//允许踢出群内成员

    public boolean allowSilencedRoomUser();		//允许禁言群内用户

    public boolean allowUnSilencedRoomUser();		//允许恢复群内被禁言用户

    public boolean allowChangeRole();			//允许修改角色
}
```

