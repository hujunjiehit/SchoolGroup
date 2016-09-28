package com.huchao.schoolgroup.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.huchao.schoolgroup.config.MyConfig;
import com.huchao.schoolgroup.signup.SignUpActivity;
import com.huchao.schoolgroup.signup.UserInfo;
import com.yuntongxun.schoolgroup.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class LoginActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  /**通过Bmob登录*/
  private final int LOG_BY_BMOB = 1;
  /**通过微博登录*/
  private final int LOG_BY_WEIBO = 2;

  private ToggleButton mTgBtnShowPsw;
  private EditText mEditPsw;
  private EditText mEditUid;
  private ImageView mBtnClearUid;
  private ImageView mBtnClearPsw;
  private Button mBtnLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BmobUser bmobUser = BmobUser.getCurrentUser();
    Log.e("LoginActivity","bmobUser = " + bmobUser);

    if(bmobUser != null){
      // 允许用户使用应用
      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
      intent.putExtra("uid", bmobUser.getObjectId());
      startActivity(intent);
      overridePendingTransition(0, 0);
      LoginActivity.this.finish();
    }

    setContentView(R.layout.activity_login_schoolgroup);
    initUI();
    setOnListener();
    initUid();
  }

  private void initUI() {
    mBtnLogin = (Button) findViewById(R.id.btn_login);
    mEditUid = (EditText) findViewById(R.id.edit_uid);
    mEditPsw = (EditText) findViewById(R.id.edit_psw);
    mBtnClearUid = (ImageView) findViewById(R.id.img_login_clear_uid);
    mBtnClearPsw = (ImageView) findViewById(R.id.img_login_clear_psw);
    mTgBtnShowPsw = (ToggleButton) findViewById(R.id.tgbtn_show_psw);
  }


  private void setOnListener() {
    mEditUid.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditUid.getText().toString().length() > 0) {
          mBtnClearUid.setVisibility(View.VISIBLE);
          if (mEditPsw.getText().toString().length() >0) {
            mBtnLogin.setEnabled(true);
          } else {
            mBtnLogin.setEnabled(false);
          }
        } else {
          mBtnLogin.setEnabled(false);
          mBtnClearUid.setVisibility(View.INVISIBLE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    mEditPsw.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditPsw.getText().toString().length() > 0) {
          mBtnClearPsw.setVisibility(View.VISIBLE);
          if (mEditUid.getText().toString().length() > 0) {
            mBtnLogin.setEnabled(true);
          } else {
            mBtnLogin.setEnabled(false);
          }
        } else {
          mBtnLogin.setEnabled(false);
          mBtnClearPsw.setVisibility(View.INVISIBLE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    mBtnLogin.setOnClickListener(this);
    mBtnClearUid.setOnClickListener(this);
    mBtnClearPsw.setOnClickListener(this);
    mTgBtnShowPsw.setOnCheckedChangeListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    findViewById(R.id.btn_login_wb).setOnClickListener(this);
    findViewById(R.id.tv_quick_sign_up).setOnClickListener(this);
  }

  /**
   * 初始化记住的用户名
   */
  private void initUid() {
    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
    String uid = sp.getString("uid", "");
    mEditUid.setText(uid);
  }

  /**
   * 清空控件文本
   */
  private void clearText(EditText edit) {
    edit.setText("");
  }

  /**
   * 登录按钮
   */
  private void login() {
    String userName = mEditUid.getText().toString();
    String pwd = mEditPsw.getText().toString();

    final UserInfo user = new UserInfo();
    user.setUsername(userName);
    user.setPassword(pwd);
    user.login(new SaveListener<UserInfo>() {
      @Override
      public void done(UserInfo userInfo, BmobException e) {
        if (e == null) {
          //将用户名保存到Preference
          SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
          SharedPreferences.Editor edit = sp.edit();
          edit.putString("uid", mEditUid.getText().toString());
          edit.commit();

          String id2 = user.getObjectId();
          Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
          Log.e("test",userInfo.toString());
          //通过Bmob登录
          setSP(LOG_BY_BMOB);
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          intent.putExtra("uid", id2);
          startActivity(intent);
          overridePendingTransition(0, 0);
          LoginActivity.this.finish();
        } else {
          Toast.makeText(LoginActivity.this, "用户名或密码错误," + e.toString(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  /**
   * 将登录途径保存到SharedPreferences，1为Bmob，2为微博
   */
  private void setSP(int type) {
    //保存当前位置到SharedPreferences
    SharedPreferences sp = this
        .getSharedPreferences("login_type", Context.MODE_PRIVATE);
    SharedPreferences.Editor edit = sp.edit();
    edit.putInt("login_type", type);
    edit.commit();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_login:	//登录
        login();
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.btn_login_wb:	//微博登录
        //loginWB();
        break;
      case R.id.img_login_clear_uid:	//清除用户名
        clearText(mEditUid);
        break;
      case R.id.img_login_clear_psw:	//清除密码
        clearText(mEditPsw);
        break;
      case R.id.tv_quick_sign_up:	//快速注册
        startActivity(new Intent(this, SignUpActivity.class));
        break;

      default:
        break;
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      //显示密码
      mEditPsw.setTransformationMethod(
          HideReturnsTransformationMethod.getInstance());
    } else {
      //隐藏密码
      mEditPsw.setTransformationMethod(
          PasswordTransformationMethod.getInstance());
    }
  }
}
