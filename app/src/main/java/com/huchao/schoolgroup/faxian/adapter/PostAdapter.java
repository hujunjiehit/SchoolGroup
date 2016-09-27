package com.huchao.schoolgroup.faxian.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huchao.schoolgroup.model.PostModel;
import com.huchao.schoolgroup.utils.BitmapTools;
import com.squareup.picasso.Picasso;
import com.yuntongxun.schoolgroup.R;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class PostAdapter extends BaseAdapter{

  private List<PostModel> mPosts;
  private final LayoutInflater mInflater;
  private final Context mContext;

  private static final int LAYOUT_TYPE = 2;//2种布局
  private static final int LAYOUT_TYPE_NO_PICTURE = 0;
  private static final int LAYOUT_TYPE_WITH_PICTURE = 1;

  public PostAdapter(Context context) {
    super();
    mContext = context;
    mInflater = LayoutInflater.from(context);
  }

  public List<PostModel> getPosts() {
    return mPosts;
  }

  public void setPosts(List<PostModel> mPosts) {
    this.mPosts = mPosts;
  }

  @Override
  public int getCount() {
    if(mPosts == null){
      return 0;
    }
    return mPosts.size();
  }

  @Override
  public int getItemViewType(int position) {
    int type = IGNORE_ITEM_VIEW_TYPE;
    PostModel model = mPosts.get(position);
    if(model != null && model.getPostPicture() != null){
      type = LAYOUT_TYPE_WITH_PICTURE;
    }else{
      type = LAYOUT_TYPE_NO_PICTURE;
    }
    return type;
  }

  @Override
  public int getViewTypeCount() {
    return LAYOUT_TYPE;
  }


  @Override
  public Object getItem(int position) {
    return mPosts.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHodel viewHodel;
    switch (getItemViewType(position)) {
      case LAYOUT_TYPE_NO_PICTURE:
        if (convertView == null || convertView.getTag() == null) {
          convertView = mInflater.inflate(R.layout.item_post_content, parent, false);
          viewHodel = new ViewHodel();
          viewHodel.userIcon = (ImageView) convertView.findViewById(R.id.iv_owner_icon);
          viewHodel.userName = (TextView) convertView.findViewById(R.id.tv_owner_name);
          viewHodel.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
          viewHodel.postContent = (TextView) convertView.findViewById(R.id.tv_post_content);
          convertView.setTag(viewHodel);
        }else{
          viewHodel = (ViewHodel) convertView.getTag();
        }
        fillData(viewHodel, position);
        break;
      case LAYOUT_TYPE_WITH_PICTURE:
        if (convertView == null || convertView.getTag() == null) {
          convertView = mInflater.inflate(R.layout.item_post_content_with_picture, parent, false);
          viewHodel = new ViewHodel();
          viewHodel.userIcon = (ImageView) convertView.findViewById(R.id.iv_owner_icon);
          viewHodel.userName = (TextView) convertView.findViewById(R.id.tv_owner_name);
          viewHodel.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
          viewHodel.postContent = (TextView) convertView.findViewById(R.id.tv_post_content);
          viewHodel.postPicture = (ImageView) convertView.findViewById(R.id.iv_post_picture);
          convertView.setTag(viewHodel);
        }else{
          viewHodel = (ViewHodel) convertView.getTag();
        }
        fillData(viewHodel, position);
        break;
      default:
        Log.e("postAdapter", "fatal error, unKnow view type");
        convertView = new View(mContext);
        break;
    }
    return convertView;
  }

  private void fillData(ViewHodel viewHodel, int position) {
    PostModel model = mPosts.get(position);
    viewHodel.userIcon.setImageResource(R.drawable.icon_discover_school);
    viewHodel.userName.setText(model.getOwnerName());
    viewHodel.updateTime.setText(model.getUpdatedAt());
    viewHodel.postContent.setText(model.getPostContent());
    if(model.getPostPicture() != null && viewHodel.postPicture != null){
      BmobFile bmobFile = model.getPostPicture();
      //downloadFile(bmobFile,viewHodel.postPicture);
      //use Picasso to load picture
      Picasso.with(mContext).load(bmobFile.getFileUrl()).resize(BitmapTools.dp2px(mContext,250),BitmapTools.dp2px(mContext,250)).centerCrop().into(viewHodel.postPicture);
    }
  }

  private void downloadFile(BmobFile file, final ImageView imageView){
    file.download(new DownloadFileListener() {
      @Override
      public void done(String s, BmobException e) {
        if(e == null){
          Log.e("test","downlod sucess, s = " + s);
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 2;
          Bitmap bm = BitmapFactory.decodeFile(s, options);
          imageView.setImageBitmap(bm);
        }else{
          Log.e("test","downlod failed, e = " + e.toString());
        }
      }

      @Override
      public void onProgress(Integer integer, long l) {

      }
    });
  }
  public class ViewHodel {
   ImageView userIcon;
    TextView userName;
    TextView updateTime;
    TextView postContent;
    ImageView postPicture;
  }
}
