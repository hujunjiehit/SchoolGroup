package com.huchao.schoolgroup.signup;

import cn.bmob.v3.BmobUser;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class UserInfo extends BmobUser{

  @Override
  public String toString() {
    return "objectId:" + this.getObjectId() + "  userName:" + this.getUsername() + "  CreatedAt:" + this.getCreatedAt() + "  UpdatedAt:" + this.getUpdatedAt();
  }
}
