package com.yang.shiro.Controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/shiro")
public class shiroController {
    @ResponseBody
    @RequiresRoles({"admin"})
    @RequestMapping("/index")
    public String index()
    {
        return "测试成功";
    }
    @RequestMapping(value = "/login")
    public String shiroLogin(String username,String password)
    {
        Logger log= LoggerFactory.getLogger(getClass().getName());
        Subject subject= SecurityUtils.getSubject();
        Session session=subject.getSession();
        session.setAttribute("someKey","aValue");
        if(!subject.isAuthenticated())
        {
            UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(username,password);
//            usernamePasswordToken.setRememberMe(true);
            try{
               subject.login(usernamePasswordToken);
            }catch (UnknownAccountException e){
                log.info("登录失败");
            }
        }
        return "redirect:/jsp/success.jsp";
    }
}
