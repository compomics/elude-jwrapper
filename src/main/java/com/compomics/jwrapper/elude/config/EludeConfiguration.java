package com.compomics.jwrapper.elude.config;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class is a
 */
public class EludeConfiguration {
    private static final String OS_UNIX = "unix";
    private static final String OS_WINDOWS = "win";

    private static final Logger logger = Logger.getLogger(EludeConfiguration.class);

    private static PropertiesConfiguration config;
    private static File iWorkSpace = null;
    private static String iRootDirectory = null;

    // -------------------------- STATIC BLOCKS --------------------------

    static {
        try {
            URL lResource = Resources.getResource("config/elude-jwrapper.properties");
            config = new PropertiesConfiguration(lResource);
        } catch (org.apache.commons.configuration.ConfigurationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    // -------------------------- STATIC METHODS --------------------------

    public static File getExecutable() {
        String lRootDirectory = getRootDirectory();

        String lExecutable = null;
        if (Utilities.isUnix()) {
            config.setProperty("elude.os", OS_UNIX);
            lExecutable = config.getString("elude.unix.executable");
        } else {
            config.setProperty("elude.os", OS_WINDOWS);
            lExecutable = config.getString("elude.windows.executable");
        }

        return new File(lRootDirectory, lExecutable);
    }


    public static String getEludeFolder() {
        return getRootDirectory() + File.separator + config.getString("elude.directory");
    }

    public static File getWorkSpace() {
        if (iWorkSpace == null) {
            iWorkSpace = Files.createTempDir();
        }
        return iWorkSpace;
    }

    public static String getModel() {
        return getRootDirectory() + File.separator + config.getString("elude.model");
    }

    private static String getRootDirectory() {
        if (iRootDirectory == null) {
            //find root directory
            File lFile = new File(".");
            try {
                iRootDirectory = lFile.getCanonicalPath();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return iRootDirectory;
    }
}
