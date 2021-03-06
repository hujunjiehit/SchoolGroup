package com.huchao.schoolgroup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huchao.schoolgroup.model.PostModel;
import com.huchao.schoolgroup.utils.BitmapTools;
import com.squareup.picasso.Picasso;
import com.yuntongxun.schoolgroup.R;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by bjhujunjie on 2016/9/20.
 */
public class DetailPostActivity extends Activity implements View.OnClickListener{

  private BmobUser bmobUser;
  private ImageView ivPostPicture;
  private TextView tvPostContent;
  private ImageView ivBack;
  private TextView tvJoinGroup;
  private String postObjectId;
  private PostModel mPostModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bmobUser = BmobUser.getCurrentUser();
    setContentView(R.layout.activity_detail_post_layout);
    initView();
    setOnListener();
    init();
  }

  private void initView() {
    ivPostPicture = (ImageView) findViewById(R.id.iv_post_picture);
    tvPostContent = (TextView)  findViewById(R.id.tv_post_content);
    ivBack = (ImageView) findViewById(R.id.img_back);
    tvJoinGroup = (TextView) findViewById(R.id.tv_join_group);
  }

  private void setOnListener() {
    ivPostPicture.setOnClickListener(this);
    ivBack.setOnClickListener(this);
    tvJoinGroup.setOnClickListener(this);
  }

  private void init() {
    if(getIntent() != null){
      postObjectId = getIntent().getStringExtra("postObjectId");
    }
    getData();
  }

  private void getData() {
    if(postObjectId != null){
      BmobQuery<PostModel> bmobQuery = new BmobQuery<PostModel>();
      bmobQuery.getObject(postObjectId, new QueryListener<PostModel>() {
        @Override
        public void done(PostModel postModel, BmobException e) {
         if(e == null){
           mPostModel = postModel;
           if(postModel.getPostPicture() != null){
             ivPostPicture.setVisibility(View.VISIBLE);
             Picasso.with(DetailPostActivity.this).load(postModel.getPostPicture().getFileUrl())
                 .resize(BitmapTools.dp2px(DetailPostActivity.this,250),BitmapTools.dp2px(DetailPostActivity.this,250))
                 .centerCrop().into(ivPostPicture);
           }
           tvPostContent.setText(postModel.getPostContent());
         }else{
           Log.e("DetailPostActivity","get post detail failed");
         }
        }
      });
    }else{
      Log.e("DetailPostActivity","there is no post objectId");
    }
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.tv_join_group:
        Log.e("DetailPostActivity","用户点击加入群聊");
        break;
      case R.id.img_back:
        finish();
        break;
      case R.id.iv_post_picture:
        Intent intent = new Intent(this, PicturePreviewActivity.class);
        intent.putExtra("url",mPostModel.getPostPicture().getFileUrl());
        startActivity(intent);
        break;
      default:
        break;
    }

  }
}
