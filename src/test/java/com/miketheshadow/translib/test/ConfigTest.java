package com.miketheshadow.translib.test;

import com.miketheshadow.translib.TransLib;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;

public class ConfigTest {

    Logger logger = LoggerFactory.getLogger(ConfigTest.class);

    @Test
    public void testConfigTools() {

        // Very small footprint creation
        File file = new File("tests");
        TransLib transLib = new TransLib("en_us.yml",file);

        // Get a known key using the base config
        logger.info(() -> transLib.getTranslation("example-message-four"));

        // Change active config to fr
        transLib.setActiveConfig("fr.yml");

        // Get the same known key from the second config
        logger.info(() -> transLib.getTranslation("example-message-four"));

        // Yet to be added to config values do not error instead ->
        logger.info(() -> transLib.getTranslation("example-message-ten"));
    }

}
