package com.guyi.apiinterface.controller;

import com.guyi.clientsdk.model.InterfaceUser;
import com.guyi.clientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 查询名称 API
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody InterfaceUser user, HttpServletRequest httpServletRequest) {
        String accessKey = httpServletRequest.getHeader("accessKey");
        String body = httpServletRequest.getHeader("body");
        String sign = httpServletRequest.getHeader("sign");
        String nonce = httpServletRequest.getHeader("nonce");
        String timestamp = httpServletRequest.getHeader("timestamp");

        if (! "guyi".equals(accessKey)) {
            throw new RuntimeException("无权限");
        }

        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限");
        }

        return "POST 你的用户名是" + user.getUsername();
    }

}
