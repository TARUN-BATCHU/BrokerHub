package com.brokerhub.brokerageapp.entity;

public class ResetPassword {

    private String newPassword;

    private Integer resetToken;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Integer getResetToken() {
        return resetToken;
    }

    public void setResetToken(Integer resetToken) {
        this.resetToken = resetToken;
    }
}
