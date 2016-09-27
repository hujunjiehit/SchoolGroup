package com.huchao.schoolgroup.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huchao.schoolgroup.activity.LoginActivity;
import com.huchao.schoolgroup.activity.MainActivity;
import com.yuntongxun.schoolgroup.R;

import cn.bmob.v3.BmobUser;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

  private View layout;
  private View mViewNotLogined;
  private View mViewLogined;
  private TextView mTvUid;
  private ImageView mImgUserIcon;
  private String uid;
  private String userName;
  private String icon;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      initLogin();
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_mine, container, false);
    initView();
    setOnListener();
    initLogin();
    return layout;
  }

  private void initView() {
    mViewNotLogined = layout.findViewById(R.id.layout_not_logined);
    mViewLogined = layout.findViewById(R.id.layout_logined);
    mTvUid = (TextView) layout.findViewById(R.id.tv_uid);
    mImgUserIcon = (ImageView) layout.findViewById(R.id.user_icon);
  }

  private void setOnListener() {
    layout.findViewById(R.id.tv_more).setOnClickListener(this);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // 将layout从父组件中移除
    ViewGroup parent = (ViewGroup) layout.getParent();
    parent.removeView(layout);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_more: // 注销登录
        BmobUser.logOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
        break;
      default:
        break;
    }
  }

  /**
   * 初始化登录信息
   */
  private void initLogin() {
    MainActivity activity = (MainActivity) getActivity();
    boolean isLogined = activity.getLogined();
    if (isLogined) {
      // 读取登录类型
      SharedPreferences sp = activity.getSharedPreferences("login_type",
          Context.MODE_PRIVATE);
      int type = sp.getInt("login_type", 0);
      switch (type) {
        case 1: // 通过Bmob登录
          break;
        case 2: // 通过微博登录
          //icon = activity.getIcon();
          //UILUtils.displayImage(getActivity(), icon, mImgUserIcon);
          break;
        default:
          break;
      }
      userName = activity.getUserName();
      mViewNotLogined.setVisibility(View.GONE);
      mViewLogined.setVisibility(View.VISIBLE);
      mTvUid.setText(userName);
    } else {
      mViewNotLogined.setVisibility(View.VISIBLE);
      mViewLogined.setVisibility(View.GONE);
    }
  }
}
