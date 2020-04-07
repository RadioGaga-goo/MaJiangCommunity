package com.example.majiang.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.majiang.dto.GithubAccessTokenDto;
import com.example.majiang.dto.GithubUserDto;
import com.example.majiang.mapper.UserMapper;
import com.example.majiang.model.User;
import com.example.majiang.utils.OkHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) throws IOException {

        GithubUserDto githubUser = githubOauthLogin(code, state);
        if(githubUser != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setCreateTime(LocalDateTime.now());
            user.setModifiedTime(LocalDateTime.now());
            userMapper.insert(user);
            response.addCookie(new Cookie("token", token));
        }
        return "redirect:/";
    }

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private GithubUserDto githubOauthLogin(String code, String state) throws IOException {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        GithubAccessTokenDto accessTokenDto = new GithubAccessTokenDto();
        accessTokenDto.setCode(code);
        accessTokenDto.setState(state);
        accessTokenDto.setClientId(clientId);
        accessTokenDto.setClientSecret(clientSecret);
        accessTokenDto.setRedirectUri(redirectUri);
        String rspStr = OkHttpUtil.post("https://github.com/login/oauth/access_token",
                JSONObject.toJSONString(accessTokenDto, config));
        String accessToken = rspStr.split("&")[0].split("=")[1];
        String userJSON = OkHttpUtil.get("https://api.github.com/user?access_token=" + accessToken);

        return JSONObject.parseObject(userJSON, GithubUserDto.class);
    }
}
