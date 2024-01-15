package com.rykk.rykapiinterface;

import com.rykk.rykapiclientsdk.client.RykApiClient;
import com.rykk.rykapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RykapiInterfaceApplicationTests {

    @Resource
    private RykApiClient rykApiClient;

    @Test
    void contextLoads() {
        String result = rykApiClient.getNameByGet("axax");
        User user = new User();
        user.setName("zxzx");
        user.setAge(18);
        String userNameByPost = rykApiClient.getUserNameByPost(user);
//        System.out.println(result);
//        System.out.println(userNameByPost);

    }

}
