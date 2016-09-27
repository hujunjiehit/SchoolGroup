package com.huchao.schoolgroup.message.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuntongxun.schoolgroup.R;

/**
 * Created by bjhujunjie on 2016/9/21.
 */

public class FriendFragment extends Fragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View chatView = inflater.inflate(R.layout.fragment_friend_layout, container, false);
    return chatView;
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }
}
