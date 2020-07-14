shiro的三大组件：Subject SecurityManager Realms
subject是指当前操作用户
securityManager是指安全管理
realms是和数据进行安全交互的
shiro的配置（在applicationContext.xml）
1.securityManager配置
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
            <property name="cacheManager" ref="cacheManager"/>
            <!-- Single realm app.  If you have multiple realms, use the 'realms' property instead. -->
            <property name="sessionMode" value="native"/>
            <property name="realm" ref="jdbcRealm"/>
    </bean>
2.cacheManager配置
    <bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
            <!-- Set a net.sf.ehcache.CacheManager instance here if you already have one.  If not, a new one
                 will be creaed with a default config:
                 <property name="cacheManager" ref="ehCacheManager"/> -->
            <!-- If you don't have a pre-built net.sf.ehcache.CacheManager instance to inject, but you want
                 a specific Ehcache configuration to be used, specify that here.  If you don't, a default
                 will be used.:
            <property name="cacheManagerConfigFile" value="classpath:some/path/to/ehcache.xml"/> -->
     </bean>
3.realms配置
    <!--    realm   直接继承实现Realm的接口的类-->
        <bean id="jdbcRealm" class="com.yang.shiro.shiroRealms"></bean>
4.lifecycleBeanPostProcessor配置
//可以自动的调用spring配置的shiro的初始化和结束方法，控制shiro的生命周期
    <!-- =========================================================
             Shiro Spring-specific integration
             ========================================================= -->
        <!-- Post processor that automatically invokes init() and destroy() methods
             for Spring-configured Shiro objects so you don't have to
             1) specify an init-method and destroy-method attributes for every bean
                definition and
             2) even know which Shiro objects require these methods to be
                called. -->
        <bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>
5.启用IOC容器中bean的注解（但必须在配置lifecycleBeanPostProcessor后）
     <!-- Enable Shiro Annotations for Spring-configured beans.  Only run after
             the lifecycleBeanProcessor has run: -->
        <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"
              depends-on="lifecycleBeanPostProcessor"/>
        <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
            <property name="securityManager" ref="securityManager"/>
        </bean>
6.shiroFilter配置
        <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
                <property name="securityManager" ref="securityManager"/>
                <property name="loginUrl" value=""></property>
                <property name="successUrl" value=""></property>
                <property name="unauthorizedUrl" value=""></property>
                <!-- The 'filters' property is not necessary since any declared javax.servlet.Filter bean
                     defined will be automatically acquired and available via its beanName in chain
                     definitions, but you can perform overrides or parent/child consolidated configuration
                     here if you like: -->
                <property name="filterChainDefinitions">
                    <value>
                        /jsp/login.jsp=anon  //anon为可以匿名访问
                        /adm/** = auth       //auth为必须认证
                        /jsp/logout.jsp=logout//logout为登出
                    </value>
                </property>
            </bean>
springBoot整合shiro
    将以上的配置xml使用配置类代替（详见shiroConfig）
在对filterChainDefinitions的配置中，配置url权限采用第一次匹配优先（即若两次配置冲突，如/jsp/login.jsp=anon /**=authc login.jsp可访问，但/**=authc /jsp/login.jsp=anon login.jsp不可访问）
由于shiro利用的拦截器原理，所以注意将访问验证路径设为可匿名访问
shiro的密码比对
    shiro会使用CredentialsMatcher的比对类，将AuthenticationToken（页面传入的信息）和SimpleAuthenticationInfo（从数据库中查到的信息）
shiro的加密
    对CredentialsMatcher进行配置（但是由于该类过期，官方推荐HashCredentialsMatcher）
    配置完之后，可将页面传送的密码进行加密（配置过程详见shiroConfig）
shiro盐值加密
    由于存在密码一致的情况，所以单纯的加密并不安全，所以存在盐值加密
    盐值需要唯一，一般采用用户的username
shiro的多Realm
    由于在开发中可能会使用多个数据源，所以会用到多Realm
    SecurityManager.setRealms()方法底层会调用modularRealmAuthenticator.setRealms();最终加进验证器中
    shiro的验证策略共三种；
                  //        AllSuccessfulStrategy	所有都满足的情况
                  //        AtLeastOneSuccessfulStrategy	至少一条满足的情况(默认的)
                  //        FirstSuccessfulStrategy	第一条满足的情况
    在验证器ModularRealmAuthenticator中配置，不配置则使用默认验证策略
    在SecurityManager中要想定义验证器配置，其位置必须在setRealms（）之前，否则realms被无法加载
shiro的权限管理
    在权限判断（比如注解）的底层hasRoles/hasPermitted方法中，底层会调用doGetAuthorizationInfo(PrincipalCollection principalCollection)，所以我们在realm的该方法中进行授权
    其中，该参数为用户的username
    开发中应用注解权限管理
        @RequiresAuthentication
        　　表示当前Subject已经通过login 进行了身份验证；即Subject. isAuthenticated()返回true。

        　　@RequiresUser
        　　表示当前Subject已经身份验证或者通过记住我登录的。

        　　@RequiresGuest
        　　表示当前Subject没有身份验证或通过记住我登录过，即是游客身份。

        　　@RequiresRoles(value={“admin”, “user”}, logical= Logical.AND)
        　　@RequiresRoles(value={“admin”})
        　　@RequiresRoles({“admin“})
        　　表示当前Subject需要角色admin 和user。

        　　@RequiresPermissions (value={“user:a”, “user:b”}, logical= Logical.OR)
        　　表示当前Subject需要权限user:a或user:b。
    在多realm环境下，有一个realm中权限通过即可


    问题出现：在同一个域名下部署两个项目，两个项目的cookie相互影响
    问题原因：cookie不设置情况下默认的key为JSESSIONID，后一个会对前一个进行覆盖
    解决方法：修改其中一个项目的cookie名
    //同一个域下两个项目使用shiro，cookie值相同相互影响
       /* @Bean
        public Cookie cookieDAO() {
           Cookie cookie=new org.apache.shiro.web.servlet.SimpleCookie();
           cookie.setName("WEBSID");
           return cookie;
        }

     /**
         * shiro session的管理
         */
        @Bean
        public DefaultWebSessionManager sessionManager() {
            DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
            sessionManager.setGlobalSessionTimeout(tomcatTimeout * 1000);
            sessionManager.setSessionDAO(sessionDAO());
            //将修改的cookie放入sessionManager中
            sessionManager.setSessionIdCookie(cookieDAO());
            Collection<SessionListener> listeners = new ArrayList<SessionListener>();
            listeners.add(new BDSessionListener());
            sessionManager.setSessionListeners(listeners);
            return sessionManager;
        }