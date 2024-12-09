package ru.otus.java.pro.webserver.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class.getName());

    private final Properties configProperties;

    public Config() {
        this.configProperties = new Properties();
        String configFileName = "config.properties";
        ClassLoader classLoader = Config.class.getClassLoader();
        try (InputStream configInputStream = classLoader.getResourceAsStream(configFileName)) {
            if (configInputStream != null) {
                this.configProperties.load(configInputStream);
                logger.debug("Simple HTTP web server configuration properties have been initialized");
            } else {
                logger.error("Config file {} not found in resources folder", configFileName);
            }
        } catch (IOException e) {
            logger.error("General I/O exception", e);
        }
    }

    public String getProperty(String key, String defaultValue) {
        return this.configProperties.getProperty(key, defaultValue);
    }
}
