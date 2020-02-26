package com.yang.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShiroApplicationTests {
    //该测试方法获取加密后的值,方便模拟从数据库取出的密码
    @Test
    void contextLoads() {
        String hashCredential="MD5";//加密形式
        Object password="12345";//密码
        Object salt= ByteSource.Util.bytes("H");//加盐
        int hashIteration=1024;//加密次数
        Object result=new  SimpleHash(hashCredential,password,salt,hashIteration);
        System.out.println(result);
    }

}
