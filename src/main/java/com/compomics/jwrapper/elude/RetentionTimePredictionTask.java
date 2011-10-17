package com.compomics.jwrapper.elude;

import com.compomics.jwrapper.elude.beans.PeptideInputBean;
import com.compomics.jwrapper.elude.beans.PeptideOutputBean;
import com.compomics.jwrapper.elude.config.EludeConfiguration;
import com.compomics.jwrapper.elude.exception.EludeException;
import com.compomics.jwrapper.elude.threads.CommandThread;
import com.google.common.io.Files;
import org.apache.log4j.Logger;
import sun.misc.ConditionLock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class is a
 */
public class RetentionTimePredictionTask {
    private static Logger logger = Logger.getLogger(RetentionTimePredictionTask.class);

    /**
     * This method predicts the retention time behaviour of a set of PeptideInputBeans
     *
     * @param aPeptideInputBeans
     * @return
     */
    public static Set<PeptideOutputBean> predictRetentionTimes(Set<PeptideInputBean> aPeptideInputBeans) throws EludeException {
        Set<PeptideOutputBean> lPeptideOutputBeans = null;

        try {
            // Ok, first write the given inputbeans to a tmp file.
            File lWorkSpace = EludeConfiguration.getWorkSpace();
            File lTmpInputFile = new File(lWorkSpace, "com.compomics.jwrapper.elude-in-" + System.currentTimeMillis() + ".tmp");
            File lTmpOutputFile = new File(lWorkSpace, "com.compomics.jwrapper.elude-out-" + System.currentTimeMillis() + ".tmp");

            // Create the file
            lTmpInputFile.createNewFile();
            lTmpOutputFile.createNewFile();

            // Open a BufferedWriter.
            BufferedWriter lWriter = Files.newWriter(lTmpInputFile, Charset.defaultCharset());
            for (PeptideInputBean lPeptideInputBean : aPeptideInputBeans) {
                lWriter.write(lPeptideInputBean.getEludeInputNotation());
                lWriter.write("\n");
            }
            lWriter.flush();
            lWriter.close();

            // Ok, this file must now be passed to Elude.
            File lExecutable = EludeConfiguration.getExecutable();
            String[] lCommand = makeEludeCommand(lExecutable, lTmpInputFile, lTmpOutputFile);
            CommandThread lCommandThread = new CommandThread(lTmpInputFile.getName(), lCommand, lTmpOutputFile);

            // make a RRunner from this RSource.
            logger.info("submitting com.compomics.jwrapper.elude task");
            Future lFuture = Executors.newSingleThreadExecutor().submit(lCommandThread);
            ConditionLock lConditionLock = new ConditionLock();

            // Keep busy until the Thread has finished.
            synchronized (lConditionLock) {
                while (lFuture.isDone() != true) {
                    lConditionLock.wait(1000);
                    System.out.println(".");
                }
            }

            // Ok, retention time prediction has finished. Now parse the output file.
            lPeptideOutputBeans = RetentionTimePredictionParser.parse(lTmpOutputFile);
            // Return the result.

            return lPeptideOutputBeans;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return lPeptideOutputBeans;
    }

    /**
     * This convenience method creates an appropriate Command to launch com.compomics.jwrapper.elude
     *
     * @param aInputFile
     * @return
     */
    private static String[] makeEludeCommand(File aPath, File aInputFile, File aOutputFile) throws IOException {
        String lModel = EludeConfiguration.getModel();

        String[] lCommand = new String[]{aPath.getCanonicalPath(), "-l", lModel, "-e", aInputFile.getAbsolutePath(), "-o", aOutputFile.getAbsolutePath()};
        logger.debug("com.compomics.jwrapper.elude command:\t" + lCommand.toString());

        return lCommand;
    }

}
