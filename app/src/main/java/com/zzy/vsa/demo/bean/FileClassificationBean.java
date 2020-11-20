package com.zzy.vsa.demo.bean;

import android.text.TextUtils;

public class FileClassificationBean {
    private String suffix;
    private String url;
    private String mime;
    private String path;

    public FileClassificationBean(String suffix, String url) {
        this.suffix = suffix;
        if(TextUtils.isEmpty(url)){
            this.url = "http://192.168.1.37:9003/ReBuildApk/rebuildapk/download?file=/17187/77287/2020/11/04/com.zzy.vsa.demo_1_1604475819271_uusafe_signed_64.apk";
        } else {
            this.url = url;
        }

    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
