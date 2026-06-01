package com.stephen.AA_Main;

import com.stephen.BaseStats.BaseStats_Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication(scanBasePackages = "com.stephen")
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("PoolManager application starting...");
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner test(ApplicationContext ctx) {
        return args -> {
            System.out.println(ctx.getBeansOfType(BaseStats_Repository.class));
        };
    }
    @Bean
    CommandLineRunner debug(BaseStats_Repository repo) {
        return args -> System.out.println("REPO LOADED: " + repo.getClass());
    }

    @GetMapping("/public/ping")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Backend is alive!");
    }
}