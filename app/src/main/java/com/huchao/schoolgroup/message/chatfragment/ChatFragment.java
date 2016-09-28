package com.huchao.schoolgroup.message.chatfragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.huchao.schoolgroup.message.chatfragment.adapter.MessageListAdapter;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.schoolgroup.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

  private ListView mListView;
  private MessageListAdapter mAdapter;
  private List<ECGroup> mGroups;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Log.e("hujunjie","onAttach" );
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.e("hujunjie","onResume, getGroups" );
    getGroups();
  }


  private void getGroups() {
    // 获得SDK群组创建管理类
    ECGroupManager groupManager = ECDevice.getECGroupManager();
    // 调用查询个人加入的群组接口，设置结果回调
    //Target.Group是群组，Target.Discussion是讨论组
    groupManager.queryOwnGroups(ECGroupManager.Target.GROUP, new ECGroupManager.OnQueryOwnGroupsListener() {
      @Override
      public void onQueryOwnGroupsComplete(ECError ecError, List<ECGroup> list) {
        if(SdkErrorCode.REQUEST_SUCCESS == ecError.errorCode) {
          Log.e("hujunjie","query groups complete, size = " + list.size() );
          mGroups.clear();
          for (ECGroup group:list){
            mGroups.add(group);
          }
          mAdapter.notifyDataSetChanged();
          // 请求成功
          // 进行本地数据库与服务器数据同步
          // 删除不存在或者没有加入的群组
          return ;
        }
        // 查询个人加入的群组失败
        Log.e("ECSDK_Demo", "query own groups fail " + ", errorCode=" + ecError.errorCode);

      }
    });

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View chatView = inflater.inflate(R.layout.fragment_chat_layout, container, false);
    mListView = (ListView) chatView.findViewById(R.id.list_view);
    mGroups = new ArrayList<ECGroup>();
    mAdapter = new MessageListAdapter(getActivity(), mGroups);
    mListView.setAdapter(mAdapter);
    return chatView;
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }
}
