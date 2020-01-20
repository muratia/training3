package com.am.training.demo;


import com.am.training.demo.controller.FileDownloaderController;
import com.am.training.demo.ifs.FileSystemStorageService;
import com.am.training.demo.ifs.StorageProperties;
import com.am.training.demo.ifs.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableAutoConfiguration
@AutoConfigurationPackage
@ComponentScan(excludeFilters =
@ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "com\\.am\\.training\\.demo\\.utils\\..*"))
@ComponentScan(excludeFilters =
@ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "com\\.am\\.training\\.demo\\.dto\\..*"))
@EnableConfigurationProperties(StorageProperties.class)

public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloaderController.class);

    public static    StorageProperties properties;


    public static StorageService storageService;

    public static Path rootLocation;

    public static void main(String[] args) throws IOException {

        SpringApplication.run(DemoApplication.class, args);
        properties = new StorageProperties();
        storageService = new FileSystemStorageService();


        rootLocation = Paths.get(properties.getLocation());
    }
}
