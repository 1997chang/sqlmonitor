package com.moxiao.sqlmonitor.notice;

import com.moxiao.sqlmonitor.notice.dingding.DingDingNoticePolicyImpl;
import com.moxiao.sqlmonitor.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;

public class NoticeHolder {

    private static List<NoticePolicy> noticePolicyList;

    public static synchronized void build(String noticePolicy) {
        if (noticePolicyList != null) {
            return;
        }
        if (noticePolicy == null || noticePolicy.isEmpty()) {
            noticePolicyList = Collections.emptyList();
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(noticePolicy, ",", false);
            HashSet<String> distinctSet = new HashSet<>(stringTokenizer.countTokens());
            noticePolicyList = new ArrayList<>(stringTokenizer.countTokens());
            while (stringTokenizer.hasMoreTokens()) {
                String nextNoticeImpl = stringTokenizer.nextToken().trim();
                if (nextNoticeImpl.isEmpty() || distinctSet.contains(nextNoticeImpl)) {
                    continue;
                }
                distinctSet.add(nextNoticeImpl);
                if ("DINGDING".equals(nextNoticeImpl)) {
                    noticePolicyList.add(new DingDingNoticePolicyImpl());
                } else {
                    defaultImpl(nextNoticeImpl);
                }
            }
        }
    }

    public static void defaultImpl(String noticePolicy) {
        try {
            Class<NoticePolicy> clazzImpl = ClassUtils.getClazz(noticePolicy);
            Constructor<NoticePolicy> constructor = clazzImpl.getConstructor();
            noticePolicyList.add(constructor.newInstance());
        } catch (Exception e) {
            //ignore
        }
    }

    public static List<NoticePolicy> getNoticePolicyList() {
        return noticePolicyList;
    }
}
