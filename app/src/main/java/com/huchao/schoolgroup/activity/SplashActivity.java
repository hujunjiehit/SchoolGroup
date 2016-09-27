package com.huchao.schoolgroup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yuntongxun.schoolgroup.R;

public class SplashActivity extends Activity {

  private ImageView mSplashItem_iv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    mSplashItem_iv = (ImageView) findViewById(R.id.splash_loading_item);
    initView();
  }

  @Override
  protected void onResume() {
    super.onResume();
    //JPushInterface.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //JPushInterface.onPause(this);
  }

  private void initView() {
    Animation translate = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
    translate.setAnimationListener(new Animation.AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        overridePendingTransition(0, 0);
        SplashActivity.this.finish();
      }
    });
    mSplashItem_iv.setAnimation(translate);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    //不允许返回
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }

}
