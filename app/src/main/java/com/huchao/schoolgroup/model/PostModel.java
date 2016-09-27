package com.huchao.schoolgroup.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class PostModel extends BmobObject{

  private String ownerId;
  private String ownerName;
  private String postContent;
  private BmobFile postPicture;
  private String groupId;       //该帖子对应的群组id

  public PostModel(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getPostContent() {
    return postContent;
  }

  public void setPostContent(String postContent) {
    this.postContent = postContent;
  }

  public BmobFile getPostPicture() {
    return postPicture;
  }

  public void setPostPicture(BmobFile postPicture) {
    this.postPicture = postPicture;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
}
