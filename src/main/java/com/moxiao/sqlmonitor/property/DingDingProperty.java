package com.moxiao.sqlmonitor.property;

import com.moxiao.sqlmonitor.notice.dingding.DingDingType;

import java.util.List;
import java.util.Locale;

public class DingDingProperty {

    private String secret;
    
    private String accessToken;
    
    private List<String> atMobiles;
    
    private boolean collectionAble = false;
    
    private DingDingType type = DingDingType.MARKDOWN;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    public DingDingType getType() {
        return type;
    }

    public void setType(String type) {
        try {
            this.type = Enum.valueOf(DingDingType.class, type.toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            // ignore
        }
    }

    public boolean isCollectionAble() {
        return collectionAble;
    }

    public void setCollectionAble(boolean collectionAble) {
        this.collectionAble = collectionAble;
    }
}
