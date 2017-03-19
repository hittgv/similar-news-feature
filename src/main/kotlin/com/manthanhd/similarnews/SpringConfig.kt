package com.manthanhd.similarnews

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * Created by manthan on 19/03/17.
 */
@Configuration
open class SpringConfig {
    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}