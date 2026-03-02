package com.lokomanako.hack_api.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtCfg {

    private String secret = "9ce57517f16f4e1eb52f7be17313f4f744307b8c2b6b9e11f0f1a4c2e6d8792a";
    private Integer accessMin = 15;
    private Integer refreshDays = 30;
}
