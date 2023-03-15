package com.moxiao.sqlmonitor.property;

import com.moxiao.sqlmonitor.util.EsTools;

public class ElasticsearchProperty {
    
    private String uri;
    
    private String username;
    
    private String password;
    
    private String certFile;
    
    private String fingerPrint;
    
    private boolean versionLessEleven = false;
    
    private String numberOfShards = "3";
    
    private String numberOfReplicas = "1";
    
    private String indexName = EsTools.index;

    public String getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(String numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public String getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(String numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getCertFile() {
        return certFile;
    }

    public void setCertFile(String certFile) {
        this.certFile = certFile;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public boolean isVersionLessEleven() {
        return versionLessEleven;
    }

    public void setVersionLessEleven(boolean versionLessEleven) {
        this.versionLessEleven = versionLessEleven;
    }
}
