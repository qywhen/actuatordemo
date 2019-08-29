package com.wisd.actuatordemo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ActuatordemoApplication {

    static void main(String[] args) {
        SpringApplication.run(ActuatordemoApplication, args)
    }

    @Bean
    ObjectMapper objectMapper() { new ObjectMapper() }

}
