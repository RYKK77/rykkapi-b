package com.rykk.rykapiclientsdk;


import com.rykk.rykapiclientsdk.client.RykApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("rykk.client")
@Data
//能够扫描到包
@ComponentScan
public class RykapiClientConfig {

    private String accessKey;

    private String secretKey;


    @Bean
    public RykApiClient rykApiClient() {
        return new RykApiClient(accessKey, secretKey);

    }

}
