package com.rykk.rykapiclientsdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.rykk.rykapiclientsdk.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.rykk.rykapiclientsdk.utils.SignUtils.genSign;


/**
 * 调用第三方接口的客户端
 */
public class RykApiClient {
    private String accessKey;

    private String secretKey;

    private static final String GATEWAY_HOST = "http://127.0.0.1:8090";

    public RykApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.get(GATEWAY_HOST + "/api/basic/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result = HttpUtil.post(GATEWAY_HOST + "api/basic/name", paramMap);
        System.out.println(result);
        return result;
    }

    public String getUserNameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);

        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/basic/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        int status = httpResponse.getStatus();
        String result = httpResponse.body();
        System.out.println(status);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        //这个参数一定不能传
        //hashMap.put("secretKey", secretKey);
        //生成10000以内的随机数
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        //请求体
        hashMap.put("body", body);
        //时间戳，防止重复利用请求
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        //生成签名
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }
}
