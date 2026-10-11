
package com.hospital.vidasalud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HospitalVidaSaludEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalVidaSaludEurekaApplication.class, args);
    }
}