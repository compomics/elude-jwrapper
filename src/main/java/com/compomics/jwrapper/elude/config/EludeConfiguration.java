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
    private static final String OS_UNIX = "mac";
    private static final String OS_WINDOWS = "win";

    private static final Logger logger = Logger.getLogger(EludeConfiguration.class);

    private static PropertiesConfiguration config;
    private static File iWorkSpace = null;
    private static String iRootDirectory = null;
    /**
     * The elude.unix.standalone.executable property value in the config file.
     * We added this option to be able to run on linux distributions where the elude binary is installed
     * on the system and not shipped with the jar. If this value is empty, the shipped windows/mac elude version will be used.
     */
    private static String unixStandaloneExecutable = null;

    // -------------------------- STATIC BLOCKS --------------------------

    static {
        try {
            URL lResource = Resources.getResource("config/elude-jwrapper.properties");
            config = new PropertiesConfiguration(lResource);
            unixStandaloneExecutable = config.getString("elude.unix.standalone.executable");
        } catch (org.apache.commons.configuration.ConfigurationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    // -------------------------- STATIC METHODS --------------------------

    public static File getExecutable() {
        File executable;
        if (unixStandaloneExecutable.isEmpty()) {
            String lRootDirectory = getRootDirectory();

            String lExecutable = null;
            if (Utilities.isUnix()) {
                config.setProperty("elude.os", OS_UNIX);
                lExecutable = config.getString("elude.unix.executable");
            } else {
                config.setProperty("elude.os", OS_WINDOWS);
                lExecutable = config.getString("elude.windows.executable");
            }

            executable = new File(lRootDirectory, lExecutable);
        } else {
            executable = new File(unixStandaloneExecutable);
        }

        return executable;
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
        String model;
        if (unixStandaloneExecutable.isEmpty()) {
            model = getRootDirectory() + File.separator + config.getString("elude.model");
        } else {
            model = config.getString("elude.unix.standalone.model");
        }
        return model;
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
