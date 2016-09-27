package com.huchao.schoolgroup.message;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huchao.schoolgroup.message.adapter.MessageViewPagerAdapter;
import com.huchao.schoolgroup.message.fragment.ChatFragment;
import com.huchao.schoolgroup.message.fragment.ContactsFragment;
import com.huchao.schoolgroup.message.fragment.FriendFragment;
import com.huchao.schoolgroup.message.view.MessageUITabView;
import com.yuntongxun.schoolgroup.R;
import com.yuntongxun.schoolgroup.common.base.CCPCustomViewPager;
import com.yuntongxun.schoolgroup.common.utils.LogUtil;
import com.yuntongxun.schoolgroup.ui.BaseFragment;
import com.yuntongxun.schoolgroup.ui.ConversationListFragment;
import com.yuntongxun.schoolgroup.ui.DiscussionListFragment;
import com.yuntongxun.schoolgroup.ui.GroupListFragment;
import com.yuntongxun.schoolgroup.ui.LauncherActivity;
import com.yuntongxun.schoolgroup.ui.TabFragment;
import com.yuntongxun.schoolgroup.ui.contact.MobileContactActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bjhujunjie on 2016/9/21.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{

  private View layout;



  /**
   * Tab的下方的引导线
   */
  private ImageView mTabLineIv;
  /**
   * Tab显示内容TextView
   */
  private TextView mTabChatTv, mTabContactsTv, mTabFriendTv;

  private ViewPager mViewPager;
  private List<Fragment> mFragmentList = new ArrayList<Fragment>();
  private MessageViewPagerAdapter mViewPagerAdapter;

  private ChatFragment mChatFg;
  private FriendFragment mFriendFg;
  private ContactsFragment mContactsFg;

  /**
   * ViewPager的当前选中页
   */
  private int currentIndex;
  /**
   * 屏幕的宽度
   */
  private int screenWidth;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_message, container, false);
    initView();
    setOnListener();
    initData();
    initTabLineWidth();
    return layout;
  }



  private void initView() {
    mTabContactsTv = (TextView) layout.findViewById(R.id.id_contacts_tv);
    mTabChatTv = (TextView) layout.findViewById(R.id.id_chat_tv);
    mTabFriendTv = (TextView) layout.findViewById(R.id.id_friend_tv);

    mTabLineIv = (ImageView) layout.findViewById(R.id.id_tab_line_iv);
    mViewPager = (ViewPager) layout.findViewById(R.id.pager);
  }

  private void setOnListener() {
    mTabChatTv.setOnClickListener(this);
    mTabFriendTv.setOnClickListener(this);
    mTabContactsTv.setOnClickListener(this);
  }

  private void initData() {
    mFriendFg = new FriendFragment();
    mContactsFg = new ContactsFragment();
    mChatFg = new ChatFragment();
    mFragmentList.add(mChatFg);
    mFragmentList.add(mFriendFg);
    mFragmentList.add(mContactsFg);

    mViewPagerAdapter = new MessageViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
    mViewPager.setAdapter(mViewPagerAdapter);
    mViewPager.setCurrentItem(0);

    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

      /**
       * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
       * offsetPixels:当前页面偏移的像素位置
       */
      @Override
      public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv.getLayoutParams();
        Log.e("offset:", offset + "");
        /**
         * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
         * 设置mTabLineIv的左边距 滑动场景：
         * 记3个页面,
         * 从左到右分别为0,1,2
         * 0->1; 1->2; 2->1; 1->0
         */
        if (currentIndex == 0 && position == 0)// 0->1
        {
          lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));

        } else if (currentIndex == 1 && position == 0) // 1->0
        {
          lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));

        } else if (currentIndex == 1 && position == 1) // 1->2
        {
          lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
        } else if (currentIndex == 2 && position == 1) // 2->1
        {
          lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
        }
        mTabLineIv.setLayoutParams(lp);
      }

      @Override
      public void onPageSelected(int position) {
        resetTextView();
        switch (position) {
          case 0:
            mTabChatTv.setTextColor(Color.BLUE);
            break;
          case 1:
            mTabFriendTv.setTextColor(Color.BLUE);
            break;
          case 2:
            mTabContactsTv.setTextColor(Color.BLUE);
            break;
        }
        currentIndex = position;
      }

      /**
       * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
       */
      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

  }

  private void initTabLineWidth() {
    DisplayMetrics dpMetrics = new DisplayMetrics();
    getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
    screenWidth = dpMetrics.widthPixels;
    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv.getLayoutParams();
    lp.width = screenWidth / 3;
    mTabLineIv.setLayoutParams(lp);
  }

  /**
   * 重置颜色
   */
  private void resetTextView() {
    mTabChatTv.setTextColor(Color.BLACK);
    mTabFriendTv.setTextColor(Color.BLACK);
    mTabContactsTv.setTextColor(Color.BLACK);
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
    resetTextView();
    switch (v.getId()){
      case R.id.id_chat_tv:
        mTabChatTv.setTextColor(Color.BLUE);
        currentIndex = 0;
        break;
      case R.id.id_friend_tv:
        mTabFriendTv.setTextColor(Color.BLUE);
        currentIndex = 1;
        break;
      case R.id.id_contacts_tv:
        mTabContactsTv.setTextColor(Color.BLUE);
        currentIndex = 2;
        break;
      default:
        break;
    }
    mViewPager.setCurrentItem(currentIndex);
  }
}
