package com.server.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticResourceConfiguration implements WebMvcConfigurer {
    private final AuthProperties authProperties;

    @Autowired
    public StaticResourceConfiguration(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(authProperties.getStorage().getRoot()).toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:" + path)
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

    @Bean
    CommandLineRunner createStorageDirs() {
        return args -> {
            createFolder(Paths.get(authProperties.getStorage().getRoot()));
            createFolder(Paths.get(authProperties.getStorage().getRoot(), authProperties.getStorage().getProfile()));
        };
    }

    private void createFolder(Path path) {
        File file = path.toFile();
        boolean isFolderExist = file.exists() && file.isDirectory();
        if (!isFolderExist) {
            file.mkdir();
        }
    }
}