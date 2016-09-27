package com.huchao.schoolgroup.app;

import android.app.Application;

import com.huchao.schoolgroup.config.MyConfig;

import cn.bmob.v3.Bmob;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class MyApplication extends Application{

  @Override
  public void onCreate() {
    super.onCreate();
    Bmob.initialize(this, MyConfig.applicationId);
  }
}
