package net.easyconn.sdk.talkie.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.easyconn.talkie.sdk.TalkieClient;
import net.easyconn.talkie.sdk.TalkieManager;
import net.easyconn.talkie.sdk.bean.RoomInfo;
import net.easyconn.talkie.sdk.bean.UserInfo;
import net.easyconn.talkie.sdk.constants.MicrophoneState;
import net.easyconn.talkie.sdk.constants.RoomRole;
import net.easyconn.talkie.sdk.constants.StopSpeakType;

import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private TextView vOpenId, vRole, vState;

    private TextView vId;

    private EditText vName;

    private Button vChangeName;

    private TextView vOnline, vTotal;

    private TextView vBroadcast;

    private TextView vNotification;

    private TextView vMemberSpeak;

    private Button vUploadLocation;

    private TextView vLocation;

    private Button vVolumeAdd, vVolumeMinus;

    private Button vRoomInfo;

    private TextView vSpeakState;

    private Button vOffline;

    private Button vLeave;

    private Button vLogout;

    private Button vUserListButton;

    private CheckBox vLocationSharing;

    private TalkieActionView vActionView;

    private ListView vUserList;

    private Adapter mAdapter;

    private float volume = 1.0F;

    private RoomInfo mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initView();
        initListener();
        initTalkie();

        mRoom = getIntent().getParcelableExtra("ROOM");
        initRoom();

        mAdapter = new Adapter();
        vUserList.setAdapter(mAdapter);
    }

    private void initView() {
        vOpenId = (TextView) findViewById(R.id.tv_open_id);
        vRole = (TextView) findViewById(R.id.tv_role);
        vState = (TextView) findViewById(R.id.tv_state);
        vId = (TextView) findViewById(R.id.tv_room_id);
        vName = (EditText) findViewById(R.id.et_name);
        vOnline = (TextView) findViewById(R.id.tv_online);
        vTotal = (TextView) findViewById(R.id.tv_total);
        vChangeName = (Button) findViewById(R.id.btn_change_name);
        vBroadcast = (TextView) findViewById(R.id.tv_broadcast);
        vNotification = (TextView) findViewById(R.id.tv_notification);
        vMemberSpeak = (TextView) findViewById(R.id.tv_member_speak);
        vUploadLocation = (Button) findViewById(R.id.btn_location);
        vLocation = (TextView) findViewById(R.id.tv_location);
        vVolumeAdd = (Button) findViewById(R.id.volume_add);
        vVolumeMinus = (Button) findViewById(R.id.volume_minus);
        vRoomInfo = (Button) findViewById(R.id.btn_room_info);
        vSpeakState = (TextView) findViewById(R.id.tv_speak_state);
        vOffline = (Button) findViewById(R.id.btn_offline);
        vLeave = (Button) findViewById(R.id.btn_leave);
        vLogout = (Button) findViewById(R.id.btn_logout);
        vLocationSharing = (CheckBox) findViewById(R.id.cb_location_sharing);
        vUserListButton = (Button) findViewById(R.id.btn_user_list);
        vActionView = (TalkieActionView) findViewById(R.id.talkie_action_view);
        vUserList = (ListView) findViewById(R.id.list_view);
    }

    private void initListener() {
        vChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = vName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    toast("名称不能为空");
                    return;
                }
                TalkieManager.setRoomName(mRoom.getId(), name, new TalkieClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        toast("修改成功");
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        toastError("修改失败", errorCode, errorMsg);
                    }
                });
            }
        });
        vUploadLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.location(30.574305D, 114.286212D, 10, 30);
                vLocation.setText("上报位置\n纬度:30.574305,经度:114.286212\n速度10,方向30");
            }
        });
        vVolumeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume = volume + 0.05F;
                TalkieManager.setTalkieVolume(volume);
            }
        });
        vVolumeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume = volume - 0.05F;
                TalkieManager.setTalkieVolume(volume);
            }
        });
        vRoomInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.getRoomInfo(mRoom.getId(), new TalkieClient.ResultCallback<RoomInfo>() {
                    @Override
                    public void onResult(RoomInfo room) {
                        toast("获取房间信息成功");

                        if (mRoom != null && room != null && TextUtils.equals(mRoom.getId(), room.getId())) {
                            mRoom = room;

                            initRoom();
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        toastError("获取房间信息失败", errorCode, errorMsg);
                    }
                });
            }
        });
        vOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.offline();
                finish();
            }
        });
        vLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.logout();
                SpUtil.clearOpenId(InfoActivity.this);
                SpUtil.clearToken(InfoActivity.this);
                Intent intent = new Intent();
                intent.putExtra("TYPE", "LOGOUT");
                setResult(1002, intent);
                finish();
            }
        });
        vLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.leave(mRoom.getId(), new TalkieClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        toast("退出房间成功");

                        Intent intent = new Intent();
                        intent.putExtra("TYPE", "LEAVE");
                        intent.putExtra("ROOM", mRoom);
                        setResult(1002, intent);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        toastError("退出房间失败", errorCode, errorMsg);
                    }
                });
            }
        });
        vUserListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkieManager.getUserList(mRoom.getId(), 1, 100, new TalkieClient.PageResultCallback<List<UserInfo>>() {
                    @Override
                    public void onResult(List<UserInfo> users, int total) {
                        toast("获取成员列表成功");

                        mAdapter.setUsers(users);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        toastError("获取成员列表失败", errorCode, errorMsg);
                    }
                });
            }
        });
        vActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MicrophoneState state = TalkieManager.getSpeakState();
                switch (state) {
                    case ERROR:
                        toast("未知错误");
                        break;
                    case LEISURE:
                        TalkieManager.reqSpeak(new TalkieClient.ReqSpeakCallback() {
                            @Override
                            public void onReady() {
                                vActionView.onRequestSpeaking();
                                vSpeakState.setText("抢麦中");
                            }

                            @Override
                            public void onSuccess() {
                                vActionView.onSpeaking();
                                vSpeakState.setText("正在录音");
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                                vActionView.onRequestFailure(errorCode);
                                vSpeakState.setText("抢麦失败 code:" + errorCode + " msg:" + errorMsg);
                            }
                        });
                        break;
                    case REQUEST_SPEAKING:
                    case SELF_SPEAKING:
                        TalkieManager.stopSpeak();
                        break;
                    case MEMBER_SPEAKING:
                        toast("群友正在讲话，请稍后抢麦");
                        break;
                    default:
                        break;
                }
            }
        });
        vUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Object item = mAdapter.getItem(position);
                    if (item != null && item instanceof UserInfo) {
                        UserInfo info = (UserInfo) item;
                        TalkieManager.getUserInfo(mRoom.getId(), info.getOpenId(), new TalkieClient.ResultCallback<UserInfo>() {
                            @Override
                            public void onResult(final UserInfo user) {
                                toast("获取成员信息成功");

                                View content = LayoutInflater.from(InfoActivity.this).inflate(R.layout.dialog_manager_user, null);
                                TextView vOpenId = (TextView) content.findViewById(R.id.tv_open_id);
                                vOpenId.setText(user.getOpenId());
                                Button vSetAdministrator = (Button) content.findViewById(R.id.btn_set_administrator);
                                Button vSetGeneralMember = (Button) content.findViewById(R.id.btn_set_general_member);
                                Button vKick = (Button) content.findViewById(R.id.btn_kick);
                                Button vSilenced = (Button) content.findViewById(R.id.btn_silenced);
                                vSilenced.setText(user.allowSpeak() ? "禁言" : "取消禁言");

                                final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(InfoActivity.this).setView(content).create();

                                vSetAdministrator.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        TalkieManager.setRoomRole(mRoom.getId(), user.getOpenId(), RoomRole.ADMINISTRATOR, new TalkieClient.OperationCallback() {
                                            @Override
                                            public void onSuccess() {
                                                toast("设置成功");
                                            }

                                            @Override
                                            public void onError(int errorCode, String errorMsg) {
                                                toastError("设置失败", errorCode, errorMsg);
                                            }
                                        });
                                    }
                                });
                                vSetGeneralMember.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        TalkieManager.setRoomRole(mRoom.getId(), user.getOpenId(), RoomRole.GENERAL_MEMBER, new TalkieClient.OperationCallback() {
                                            @Override
                                            public void onSuccess() {
                                                toast("设置成功");
                                            }

                                            @Override
                                            public void onError(int errorCode, String errorMsg) {
                                                toastError("设置失败", errorCode, errorMsg);
                                            }
                                        });
                                    }
                                });
                                vKick.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        TalkieManager.kickUser(mRoom.getId(), user.getOpenId(), 1, new TalkieClient.OperationCallback() {
                                            @Override
                                            public void onSuccess() {
                                                toast("踢人成功");
                                            }

                                            @Override
                                            public void onError(int errorCode, String errorMsg) {
                                                toastError("踢人失败", errorCode, errorMsg);
                                            }
                                        });
                                    }
                                });
                                vSilenced.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        if (user.allowSpeak()) {
                                            TalkieManager.silenced(mRoom.getId(), user.getOpenId(), 1, new TalkieClient.OperationCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    toast("禁言成功");
                                                }

                                                @Override
                                                public void onError(int errorCode, String errorMsg) {
                                                    toastError("禁言失败", errorCode, errorMsg);
                                                }
                                            });
                                        } else {
                                            TalkieManager.unSilenced(mRoom.getId(), user.getOpenId(), new TalkieClient.OperationCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    toast("取消禁言成功");
                                                }

                                                @Override
                                                public void onError(int errorCode, String errorMsg) {
                                                    toastError("取消禁言失败", errorCode, errorMsg);
                                                }
                                            });
                                        }
                                    }
                                });

                                dialog.show();
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                                toastError("获取成员信息失败", errorCode, errorMsg);
                            }
                        });
                    }
                }
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener mLocationSharingCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (isRequesting) {
                return;
            }
            isRequesting = true;
            TalkieManager.setLocationSharing(mRoom.getId(), isChecked, new TalkieClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    toast("设置位置共享操作成功");

                    vLocationSharing.setOnCheckedChangeListener(null);
                    vLocationSharing.setChecked(isChecked);
                    vLocationSharing.setEnabled(true);
                    vLocationSharing.setOnCheckedChangeListener(mLocationSharingCheckedChangeListener);
                    isRequesting = false;
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    toastError("设置位置共享操作失败", errorCode, errorMsg);

                    vLocationSharing.setOnCheckedChangeListener(null);
                    vLocationSharing.setChecked(!isChecked);
                    vLocationSharing.setEnabled(true);
                    vLocationSharing.setOnCheckedChangeListener(mLocationSharingCheckedChangeListener);
                    isRequesting = false;
                }
            });
        }
    };

    private boolean isRequesting;

    private void initTalkie() {
        TalkieManager.setSelfEventListener(new TalkieClient.SelfEventListener() {
            @Override
            public void onStopSpeak(StopSpeakType type) {
                //丢麦后当前麦属于空闲状态
                vActionView.onLeisure();

                switch (type) {
                    case BY_HAND:
                        vSpeakState.setText("手动丢麦");
                        break;
                    case BY_HIGHER_PERMISSION:
                        vSpeakState.setText("被更高的抢麦权限打断");
                        break;
                    case BY_SERVER_NO_RECEIVER_AUDIO:
                        vSpeakState.setText("服务端未收到语音数据");
                        break;
                    case BY_SPEAK_TIME_OUT:
                        vSpeakState.setText("服务端说话时长超时");
                        break;
                    case BY_AUTO:
                        vSpeakState.setText("自动丢麦");
                        break;
                    case BY_PHONE:
                        vSpeakState.setText("来电（去电）状态丢麦");
                        break;
                    case BY_SOCKET_SERVER_DISCONNECT:
                        vSpeakState.setText("对讲服务断开");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRoleChange(RoomRole role) {
                vNotification.setText(String.format("自己角色改变:%s", getRole(role)));
                vRole.setText(getRole(role));
            }

            @Override
            public void onKickedOut() {
                vNotification.setText("自己被踢出房间");
            }

            @Override
            public void onSilenced(int hour) {
                vNotification.setText(String.format("被禁言%s小时", hour));
            }

            @Override
            public void onUnSilenced() {
                vNotification.setText("解除禁言");
            }

            @Override
            public void onTalkieServerConnected(int onlineSize, int totalSize) {
                vNotification.setText("对讲服务连接成功 可正常收听语音消息");
                vOnline.setText(String.format("%s", onlineSize));
                vTotal.setText(String.format("%s", totalSize));

                vState.setText("连接");
            }

            @Override
            public void onTalkieServerDisconnected() {
                vNotification.setText("对讲服务连接断开 无法收听语音消息 离线状态");

                vState.setText("断开");
            }
        });
        TalkieManager.setMemberEventListener(new TalkieClient.MemberEventListener() {

            @Override
            public void onMemberStartSpeak(String openId) {
                vMemberSpeak.setText(String.format("%s 正在说话", openId));
            }

            @Override
            public void onMemberStopSpeak(String openId) {
                vMemberSpeak.setText(String.format("%s 停止说话", openId));
            }

            @Override
            public void onMemberStopSpeakByTimeout(String openId) {
                vMemberSpeak.setText(String.format("%s 停止说话(超时检测)", openId));
            }

            @Override
            public void onMemberLocationChange(String openId, double latitude, double longitude, int speed, int direction) {
                vBroadcast.setText(String.format("user(%s) 位置变更\n纬度:%s\n经度:%s", openId, latitude, longitude));
            }

            @Override
            public void onMemberRoleChange(String openId, RoomRole role) {
                vBroadcast.setText(String.format("user(%s) 角色变更为%s", openId, getRole(role)));
            }

            @Override
            public void onMemberOnline(String openId, int online, int total) {
                vBroadcast.setText(String.format("user(%s) 上线 ", openId));
                vOnline.setText(String.format("%s", online));
                vTotal.setText(String.format("%s", total));
            }

            @Override
            public void onMemberOffline(String openId, int online, int total) {
                vBroadcast.setText(String.format("user(%s) 离线 ", openId));
                vOnline.setText(String.format("%s", online));
                vTotal.setText(String.format("%s", total));
            }

            @Override
            public void onMemberLeave(String openId, int online, int total) {
                vBroadcast.setText(String.format("user(%s) 退出 ", openId));
                vOnline.setText(String.format("%s", online));
                vTotal.setText(String.format("%s", total));
            }

            @Override
            public void onMemberChangeRoomName(String openId, String roomName) {
                vBroadcast.setText(String.format("user(%s) 修改房间名称 name(%s)", openId, roomName));
                vName.setText(roomName);
            }

            @Override
            public void onMemberLocationSharingChange(String openId, boolean isLocationSharing) {
                if (isLocationSharing) {
                    vBroadcast.setText(String.format("user(%s) 开启位置共享", openId));
                } else {
                    vBroadcast.setText(String.format("user(%s) 关闭位置共享", openId));
                }
            }

        });
    }

    private void initRoom() {
        if (mRoom != null) {
            UserInfo self = mRoom.getSelf();
            if (self != null) {
                vOpenId.setText(self.getOpenId());
                vRole.setText(getRole(self.getRole()));
            }

            vId.setText(mRoom.getId());
            vName.setText(mRoom.getName());

            vLocationSharing.setOnCheckedChangeListener(null);
            vLocationSharing.setChecked(mRoom.isLocationSharing());
            vLocationSharing.setEnabled(true);
            vLocationSharing.setOnCheckedChangeListener(mLocationSharingCheckedChangeListener);

            vOnline.setText(String.format("%s", mRoom.getOnlineSize()));
            vTotal.setText(String.format("%s", mRoom.getTotalSize()));
        }
    }

    private class Adapter extends BaseAdapter {

        private List<UserInfo> mUsers;

        void setUsers(List<UserInfo> users) {
            this.mUsers = users;
        }

        @Override
        public int getCount() {
            if (mUsers != null) {
                return mUsers.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(InfoActivity.this).inflate(R.layout.item_users, parent, false);
                TextView vOpenId = (TextView) convertView.findViewById(R.id.tv_open_id);
                viewHolder = new ViewHolder();
                viewHolder.vOpenId = vOpenId;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserInfo user = mUsers.get(position);
            viewHolder.vOpenId.setText(user.getOpenId());

            return convertView;
        }

        private class ViewHolder {
            TextView vOpenId;
        }
    }

    private static String getRole(RoomRole role) {
        switch (role) {
            case OWNER:
                return "群主";
            case ADMINISTRATOR:
                return "管理员";
            case GENERAL_MEMBER:
                return "普通成员";
            default:
                return "";
        }
    }

    private void toast(String msg) {
        ToastUtil.show(InfoActivity.this, msg);
    }

    private void toastError(String action, int errorCode, String msg) {
        ToastUtil.show(InfoActivity.this, action + " errorCode:" + errorCode + " msg:" + msg);
    }
}
