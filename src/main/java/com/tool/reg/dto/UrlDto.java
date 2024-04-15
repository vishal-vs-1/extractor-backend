package com.tool.reg.dto;

public class UrlDto {

    String url;
    String input;

    public UrlDto(String url, String input) {
        this.url = url;
        this.input = input;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
