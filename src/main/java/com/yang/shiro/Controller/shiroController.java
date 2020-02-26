package com.yang.shiro.Controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shiro")
public class shiroController {
    @RequestMapping("/login")
    public String shiroLogin(String username,String password)
    {
        Logger log= LoggerFactory.getLogger(getClass().getName());
        Subject subject= SecurityUtils.getSubject();
        Session session=subject.getSession();
        session.setAttribute("someKey","aValue");
        if(!subject.isAuthenticated())
        {
            UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(username,password);
            try{
               subject.login(usernamePasswordToken);
            }catch (Exception e){
                log.info("登录失败"+e.getMessage());
            }
        }
        return "redirect:/jsp/success.jsp";
    }
}
