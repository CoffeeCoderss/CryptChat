package com.coffeecoders.cryptchat;

public class MessageModel {
    private final static String TAG = "MessageModel";

    private String message, senderId, imageUrl;
    private long timestamp;

    public MessageModel() {
    }

    public MessageModel(String senderId, String imageUrl, long timestamp) {
        this.senderId = senderId;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
