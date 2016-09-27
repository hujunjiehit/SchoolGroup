package com.huchao.schoolgroup.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huchao.schoolgroup.model.PostModel;
import com.huchao.schoolgroup.utils.BitmapTools;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.PersonInfo;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.schoolgroup.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class NewPostActivity extends Activity implements View.OnClickListener {

  private TextView tvSendPost;
  private ImageView ivAddPicture;
  private ImageView ivShowPicture;
  private EditText etPostContent;
  private ImageView ivBack;
  private BmobUser bmobUser;
  private String picturePath;
  private Uri photoUri;
  private File file;
  private Bitmap photo;

  private ProgressDialog progressDialog;

  private static final int REQUEST_CODE_PICK_PIC = 10;
  private static final int REQUEST_CODE_TAKE_PHOTO = 11;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bmobUser = BmobUser.getCurrentUser();
    setContentView(R.layout.activity_new_post);
    initView();
    setOnListener();
    init();
  }


  private void initView() {
    tvSendPost = (TextView) findViewById(R.id.tv_send_post);
    etPostContent = (EditText) findViewById(R.id.post_content);
    ivBack = (ImageView) findViewById(R.id.img_back);
    ivAddPicture = (ImageView) findViewById(R.id.iv_add_picture);
    ivShowPicture = (ImageView) findViewById(R.id.iv_show_picture);
  }

  private void setOnListener() {
    tvSendPost.setOnClickListener(this);
    ivBack.setOnClickListener(this);
    ivAddPicture.setOnClickListener(this);
  }

  private void init() {
    //显示输入法
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        showKeyboard();
      }
    }, 200);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_send_post:
        String post_content = etPostContent.getText().toString().trim();
        PostModel postModel = new PostModel(bmobUser.getObjectId());
        postModel.setOwnerName(bmobUser.getUsername());
        postModel.setPostContent(post_content);
        sendNewPost(postModel);
        break;
      case R.id.img_back:
        finish();
        break;
      case R.id.iv_add_picture:
        showChooseDialog();
        break;
      default:
        break;
    }
  }

  /**
   * 弹出拍照上传和相册选择对话框
   */
  private void showChooseDialog() {
    final CharSequence[] items = {"相册", "拍照"};
    AlertDialog dlg = new AlertDialog.Builder(this)
        .setTitle("选择图片")
        .setItems(items, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int item) {
            // 这里item是根据选择的方式，
            if (item == 0) {
              //相册选择
              Intent intent = new Intent(Intent.ACTION_PICK);
              intent.setType("image/*");
              startActivityForResult(intent, REQUEST_CODE_PICK_PIC);
            } else {
              //拍照上传
              destoryBimap();
              Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              Log.e("NewPostActivity", "sd 卡路径：" +  Environment.getExternalStorageDirectory());
              if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String saveDir = Environment.getExternalStorageDirectory() + "/temp";
                File dir = new File(saveDir);
                if (!dir.exists()) {
                  dir.mkdir();
                }
                file = new File(saveDir, "/takephoto.png");
                Log.e("test","file path = " + file.getPath());
                file.delete();
                if (!file.exists()) {
                  try {
                    file.createNewFile();
                  } catch (IOException e) {
                    e.printStackTrace();
                    return;
                  }
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(file));
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);

              } else {
//                                Toast.makeText(MainActivity.this, 未找到存储卡，无法存储照片！,
//                                        Toast.LENGTH_SHORT).show();
              }
            }
          }
        }).create();
    dlg.show();
  }

  /**
   * 销毁图片文件
   */
  private void destoryBimap() {
    if (photo != null && !photo.isRecycled()) {
      photo.recycle();
      photo = null;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == REQUEST_CODE_PICK_PIC) {
        if (data == null) {
          Log.e("NewPostActivity","pick pic failed");
          return;
        } else {
          Uri uri = data.getData();
          Bitmap bitmap = getOriginalBitmap(uri);

          BitmapTools.compressBmpToFile(bitmap);

//          String[] proj = {MediaStore.Images.Media.DATA};
//          Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
//          cursor.moveToFirst();
//          int columnIndex = cursor.getColumnIndex(proj[0]);
//          picturePath = cursor.getString(columnIndex);

          //原来的获取图片路径的方式不用，改用压缩后的图片来上传
          picturePath = Environment.getExternalStorageDirectory() + "/temp/temp.png";
          ivShowPicture.setImageBitmap(bitmap);
          ivShowPicture.setVisibility(View.VISIBLE);
        }
      }else if(requestCode == REQUEST_CODE_TAKE_PHOTO){
        if (file != null && file.exists()) {
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 2;
          photo = BitmapFactory.decodeFile(file.getPath(), options);
          BitmapTools.compressBmpToFile(photo);

          picturePath = Environment.getExternalStorageDirectory() + "/temp/temp.png";
          ivShowPicture.setImageBitmap(photo);
          ivShowPicture.setVisibility(View.VISIBLE);

        }
      }
    }
  }

  private Bitmap getOriginalBitmap(Uri photoUri) {
    if (photoUri == null) {
      return null;
    }
    Bitmap bitmap = null;
    try {
      ContentResolver conReslv = getContentResolver();
      // 得到选择图片的Bitmap对象
      bitmap = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
    } catch (Exception e) {
      Log.e("NewPostActivity", "Media.getBitmap failed", e);
    }
    return bitmap;
  }



  private void sendNewPost(final PostModel postModel) {
    if (postModel.getPostContent().length() == 0) {
      Toast.makeText(NewPostActivity.this, "您不能发布空内容", Toast.LENGTH_SHORT).show();
      return;
    }
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("正在发布中，请稍候...");
    progressDialog.setCancelable(false);
    progressDialog.show();
    if (picturePath != null) {
      //包含图片文件
      final BmobFile bmobFile = new BmobFile(new File(picturePath));
      postModel.setPostPicture(bmobFile);
      bmobFile.uploadblock(new UploadFileListener() {
        @Override
        public void done(BmobException e) {
          if (e == null) {
            insertObject(postModel);
          } else {
            if(progressDialog != null && progressDialog.isShowing()){
              progressDialog.dismiss();
            }
            Toast.makeText(NewPostActivity.this, "文件上传失败,请检查您的网络环境", Toast.LENGTH_SHORT).show();
            Log.e("NewPostActivity", "file upload,fail   error:" + e.toString());
          }
        }

        @Override
        public void onProgress(Integer value) {
          super.onProgress(value);
        }
      });
    } else {
      insertObject(postModel);
    }

  }

  private void insertObject(final BmobObject obj) {
    obj.save(new SaveListener<String>() {
      @Override
      public void done(String s, BmobException e) {
        if(progressDialog != null && progressDialog.isShowing()){
          progressDialog.dismiss();
        }
        if (e == null) {
          Toast.makeText(NewPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
          createPostGroup(((PostModel)obj).getPostContent());
          finish();
        } else {
          Toast.makeText(NewPostActivity.this, "发布失败," + e.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void createPostGroup(String GroupName) {
    // 构建群组参数
    ECGroup group = new ECGroup();

    //设置群组名称
    group.setName(GroupName);

    //设置群组公告
    group.setDeclare("欢迎体验云通讯群组功能");

    //设置群组类型，如：ECGroup.Scope.TEMP临时群组（100人）
    group.setScope(ECGroup.Scope.TEMP);

    //设置群组验证权限，如：需要身份验证ECGroup.Permission.NEED_AUTH
    group.setPermission(ECGroup.Permission.NEED_AUTH);

    //设置群组创建者（可以不设置，服务器默认接口调用者为创建者）
    group.setOwner(bmobUser.getObjectId());

    //false代表是群组、true代表是讨论组
    group.setIsDiscuss(false);//是否是讨论组

    //获得SDK群组创建管理类
    ECGroupManager groupManager = ECDevice.getECGroupManager();
    // 调用创建群组接口，设置创建结果回调
    groupManager.createGroup(group, new ECGroupManager.OnCreateGroupListener() {
      @Override
      public void onCreateGroupComplete(ECError ecError, ECGroup ecGroup) {
        if(ecError.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
          // 群组/讨论组创建成功
          // 缓存创建的群组/讨论组到数据库，同时通知UI进行更新
          PersonInfo m;
          Log.e("test" , "create group sucess, GroupId = " +ecGroup.getGroupId()  + "    groupName = " + ecGroup.getName() + "  owner:" + ecGroup.getOwner());
          return ;
        }
        // 群组/讨论组创建失败
        Log.e("test" , "create group fail , errorCode=" + ecError.errorCode);
      }
      });
  }



  public void showKeyboard() {
    if (etPostContent != null) {
      etPostContent.setFocusable(true);
      etPostContent.setFocusableInTouchMode(true);
      etPostContent.requestFocus();
      InputMethodManager inputManager = (InputMethodManager) etPostContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.showSoftInput(etPostContent, 0);
    }
  }
}
