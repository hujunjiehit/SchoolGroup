package com.huchao.schoolgroup.message.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.huchao.schoolgroup.message.view.MessageUITabView;
import com.yuntongxun.schoolgroup.common.utils.LogUtil;
import com.yuntongxun.schoolgroup.ui.BaseFragment;
import com.yuntongxun.schoolgroup.ui.ConversationListFragment;
import com.yuntongxun.schoolgroup.ui.LauncherActivity;
import com.yuntongxun.schoolgroup.ui.TabFragment;
import com.yuntongxun.schoolgroup.ui.contact.MobileContactActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bjhujunjie on 2016/9/21.
 */

public class MessageViewPagerAdapter extends FragmentPagerAdapter {

  List<Fragment> fragmentList = new ArrayList<Fragment>();

  public MessageViewPagerAdapter(FragmentManager fm ,List<Fragment> fragmentList) {
    super(fm);
    this.fragmentList = fragmentList;
  }

  @Override
  public Fragment getItem(int position) {
    return fragmentList.get(position);
  }

  @Override
  public int getCount() {
    return fragmentList.size();
  }
}
