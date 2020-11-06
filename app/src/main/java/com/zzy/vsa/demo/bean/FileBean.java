package com.zzy.vsa.demo.bean;

import com.zzy.vsa.demo.appenv.FileType;

/**
 * Created by ${zhaoyanjun} on 2017/1/11.
 */

public class FileBean {

    private String name;
    private String path;
    private FileType fileType;
    private int childCount ;
    private long size ;

    public FileBean(){}

    public FileBean(String name, String path,FileType fileType, int childCount, long size){
        this.name = name;
        this.path = path;
        this.fileType = fileType;
        this.childCount = childCount;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}