package com.yang.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class shiroRealms extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Logger logger=LoggerFactory.getLogger(getClass().getName());
        logger.info(principalCollection.toString());
        //参数principalCollection为用户名
        SimpleAuthorizationInfo simpleAuthorizationInfo=new SimpleAuthorizationInfo();
        //将数据库内查出的角色添加进即可
        simpleAuthorizationInfo.addRole("admin");
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        Logger log= LoggerFactory.getLogger(getClass().getName());
        UsernamePasswordToken usernamePasswordToken= (UsernamePasswordToken) authenticationToken;
        Object password = null;
        ByteSource byteSource=null;
        if (usernamePasswordToken.getUsername().equals("123456"))
        {
            byteSource=ByteSource.Util.bytes("yang");
            password="cc4d94e5b62f97e26ac1523a959936e0";
            log.info("------>欢迎yang");
        }
        if(usernamePasswordToken.getUsername().equals("654321"))
        {
            byteSource=ByteSource.Util.bytes("H");
            password="02243f19d47fcf294cdc20241cc992e6";
            log.info("-------->欢迎H");
        }
        //在普通登录验证的时候，该构造器的构造参数
        //参数1 principal：认证的实体信息，可以是用户的username，也可以是用户的实体对象
        //参数2 credential：用户的密码（在数据中查到的）
        //参数3 realmName：realm的名，调用getName（）即可
        //在盐值加密的时候，该构造器的构造参数
        //参数一 principal：认证实体信息，如上
        //参数二 credential：用户的密码（在数据库查到的）
        //参数三 credentialSalt：盐值（通过一下方式获取）
//        ByteSource byteSource=ByteSource.Util.bytes("yang");
        //参数四 realmName：realm的名字，如上获取即可
        SimpleAuthenticationInfo simpleAuthenticationInfo=new SimpleAuthenticationInfo(usernamePasswordToken.getUsername(),password,byteSource,getName());
        return simpleAuthenticationInfo;
    }
}
