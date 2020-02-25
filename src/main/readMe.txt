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
3.
