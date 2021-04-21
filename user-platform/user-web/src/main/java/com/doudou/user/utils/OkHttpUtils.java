package com.doudou.user.utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OkHttpUtils {

    private static final Logger logger = Logger.getLogger(OkHttpUtils.class.getName());

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    static {
        // DEFAULT_HEADERS.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36");
    }

    public static Map<String, String> resolveParamsByUrl(String url) {
        Map<String, String> map = new HashMap<>();
        String paramUrl = url.substring(url.indexOf("?") + 1);
        String[] params = paramUrl.split("&");
        for (String param : params) {
            int index = param.indexOf("=");
            if (index > 0) {
                map.put(param.substring(0, index), param.substring(index + 1));
            }
        }
        return map;
    }

    /**
     * get
     *
     * @param url url
     * @return result
     */
    public static String doGet(String url) {
        return doGet(url, DEFAULT_HEADERS, new HashMap<>());
    }


    /**
     * get
     *
     * @param url    url
     * @param params params
     * @return result
     */
    public static String doGetWithParams(String url, Map<String, String> params) {
        return doGet(url, DEFAULT_HEADERS, params);
    }

    /**
     * get
     *
     * @param url     url
     * @param headers headers
     * @return result
     */
    public static String doGetWithHeaders(String url, Map<String, String> headers) {
        headers = null == headers ? DEFAULT_HEADERS : headers;
        return doGet(url, headers, new HashMap<>());
    }

    /**
     * post
     *
     * @param url url
     * @return result
     */
    public static String doPost(String url) {
        return doPost(url, DEFAULT_HEADERS, new HashMap<>());
    }

    /**
     * get
     *
     * @param url    url
     * @param params params
     * @return result
     */
    public static String doPostWithParams(String url, Map<String, String> params) {
        return doPost(url, DEFAULT_HEADERS, params);
    }

    /**
     * get
     *
     * @param url     url
     * @param headers params
     * @return result
     */
    public static String doPostWithHeaders(String url, Map<String, String> headers) {
        headers = null == headers ? DEFAULT_HEADERS : headers;
        return doPost(url, headers, new HashMap<>());
    }

    /**
     * get
     *
     * @param url     url
     * @param headers headers
     * @param params  parameters
     * @return result
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        logger.info(String.format("method = doGet, url = %s, headers = %s, params = %s", url, headers.toString(), params.toString()));
        String urlWithParam = url;
        if (!params.isEmpty()) {
            urlWithParam += params.keySet().stream().map(key -> key + "=" + params.get(key))
                    .collect(Collectors.joining("&", "?", ""));
        }
        Request.Builder requestBuilder = new Request.Builder().url(urlWithParam);
        return sendRequest(headers, requestBuilder);
    }

    /**
     * @param url     url
     * @param headers headers
     * @param params  params
     * @return result
     */
    public static String doPost(String url, Map<String, String> headers, Map<String, String> params) {
        logger.info(String.format("method = doPost, url = %s, headers = %s, params = %s", url, headers.toString(), params.toString()));
        // 构建参数的对象
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entity : params.entrySet()) {
            formBuilder.add(entity.getKey(), defaultValue(entity.getValue(), ""));
        }
        Request.Builder requestBuilder = new Request.Builder().url(url).post(formBuilder.build());
        return sendRequest(headers, requestBuilder);
    }

    @NotNull
    private static String sendRequest(Map<String, String> headers, Request.Builder requestBuilder) {
        for (Map.Entry<String, String> entity : headers.entrySet()) {
            requestBuilder.addHeader(entity.getKey(), defaultValue(entity.getValue(), ""));
        }
        // 构建请求项
        Request request = requestBuilder.build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response resp = client.newCall(request).execute();
            if (resp.isSuccessful()) {
                return resp.body().string();
            }else{
                logger.info("response code = " + resp.code());
                logger.info("response message = " + resp.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String defaultValue(Object origin, String defaultValue) {
        return (null != origin && !"".equals(origin.toString())) ? origin.toString() : defaultValue;
    }

}
