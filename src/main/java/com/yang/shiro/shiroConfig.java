package com.yang.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class shiroConfig {
    //不加这个注解不生效，具体不详
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
    //加入注解的使用，不加入这个注解不生效
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    @Bean
    public EhCacheManager ehCacheManager(){
        EhCacheManager ehCacheManager=new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return ehCacheManager;
    }

    //将自己的验证方式加入容器
    @Bean
    public shiroRealms  myShiroRealm() {
        shiroRealms realms=new shiroRealms();
        HashedCredentialsMatcher hashedCredentialsMatcher=new HashedCredentialsMatcher();
        //名称
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        hashedCredentialsMatcher.setHashIterations(1024);
        realms.setCredentialsMatcher(hashedCredentialsMatcher);
        return realms;
    }
    @Bean
    public secondRealm secondRealm()
    {
        secondRealm secondRealm=new secondRealm();
        HashedCredentialsMatcher hashedCredentialsMatcher=new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(1024);
        secondRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return secondRealm;
    }

    //权限管理，配置主要是Realm的管理认证
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(ehCacheManager());
//        securityManager.setRealm(myShiroRealm());
//        若想要配置Authenticator，此配置必须在setRealms前
//        securityManager.setAuthenticator(modularRealmAuthenticator());
//        List<Realm> list=new ArrayList<>();
//        list.add(myShiroRealm());
//        list.add(secondRealm());
//        securityManager.setRealms(list);//底层会调用modularRealmAuthenticator.setRealms();将realms加进Authenticator
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }
//    @Bean
//    public ModularRealmAuthenticator modularRealmAuthenticator(){
//        ModularRealmAuthenticator modularRealmAuthenticator=new ModularRealmAuthenticator();
//        AtLeastOneSuccessfulStrategy atLeastOneSuccessfulStrategy=new AtLeastOneSuccessfulStrategy();
////        modularRealmAuthenticator.setRealms();
////        AllSuccessfulStrategy	所有都满足的情况
////        AtLeastOneSuccessfulStrategy	至少一条满足的情况(默认的)
////        FirstSuccessfulStrategy	第一条满足的情况
//        modularRealmAuthenticator.setAuthenticationStrategy(atLeastOneSuccessfulStrategy);
//        return modularRealmAuthenticator;
//    }

    //Filter工厂，设置对应的过滤条件和跳转条件
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> map = new HashMap<>();
        //在此处的配置url权限采用第一次匹配优先（即若两次配置冲突，如/jsp/login.jsp=anon /**=authc login.jsp可访问，但/**=authc /jsp/login.jsp=anon login.jsp不可访问）
        //anon：为可被匿名访问  authc：需要认证  logout：登出
        map.put("/jsp/login.jsp", "anon");
        //对验证路径放开权限
        map.put("/shiro/index","roles[admin]");
        map.put("/shiro/login","anon");
        map.put("/jsp/success.jsp","anon");
        //对所有用户认证
        map.put("/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("/jsp/login.jsp");
        //首页
        shiroFilterFactoryBean.setSuccessUrl("/jsp/success.jsp");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/jsp/unauthorized.jsp");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }
}