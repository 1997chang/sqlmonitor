package com.moxiao.sqlmonitor.util;

import com.moxiao.sqlmonitor.log.Logger;
import com.moxiao.sqlmonitor.log.LoggerFactory;
import com.moxiao.sqlmonitor.notice.dingding.DingDingResponse;
import com.moxiao.sqlmonitor.property.DingDingProperty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public final class DingDingUtils {

    private static final Logger logger = LoggerFactory.getLog(DingDingUtils.class);
    
    private static final Integer SUCCESS = 0;

    public static final String DING_DING_HTTP_PREFIX = "https://oapi.dingtalk.com/robot/send?access_token=";

    private DingDingUtils() {}

    public static String sendText(DingDingProperty dingDingProperty, String context) {
        if (logger.isDebugEnabled()) {
            logger.debug("准备发送Text类型的消息，传递内容为：" + context + "钉钉实体为：" + JSONUtils.toJsonString(dingDingProperty));
        }
        return send(dingDingProperty, new TextContext(new Text(context)));
    }

    public static String sendMarkDown(DingDingProperty dingDingProperty, String title, String context) {
        if (logger.isDebugEnabled()) {
            logger.debug("准备发送MarkDown类型的消息，传递内容为：" + context  + "，标题为：" + title + 
                    "，钉钉实体为：" + JSONUtils.toJsonString(dingDingProperty));
        }
        return send(dingDingProperty, new MarkDownContext(new MarkDown(title, context)));
    }

    private static String send(DingDingProperty dingDingProperty, DingDingContext context) {
        if (dingDingProperty == null || StringUtils.isBlank(dingDingProperty.getAccessToken())) {
            throw new IllegalArgumentException("钉钉参数不正确");
        }
        if (dingDingProperty.getAtMobiles() != null && dingDingProperty.getAtMobiles().size() > 0) {
            context.setAt(new At(dingDingProperty.getAtMobiles()));
        }
        String url = DING_DING_HTTP_PREFIX + dingDingProperty.getAccessToken();
        String timestamp = System.currentTimeMillis() + "";
        String encryption = encryption(dingDingProperty.getSecret(), timestamp);
        url = url + "&timestamp=" + timestamp + "&sign=" + encryption;
        String body = HttpUtils.post(url, JSONUtils.toJsonString(context));
        DingDingResponse response = JSONUtils.parseString(body, DingDingResponse.class);
        if (response != null && !SUCCESS.equals(response.getErrcode())) {
            logger.error("发送钉钉通知失败，错误编码为：" + response.getErrcode() + "。错误消息描述：" + response.getErrmsg());
        } 
        return body;
    }

    private static String encryption(String secret, String timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class DingDingContext {
        private String msgtype;
        
        private At at;

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public At getAt() {
            return at;
        }

        public void setAt(At at) {
            this.at = at;
        }
    }
    
    private static class TextContext extends DingDingContext {
        private final Text text;

        public TextContext(Text text) {
            this.text = text;
            setMsgtype("text");
        }

        public Text getText() {
            return text;
        }
    }
    
    private static class MarkDownContext extends DingDingContext {
        private final MarkDown markdown;

        public MarkDownContext(MarkDown markdown) {
            this.markdown = markdown;
            setMsgtype("markdown");
        }

        public MarkDown getMarkdown() {
            return markdown;
        }
    }
    
    private static class At {
        private final List<String> atMobiles;

        public At(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public List<String> getAtMobiles() {
            return atMobiles;
        }
    }
    
    private static class Text {
        
        private final String content;

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
    
    private static class MarkDown {
        private final String title;
        
        private final String text;

        public MarkDown(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }
    }
}
