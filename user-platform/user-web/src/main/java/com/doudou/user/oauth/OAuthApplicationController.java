package com.doudou.user.oauth;

import com.alibaba.fastjson.JSONObject;
import com.doudou.controller.Controller;
import com.doudou.user.utils.OkHttpUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


@Path("/")
public class OAuthApplicationController implements Controller {

    private final ConfigProviderResolver provider = ConfigProviderResolver.instance();
    private final Config providerConfig = provider.getConfig();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String GET_TOKEN_API = "https://github.com/login/oauth/access_token";
    private static final String GET_USER_INFO_API = " https://api.github.com/user";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRETS_KEY = "client_secrets";

    @GET
    @Path("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String client_id = providerConfig.getValue(CLIENT_ID_KEY, String.class);
        request.setAttribute("client_id", client_id);
        return "login-oauth.jsp";
    }

    @GET
    @Path("/oauth")
    public String oauth(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");

        // 获取 token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", providerConfig.getValue(CLIENT_ID_KEY, String.class));
        params.put("client_secret", providerConfig.getValue(CLIENT_SECRETS_KEY, String.class));
        params.put("code", code);
        String result = OkHttpUtils.doPostWithParams(GET_TOKEN_API, params);
        logger.info("获取 GitHub token result: " + result);

        Map<String, String> resultMap = OkHttpUtils.resolveParamsByUrl("?" + result);
        String token = resultMap.get("access_token");

        // 获取个人信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "token " + token);
        String userInfo = OkHttpUtils.doGetWithHeaders(GET_USER_INFO_API, headers);
        logger.info("user info is: " + userInfo);

        JSONObject userObject = JSONObject.parseObject(userInfo);
        String name = userObject.getString("name");
        String avatar_url = userObject.getString("avatar_url");
        request.setAttribute("avatar_url", avatar_url);
        request.setAttribute("name", name);

        return "login-oauth-success.jsp";
    }
}
