package com.am.training.demo.dto;

public class RowItem {
    private String  url;
    private String newFileName;

    public RowItem() {

    }

    public RowItem(String url, String newFileName) {
        this.url = url;
        this.newFileName = newFileName;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }
}
