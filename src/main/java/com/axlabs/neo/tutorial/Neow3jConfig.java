package com.axlabs.neo.tutorial;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neow3jConfig {

    @Bean
    public Neow3j neow3j() {
        return Neow3j.build(new HttpService("http://localhost:30333"));
    }
}