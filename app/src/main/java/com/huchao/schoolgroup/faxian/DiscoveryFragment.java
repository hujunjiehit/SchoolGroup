package com.huchao.schoolgroup.faxian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huchao.schoolgroup.activity.DetailPostActivity;
import com.huchao.schoolgroup.activity.NewPostActivity;
import com.huchao.schoolgroup.faxian.adapter.PostAdapter;
import com.huchao.schoolgroup.model.PostModel;
import com.yuntongxun.schoolgroup.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class DiscoveryFragment extends Fragment implements View.OnClickListener {

  private PullToRefreshListView mPullToRefreshView;
  private ILoadingLayout loadingLayout;
  ListView mListView;

  private TextView tvNewPost;
  private View layout;
  private PostAdapter mPostAdapter;
  private List<PostModel> mPosts = new ArrayList<PostModel>();

  //分页加载
  private static final int STATE_REFRESH = 0;   // 下拉刷新
  private static final int STATE_MORE = 1;      // 加载更多
  private int count = 10;                       // 每页的数据是10条
  private int curPage = 0;                      // 当前页的编号，从0开始

  private String newPostTime;                   //记录最新的发帖时间，用于获取新的帖子(下拉刷新)
  private String historyPostTime;               //记录最早的发帖时间，用于获取旧的帖子(上拉加载更多)

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (layout != null) {
      // 防止多次new出片段对象，造成图片错乱问题
      return layout;
    }
    layout = inflater.inflate(R.layout.fragment_discovery, container, false);
    initView();
    setOnListener();
    init();
    return layout;
  }




  @Override
  public void onResume() {
    super.onResume();
    if(mPosts.size() != 0){
      getNewPost();
    }else{
      getPostByPage(0);
    }
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // 将layout从父组件中移除
    ViewGroup parent = (ViewGroup) layout.getParent();
    parent.removeView(layout);
  }

  private void initView() {
    mPullToRefreshView = (PullToRefreshListView) layout.findViewById(R.id.ptrScrollView_home);
    tvNewPost = (TextView) layout.findViewById(R.id.tv_new_post);
  }

  private void setOnListener() {
    mPullToRefreshView.setMode(PullToRefreshBase.Mode.BOTH);
    // 下拉刷新
    mPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
      @Override
      public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        Log.e("test", "下拉刷新, mmPosts.size = " + mPosts.size());
        //getPostByPage(0);
        if(mPosts.size() != 0){
          getNewPost();
        }else{
          getPostByPage(0);
        }
      }

      @Override
      public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        Log.e("test", "上拉加载");
        getPostByPage(curPage);

      }
    });

    tvNewPost.setOnClickListener(this);
  }

  private void init() {
    mListView = mPullToRefreshView.getRefreshableView();
    mPostAdapter = new PostAdapter(getActivity());
    mPostAdapter.setPosts(mPosts);
    mListView.setAdapter(mPostAdapter);

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //position从1开始，而List下标从0开始
        Log.e("test","you click position:" + position + "   postContent = " + mPosts.get(position-1).getPostContent());
        Intent intent = new Intent(getActivity(), DetailPostActivity.class);
        intent.putExtra("postObjectId",mPosts.get(position-1).getObjectId());
        startActivity(intent);
      }
    });
  }

  private void getPostByPage(int page) {
    BmobQuery<PostModel> query = new BmobQuery<PostModel>();
    // 按时间降序查询
    query.order("-updatedAt");

    if (page == 0) { //第一次获取数据
      curPage = 0;
    }else{  //获取分页的数据
      Date date = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
        date = sdf.parse(historyPostTime);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      // 只查询小于等于最后一个item发表时间的数据
      query.addWhereLessThan("updatedAt", new BmobDate(date));
    }
    query.setLimit(count);
    query.findObjects(new FindListener<PostModel>() {
      @Override
      public void done(List<PostModel> list, BmobException e) {
        if (mPullToRefreshView.isRefreshing()) {
          mPullToRefreshView.onRefreshComplete();
        }
        if (e == null) {
          if (list.size() > 0) {
            if(curPage == 0){
              mPosts.clear();
              for (PostModel model : list) {
                mPosts.add(model);
              }
              newPostTime = list.get(0).getUpdatedAt();
              historyPostTime = list.get(list.size() - 1).getUpdatedAt();
            }else{
              for (PostModel model : list) {
                mPosts.add(model);
              }
              historyPostTime = list.get(list.size() - 1).getUpdatedAt();
            }
            mPostAdapter.notifyDataSetChanged();
            // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
            curPage++;
            Log.e("test", "成功加载第" + curPage + "页数据");
          } else {
            Log.e("test", "没有数据了");
          }
        } else {
          Log.e("test", "请求数据失败，e" + e.toString());
        }
      }
    });
  }

  private void getNewPost() {
    BmobQuery<PostModel> query = new BmobQuery<PostModel>();
    // 按时间升序查询
    query.order("updatedAt");
    Date date = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = sdf.parse(newPostTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    // 只查询大于等于第一一个item发表时间的数据
    query.addWhereGreaterThan("updatedAt", new BmobDate(date));
    query.setLimit(count);
    query.setSkip(1); //不加这一句会重复的查询到第一条数据
    query.findObjects(new FindListener<PostModel>() {
      @Override
      public void done(List<PostModel> list, BmobException e) {
        if (mPullToRefreshView.isRefreshing()) {
          mPullToRefreshView.onRefreshComplete();
        }
        if (e == null) {
          if (list.size() > 0) {
            Log.e("test", "查询成功，共：" + list.size() + "条新的数据");
            for (PostModel model : list) {
              mPosts.add(0,model);
            }
            newPostTime = list.get(list.size()-1).getUpdatedAt();
            mPostAdapter.notifyDataSetChanged();
          } else {
            Log.e("test", "没有更多新数据");
          }
        } else {
          Log.e("test", "请求数据失败，e" + e.toString());
        }
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_new_post:
        Intent intent = new Intent(getActivity(), NewPostActivity.class);
        startActivity(intent);
        break;
      default:
        break;
    }
  }
}
