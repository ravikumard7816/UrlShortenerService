package com.schwab.urlshortener;

public class UrlAnalyticsResponse {

    private String shortUrl;
    private String longUrl;
    private long accessCount;

    public UrlAnalyticsResponse(String shortUrl, String longUrl, long accessCount) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.accessCount = accessCount;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }
}
