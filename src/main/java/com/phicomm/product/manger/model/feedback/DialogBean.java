package com.phicomm.product.manger.model.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 会话记录
 *
 * @author wei.yang on 2017/10/30
 */
public class DialogBean {

    private String userId;

    private Long dialogId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String username;

    private String imageUrl;

    private String dialogType;

    private String dialogText;

    private String dialogPicture;

    private List dialogPictures;

    private String evaluation;

    private String evaluationStar;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDialogType() {
        return dialogType;
    }

    public void setDialogType(String dialogType) {
        this.dialogType = dialogType;
    }

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public String getDialogPicture() {
        return dialogPicture;
    }

    public void setDialogPicture(String dialogPicture) {
        this.dialogPicture = dialogPicture;
    }

    public List getDialogPictures() {
        return dialogPictures;
    }

    public void setDialogPictures(List dialogPictures) {
        this.dialogPictures = dialogPictures;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getEvaluationStar() {
        return evaluationStar;
    }

    public void setEvaluationStar(String evaluationStar) {
        this.evaluationStar = evaluationStar;
    }

    @Override
    public String toString() {
        return "DialogBean{" +
                "userId='" + userId + '\'' +
                ", dialogId=" + dialogId +
                ", createTime=" + createTime +
                ", username='" + username + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", dialogType='" + dialogType + '\'' +
                ", dialogText='" + dialogText + '\'' +
                ", dialogPicture='" + dialogPicture + '\'' +
                ", dialogPictures=" + dialogPictures +
                ", evaluation='" + evaluation + '\'' +
                ", evaluationStar='" + evaluationStar + '\'' +
                '}';
    }
}
