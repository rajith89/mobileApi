package com.udipoc.api.config.BeanDefine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udipoc.api.util.image.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDefine {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CbeffUtil cbeffUtil() { return new CbeffImpl(); }
}
