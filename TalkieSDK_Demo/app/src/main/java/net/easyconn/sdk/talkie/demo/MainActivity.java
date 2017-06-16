package net.easyconn.sdk.talkie.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.easyconn.talkie.sdk.TalkieClient;
import net.easyconn.talkie.sdk.TalkieManager;
import net.easyconn.talkie.sdk.bean.GlobalSetting;
import net.easyconn.talkie.sdk.bean.RoomInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar vToolbar;

    private CheckBox vGlobalMute;

    private ListView vListView;

    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
        initListener();

        mAdapter = new Adapter();
        vListView.setAdapter(mAdapter);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1000);
    }

    private void initGlobalSetting() {
        TalkieManager.getGlobalSetting(new TalkieClient.ResultCallback<GlobalSetting>() {
            @Override
            public void onResult(GlobalSetting globalSetting) {
                toast("获取全局设置成功");

                vGlobalMute.setOnCheckedChangeListener(null);
                vGlobalMute.setChecked(globalSetting.isGlobalMute());
                vGlobalMute.setEnabled(true);
                vGlobalMute.setOnCheckedChangeListener(mGlobalMuteCheckedChangeListener);
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                toastError("获取全局设置失败", errorCode, errorMsg);
            }
        });
    }

    private void initView() {
        vGlobalMute = (CheckBox) findViewById(R.id.cb_global_mute);
        vListView = (ListView) findViewById(R.id.list_view);
        vToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(vToolbar);
    }

    private void initListener() {
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object adapter = parent.getAdapter();
                if (adapter != null && adapter instanceof Adapter) {
                    final Object item = ((Adapter) adapter).getItem(position);
                    if (item != null && item instanceof RoomInfo) {
                        //加入自己的一个群
                        TalkieManager.online(((RoomInfo) item).getId(), new TalkieClient.ResultCallback<RoomInfo>() {
                            @Override
                            public void onResult(RoomInfo room) {
                                toast("上线成功");

                                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                                intent.putExtra("ROOM", room);
                                startActivityForResult(intent, 1000);
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                                toastError("上线失败", errorCode, errorMsg);
                            }
                        });
                    }
                }
            }
        });
        vToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_global_setting:
                        initGlobalSetting();
                        break;
                    case R.id.menu_start_polling:
                        initTalkie();
                        break;
                    case R.id.menu_stop_polling:
                        TalkieManager.stopRoomListPolling();
                        break;
                    case R.id.menu_create:
                        showCreateDialog();
                        break;
                    case R.id.menu_join:
                        showJoinDialog();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        vGlobalMute.setOnCheckedChangeListener(mGlobalMuteCheckedChangeListener);
    }

    private boolean isRequesting;

    private CompoundButton.OnCheckedChangeListener mGlobalMuteCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (isRequesting) {
                return;
            }
            isRequesting = true;
            TalkieManager.setGlobalMute(isChecked, new TalkieClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    toast("设置静音操作成功");

                    vGlobalMute.setOnCheckedChangeListener(null);
                    vGlobalMute.setChecked(isChecked);
                    vGlobalMute.setEnabled(true);
                    vGlobalMute.setOnCheckedChangeListener(mGlobalMuteCheckedChangeListener);
                    isRequesting = false;
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    toastError("设置静音操作失败", errorCode, errorMsg);

                    vGlobalMute.setOnCheckedChangeListener(null);
                    vGlobalMute.setChecked(!isChecked);
                    vGlobalMute.setEnabled(true);
                    vGlobalMute.setOnCheckedChangeListener(mGlobalMuteCheckedChangeListener);
                    isRequesting = false;
                }
            });
        }
    };

    private void showCreateDialog() {
        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_create_content_view, null);
        final EditText editText = (EditText) content.findViewById(R.id.edit_text);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入群名称")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String name = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            ToastUtil.show(MainActivity.this, "名称不能为空");
                            return;
                        }
                        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        TalkieManager.create(name, new TalkieClient.ResultCallback<RoomInfo>() {
                            @Override
                            public void onResult(RoomInfo room) {
                                toast("创建成功");

                                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                                intent.putExtra("ROOM", room);
                                startActivityForResult(intent, 1000);

                                if (mAdapter != null) {
                                    mAdapter.addRoom(room);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                                toastError("创建失败", errorCode, errorMsg);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                })
                .setView(content).create();
        dialog.show();
        editText.requestFocus();
        editText.post(new Runnable() {
            @Override
            public void run() {
                manager.showSoftInput(editText, 0);
            }
        });
    }

    private void showJoinDialog() {
        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_join_content_view, null);
        final EditText editText = (EditText) content.findViewById(R.id.edit_text);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入群口令")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String id = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(id)) {
                            ToastUtil.show(MainActivity.this, "id不能为空");
                            return;
                        }
                        if (!id.matches("\\d{1,8}")) {
                            ToastUtil.show(MainActivity.this, "id必须为8位内数字");
                            return;
                        }
                        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        TalkieManager.online(id, new TalkieClient.ResultCallback<RoomInfo>() {
                            @Override
                            public void onResult(RoomInfo room) {
                                toast("上线成功");

                                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                                intent.putExtra("ROOM", room);
                                startActivityForResult(intent, 1000);
                            }

                            @Override
                            public void onError(int errorCode, String errorMsg) {
                                toastError("上线失败", errorCode, errorMsg);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                })
                .setView(content).create();
        dialog.show();
        editText.requestFocus();
        editText.post(new Runnable() {
            @Override
            public void run() {
                manager.showSoftInput(editText, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1002) {
                if (data != null) {
                    String type = data.getStringExtra("TYPE");
                    if (type.equals("LOGOUT")) {
                        mAdapter.setRooms(null);
                        mAdapter.notifyDataSetChanged();

                        vGlobalMute.setOnCheckedChangeListener(null);
                        vGlobalMute.setChecked(false);
                        vGlobalMute.setEnabled(false);
                        vGlobalMute.setOnCheckedChangeListener(mGlobalMuteCheckedChangeListener);

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 1000);
                    } else if (type.equals("LEAVE")) {
                        RoomInfo room = data.getParcelableExtra("ROOM");
                        if (room != null) {
                            mAdapter.removeRoom(room);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }
            } else if (resultCode == 1003) {
                finish();
                System.exit(0);
            }
        }
    }

    private void initTalkie() {
        //请求群列表
        TalkieManager.startRoomListPolling(10000, new TalkieClient.ResultCallback<List<RoomInfo>>() {
            @Override
            public void onResult(List<RoomInfo> rooms) {
                toast("群列表获取成功");

                //显示列表
                mAdapter.setRooms(rooms);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                toastError("群列表获取失败", errorCode, errorMsg);
            }
        });
    }

    private class Adapter extends BaseAdapter {

        private List<RoomInfo> mRooms;

        void setRooms(List<RoomInfo> rooms) {
            this.mRooms = rooms;
        }

        void addRoom(RoomInfo info) {
            if (mRooms == null) {
                mRooms = new ArrayList<>();
            }
            if (!mRooms.contains(info)) {
                mRooms.add(info);
            }
        }

        void removeRoom(RoomInfo room) {
            if (mRooms != null) {
                mRooms.remove(room);
            }
        }

        @Override
        public int getCount() {
            if (mRooms != null) {
                return mRooms.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mRooms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_rooms, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.tv_name);
                TextView order = (TextView) convertView.findViewById(R.id.tv_order);
                TextView size = (TextView) convertView.findViewById(R.id.tv_size);
                viewHolder = new ViewHolder();
                viewHolder.vName = name;
                viewHolder.vOrder = order;
                viewHolder.vSize = size;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            RoomInfo room = mRooms.get(position);
            viewHolder.vName.setText(room.getName());
            viewHolder.vOrder.setText(String.format("口令:%s", room.getId()));
            viewHolder.vSize.setText(String.format("%s/%s", room.getOnlineSize(), room.getTotalSize()));

            return convertView;
        }

        private class ViewHolder {
            TextView vName;
            TextView vOrder;
            TextView vSize;
        }
    }

    @Override
    protected void onDestroy() {
        TalkieManager.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    private void toast(String msg) {
        ToastUtil.show(MainActivity.this, msg);
    }

    private void toastError(String action, int errorCode, String msg) {
        ToastUtil.show(MainActivity.this, action + " errorCode:" + errorCode + " msg:" + msg);
    }

}
