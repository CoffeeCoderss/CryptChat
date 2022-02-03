package com.coffeecoders.cryptchat;

public class MessageModel {
    private final static String TAG = "MessageModel";
    private String messageId, message, senderId, imageUrl ,encryptSenderMsg ,encryptReceiverMsg;
    private long timestamp;
    private boolean isProtected;

    public MessageModel() {

    }

    public MessageModel(String message,String encryptSenderMsg, String encryptReceiverMsg,
                          boolean isProtectedMode, String senderId, long timestamp) {
        this.message = message;
        this.encryptSenderMsg = encryptSenderMsg;
        this.encryptReceiverMsg = encryptReceiverMsg;
        this.isProtected = isProtectedMode;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getEncryptSenderMsg() {
        return encryptSenderMsg;
    }

    public void setEncryptSenderMsg(String encryptSenderMsg) {
        this.encryptSenderMsg = encryptSenderMsg;
    }

    public String getEncryptReceiverMsg() {
        return encryptReceiverMsg;
    }

    public void setEncryptReceiverMsg(String encryptReceiverMsg) {
        this.encryptReceiverMsg = encryptReceiverMsg;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
