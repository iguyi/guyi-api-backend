package com.guyi.apiinterface;

import com.guyi.clientsdk.client.GuyiApiClient;
import com.guyi.clientsdk.model.InterfaceUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ApiInterfaceApplicationTest {

    @Resource
    private GuyiApiClient guyiApiClient;

    @Test
    void contextLoads() {
        String result1 = guyiApiClient.getNameByGet("guyi1");
        System.out.println("测试结果1: " + result1);

        InterfaceUser user = new InterfaceUser();
        user.setUsername("guyi2");
        String result2 = guyiApiClient.getUsernameByPost(user);
        System.out.println("测试结果2: " + result2);
    }

}
