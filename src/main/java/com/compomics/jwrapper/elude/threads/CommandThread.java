package com.compomics.jwrapper.elude.threads;

import com.compomics.jwrapper.elude.config.EludeConfiguration;
import com.google.common.io.Files;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

public class CommandThread extends Thread {
// ------------------------------ FIELDS ------------------------------


    private static Logger logger = Logger.getLogger(CommandThread.class);

    private String[] iCommand;
    private String iName;
    public BufferedWriter iWriter;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Creates a new Thread to run the given command.
     *
     * @param aCommand String with the command that will be run from commandline.
     */
    public CommandThread(String aName, String[] aCommand, File aOutputFile) {
        super(aName);
        iName = aName;
        iCommand = aCommand;
        iWriter = null;
        try {
            iWriter = Files.newWriter(aOutputFile, Charset.defaultCharset());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Runnable ---------------------


    public void run() {
        BufferedReader out = null;
        try {
            long startTime = System.currentTimeMillis();
            String strOutputline;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date(System.currentTimeMillis());

            ProcessBuilder pb = new ProcessBuilder();

            // Set the working directory.
            File lEludeFolder = new File(EludeConfiguration.getEludeFolder());
            Map<String,String> lEnvironment = pb.environment();
            pb = pb.directory(lEludeFolder);
            pb = pb.command(iCommand);
            pb = pb.redirectErrorStream(true);

            logger.debug("Executing command '" + Arrays.toString(iCommand) + "'");

            Process processus = pb.start();

            out = new BufferedReader(new InputStreamReader(processus.getInputStream()));
            while ((strOutputline = out.readLine()) != null) {
                logger.debug(strOutputline);
                iWriter.write(strOutputline);
                iWriter.newLine();
            }
            System.out.println(iName + "\nRESULT " + processus.waitFor());
            out.close();
            iWriter.flush();
            iWriter.close();
            processus.destroy();
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Finished command " + Arrays.toString(iCommand) + "\tDuration : " + (duration / 1000) + "sec");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
