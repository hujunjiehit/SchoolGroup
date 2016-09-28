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
import com.yuntongxun.schoolgroup.common.CCPAppManager;
import com.yuntongxun.schoolgroup.common.utils.FileAccessor;
import com.yuntongxun.schoolgroup.core.ClientUser;
import com.yuntongxun.schoolgroup.ui.SDKCoreHelper;

import cn.bmob.v3.BmobUser;

public class MainActivity extends FragmentActivity {

  private FragmentTabHost mTabHost;
  private String uid;
  private String userName;

  ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;

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
      initLogin(uid);
    }
    mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    addTab();
  }

  private void initLogin(String uid) {
    String appkey = FileAccessor.getAppKey();
    String token = FileAccessor.getAppToken();
    Log.e("hujunjie", "getAppKey: " +  appkey);
    Log.e("hujunjie", "getAppToken: " +  token);

    ClientUser clientUser = new ClientUser(uid);
    clientUser.setAppKey(appkey);
    clientUser.setAppToken(token);
    clientUser.setLoginAuthType(mLoginAuthType);
    //clientUser.setPassword(pass);
    CCPAppManager.setClientUser(clientUser);
    SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);

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
