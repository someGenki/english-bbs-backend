package com.yuan.common.baidu;


import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度翻译api
 * transApi.getTransResult(word);
 */
@Repository
public class TransApi {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private final String appid;
    private final String securityKey;

    public TransApi() {
        this.appid = "20200515000456177";
        this.securityKey = "4RTe7mWs8ddlve4o3RuK";
    }

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query) throws Exception {
        return getTransResult(query, "auto", "auto");
    }


    public String getTransResult(String query, String from, String to) throws Exception {
        Map<String, String> params = buildParams(query, from, to);
        return HttpGet.get(TRANS_API_HOST, params);
    }

    private Map<String, String> buildParams(String query, String from, String to) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

}
