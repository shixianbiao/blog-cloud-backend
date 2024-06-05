package com.xb.blog.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.xb.blog.auth.config.security.service.AuthUserDetailsService;
import com.xb.blog.auth.vo.AuthUserVo;
import com.xb.blog.common.constants.Result;
import com.xb.blog.common.utils.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @GetMapping("getAuthUser")
    public Result getAuthUser(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StrUtil.isNotBlank(token)) {
            Boolean isAuth = AuthUtil.isAuth(token);
            if (isAuth) {
                String username = AuthUtil.getUsernameFromToken(token);
                UserDetails user = authUserDetailsService.loadUserByUsername(username);
                AuthUserVo authUser = new AuthUserVo();
                BeanUtils.copyProperties(user, authUser);
                return Result.success(authUser);
            }
        }
        return Result.success(null);
    }
}