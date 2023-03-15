package com.moxiao.sqlmonitor.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.OpType;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.moxiao.sqlmonitor.exception.ElasticCommonException;
import com.moxiao.sqlmonitor.interceptor.SqlMonitorInterceptor;
import com.moxiao.sqlmonitor.property.ElasticsearchProperty;
import com.moxiao.sqlmonitor.store.StoreExecuteSql;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public final class EsTools {

    private static final Logger logger = LoggerFactory.getLogger(EsTools.class);

    public static final String index = "slow-sql-monitor";
    
    private static ElasticsearchClient client;
    

    private EsTools(){}

    public static boolean createClient(ElasticsearchProperty esConfig) {
        if (!StringUtils.isBlank(esConfig.getUsername())) {
            try {
                createClientInCertificate(esConfig);
                return true;
            } catch (Exception e) {
                logger.error("链接ES服务器错误", e);
                return false;
            }
        } else {
            try {
                createClient(esConfig.getUri(), esConfig.isVersionLessEleven());
                return true;
            } catch (Exception e) {
                logger.error("链接ES服务器错误", e);
                return false;
            }
        }
    }

    private static void createClient(String url, boolean versionLessEleven) {
        logger.info("准备连接无需授权的ES服务...url: " + url + "，versionLessEleven：" + versionLessEleven);
        HttpHost[] httpHosts = Arrays.stream(url.split(","))
                .map(String::trim)
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                            httpAsyncClientBuilder
                                    .addInterceptorLast((HttpResponseInterceptor)
                                            (response, context) -> response.addHeader("X-Elastic-Product", "Elasticsearch"));
                            if (versionLessEleven) {
                                httpAsyncClientBuilder.setDefaultHeaders(Collections.singletonList(
                                        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())));
                            }
                            return httpAsyncClientBuilder;
                        });

        ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(),
                new JacksonJsonpMapper());
        
        client = new ElasticsearchClient(transport);
    }

    private static void createClientInCertificate(ElasticsearchProperty esConfig) {
        String url = esConfig.getUri();
        String username = esConfig.getUsername();
        String password = esConfig.getPassword();
        logger.info("准备链接授权的ES服务...，URL地址：" + url + "。username: " + username + "，password：" + password 
                + "，finger：" + esConfig.getFingerPrint() + "，certFile：" + esConfig.getCertFile() + 
                "，versionLessEleven：" + esConfig.isVersionLessEleven());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        HttpHost[] httpHosts = Arrays.stream(url.split(","))
                .map(String::trim)
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);
        try {
            SSLContext sslContext = null;
            if (!StringUtils.isBlank(esConfig.getCertFile())) {
                File certFile = new File(esConfig.getCertFile());
                sslContext = TransportUtils.sslContextFromHttpCaCrt(certFile);
            } else if (!StringUtils.isBlank(esConfig.getFingerPrint())) {
                sslContext = TransportUtils.sslContextFromCaFingerprint(esConfig.getFingerPrint());
            }
            SSLContext finalSSlContext = sslContext;
            RestClientBuilder restClientBuilder = RestClient.builder(httpHosts)
                    .setHttpClientConfigCallback(builder -> {
                        builder.setDefaultCredentialsProvider(credentialsProvider)
                                .addInterceptorLast((HttpResponseInterceptor)
                                        (response, context) ->
                                                response.addHeader("X-Elastic-Product", "Elasticsearch"));
                        if (esConfig.isVersionLessEleven()) {
                            builder.setDefaultHeaders(Collections.singletonList(
                                    new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())));
                        }
                        if (finalSSlContext != null) {
                            builder.setSSLContext(finalSSlContext);
                        }
                        return builder;
                    });
            ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(),
                    new JacksonJsonpMapper());
            client = new ElasticsearchClient(transport);
        } catch (Exception e) {
            logger.error("创建ES客户端错误", e);
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        if (Objects.nonNull(client)) {
            try {
                client.shutdown()._transport().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                client = null;
            }
        }
    }

    public static boolean existIndex(String indexName) {
        try {
            return client.indices().exists(builder -> builder.index(indexName)).value();
        } catch (IOException e) {
            logger.error("检查索引是否存在出现问题。", e);
            throw new ElasticCommonException("检查索引", e);
        }
    }

    public static void createIndex(String indexName) {
        ElasticsearchProperty esConfig = SqlMonitorInterceptor.getSqlMonitorProperty().getEsConfig();

        try {
            CreateIndexResponse createIndexResponse = client.indices().create(builder -> builder
                    .index(indexName)
                    .waitForActiveShards(waitBuilder -> waitBuilder.count(1))
                    .settings(settingBuilder -> settingBuilder.numberOfShards(esConfig.getNumberOfShards())
                            .numberOfReplicas(esConfig.getNumberOfReplicas())));
            if (logger.isDebugEnabled()) {
                logger.debug("所有节点是否知道这个索引：[{}]，在超时之前是否分片备份是否创建完成：[{}]",
                        createIndexResponse.acknowledged(), createIndexResponse.shardsAcknowledged());
            }
        } catch (IOException e) {
            logger.error("创建索引失败，请重试。。。错误信息为：", e);
            throw new ElasticCommonException("创建索引失败", e);
        }
    }

    public static boolean add(StoreExecuteSql data, String indexName) {
        if (Objects.isNull(indexName) || Objects.equals("", indexName)) {
            logger.error("索引名称为NUll或者为空字符串");
            return false;
        }
        if (data == null) {
            logger.error("保存数据内容为NULL");
            return false;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("添加数据索引名称为：" + indexName);
        }
        if (!existIndex(indexName)) {
            createIndex(indexName);
        }
        //2.发送请求
        try {
            client.index(request -> request.index(indexName)
                    .id(data.getUniqueId())
                    .opType(OpType.Create)
                    .timeout(timeBuilder -> timeBuilder.time("3s"))
                    .withJson(new StringReader(JSONUtils.toJsonString(data))));
        } catch (IOException e) {
            logger.error("添加单个数据错误....indexName" + indexName);
            throw new ElasticCommonException("添加ES数据错误", e);
        }
        return true;
    }
    
}
