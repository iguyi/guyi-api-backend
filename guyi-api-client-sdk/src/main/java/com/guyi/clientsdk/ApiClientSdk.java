package com.guyi.clientsdk;

import com.guyi.clientsdk.client.GuyiApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("guyiapi.client")
@Data
@ComponentScan
public class ApiClientSdk {

    private String accessKey;

    private String secretKey;

    @Bean
    public GuyiApiClient guyiApiClient() {
        return new GuyiApiClient(accessKey, secretKey);
    }
}
