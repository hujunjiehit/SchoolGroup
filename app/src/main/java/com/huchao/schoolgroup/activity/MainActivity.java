package com.huchao.schoolgroup.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huchao.schoolgroup.faxian.DiscoveryFragment;
import com.huchao.schoolgroup.mine.MineFragment;
import com.huchao.schoolgroup.message.MessageFragment;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.schoolgroup.R;

import cn.bmob.v3.BmobUser;

public class MainActivity extends FragmentActivity {

  private FragmentTabHost mTabHost;
  private String uid;
  private String userName;

  private Class[] mFragments = new Class[] { DiscoveryFragment.class,
      MessageFragment.class, MineFragment.class, MineFragment.class};

  private int[] mTabSelectors = new int[] {
      R.drawable.main_bottom_tab_faxian,
      R.drawable.main_bottom_tab_message,
      R.drawable.main_bottom_tab_friends,
      R.drawable.main_bottom_tab_mine
  };

  private String[] mTabSpecs = new String[] { "发现", "消息", "好友",
     "我的" };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if(getIntent() != null){
      Log.e("test","MainActivity, uid = " + getIntent().getStringExtra("uid"));
      uid = getIntent().getStringExtra("uid");

      if(!ECDevice.isInitialized()) {
        ECDevice.initial(this, new ECDevice.InitListener() {
          @Override
          public void onInitialized() {
            // SDK已经初始化成功
            Log.e("test","Yuntx SDK 初始化成功");

            ECInitParams params = ECInitParams.createParams();
            params.setUserid(uid);
            params.setAppKey("8aaf0708570871f8015731e957fa13db");
            params.setToken("c185ae10e1a9d6a193f7f9953260c78c");
            // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
            params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
            // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
            // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
            // 3 LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO）
            params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);



            ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
              @Override
              public void onConnect() {

              }

              @Override
              public void onDisconnect(ECError ecError) {

              }

              @Override
              public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
                  if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                    //账号异地登陆
                    Log.e("test","账号异地登陆");
                  } else {
                    //连接状态失败
                    Log.e("test","连接状态失败, error.errorCode = " + error.errorCode);
                  }
                  return ;
                }else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                  // 登陆成功
                  Log.e("test","账号登录成功");

                }
              }
            });

            if(params.validate()) {
              // 判断注册参数是否正确
              ECDevice.login(params);
            }
          }

          @Override
          public void onError(Exception exception) {
            // SDK 初始化失败,可能有如下原因造成
            // 1、可能SDK已经处于初始化状态
            // 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
            //    或者未配置服务属性android:exported="false";
            // 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
            //    Android Build.VERSION.SDK_INT 以及以上版本）
            Log.e("test","Yuntx SDK 初始化失败，exception:" + exception.toString());
          }
        });
      }
    }
    mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    addTab();
  }

  private void addTab() {
    for (int i = 0; i < 4; i++) {
      View tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
      ImageView imageView = (ImageView) tabIndicator.findViewById(R.id.image);
      imageView.setImageResource(mTabSelectors[i]);
      TextView title = (TextView) tabIndicator.findViewById(R.id.title);
      title.setText(mTabSpecs[i]);
      mTabHost.addTab(
          mTabHost.newTabSpec(mTabSpecs[i])
              .setIndicator(tabIndicator), mFragments[i], null);
    }
  }

  /**
   * 由片段调用，获取是否已登录
   * @return
   */
  public boolean getLogined() {
    return uid != null ? true:false;
  }

  /**
   * 由片段调用，获取用户名
   *
   * @return
   */
  public String getUid() {
    return uid;
  }

  /**
   * 由片段调用，获取用户名
   *
   * @return
   */
  public String getUserName() {
    if(BmobUser.getCurrentUser() != null){
      return BmobUser.getCurrentUser().getUsername();
    }else{
      return "";
    }
  }
}
