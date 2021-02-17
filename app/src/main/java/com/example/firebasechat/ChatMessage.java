package com.example.firebasechat;

public class ChatMessage {//파이어베이스 디비에 저장될 값들
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;

    //기본 생성자 만들어주기
    public ChatMessage(){

    }

    //아이디를 제외한 파라미터 받는 생성자도 만들어주기
    public ChatMessage(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
