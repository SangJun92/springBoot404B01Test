package org.zerock.springbootb01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringbootB01Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootB01Application.class, args);
    }

}
