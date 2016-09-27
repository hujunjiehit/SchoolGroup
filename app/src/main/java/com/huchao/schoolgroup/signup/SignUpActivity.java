package com.huchao.schoolgroup.signup;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.huchao.schoolgroup.config.MyConfig;
import com.yuntongxun.schoolgroup.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class SignUpActivity extends Activity implements View.OnClickListener{

  private EditText mEditUser;
  private EditText mEditPsw;
  private Button mBtnSignUp;

  private boolean isBtnChecked = true;
  private EditText mEditPswVal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    mBtnSignUp = (Button) findViewById(R.id.btn_sign_up);
    mEditUser = (EditText) findViewById(R.id.edit_uid);
    mEditPsw = (EditText) findViewById(R.id.edit_psw);
    mEditPswVal = (EditText) findViewById(R.id.edit_psw_val);
    CheckBox btnCheck = (CheckBox) findViewById(R.id.btn_check);
    TextWatcher watcher = new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        setSignable();
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    };
    mEditUser.addTextChangedListener(watcher);
    mEditPsw.addTextChangedListener(watcher);
    mEditPswVal.addTextChangedListener(watcher);
    btnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView,
                                   boolean isChecked) {
        isBtnChecked = isChecked;
        setSignable();
      }

    });
    mBtnSignUp.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
  }

  /**
   * 是否符合注册条件
   */
  private void setSignable() {
    if (isBtnChecked) {

      if (mEditUser.getText().toString().length() > 5
          && mEditPsw.getText().toString().length() > 5
          && mEditPswVal.getText().toString().length() > 5) {
        mBtnSignUp.setEnabled(true);
      } else {
        mBtnSignUp.setEnabled(false);
      }
    } else {
      mBtnSignUp.setEnabled(false);
    }
  }

  private void signup() {
    final String userName = mEditUser.getText().toString();
    String pwd = mEditPsw.getText().toString();
    String pwdVal = mEditPswVal.getText().toString();

    if (!pwd.equals(pwdVal)) {
      mEditPswVal.setText("");
      mEditPswVal.setError("两次输入的密码不一致");
      return;
    }
    UserInfo user = new UserInfo();
    user.setUsername(userName);
    user.setPassword(pwd);
    user.signUp(new SaveListener<UserInfo>() {
      @Override
      public void done(UserInfo userInfo, BmobException e) {
        if(e == null){
          Toast.makeText(SignUpActivity.this, "注册成功，请登录", Toast.LENGTH_LONG).show();
          Log.e("test",userInfo.toString());
          finish();
        }else{
          mEditUser.setError("用户名已存在");
          Toast.makeText(SignUpActivity.this, "注册失败," + e.toString(), Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_back:
        finish();
        break;
      case R.id.btn_sign_up:
        signup();
        break;
      default:
        break;
    }
  }
}
