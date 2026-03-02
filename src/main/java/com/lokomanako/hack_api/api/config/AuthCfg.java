package com.lokomanako.hack_api.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class AuthCfg {

    private Integer rlMax = 40;
    private Integer rlSec = 60;
}
