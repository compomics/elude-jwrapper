package com.compomics.jwrapper.elude.playground;

import com.compomics.jwrapper.elude.RetentionTimePredictionTask;
import com.compomics.jwrapper.elude.beans.PeptideInputBean;
import com.compomics.jwrapper.elude.beans.PeptideOutputBean;
import com.compomics.jwrapper.elude.exception.EludeException;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class RetentionTimePredictor {

    private static Logger logger = Logger.getLogger(RetentionTimePredictor.class);


// --------------------------- main() method ---------------------------

    /**
     * This class takes an input file with peptide sequences. An elude retention time prediction wil be performed for each
     * peptide and written to an output file.
     *
     * @param args -input The input file formatted as <SEQUENCE>
     *             -species The desired species, the available species are shown in the help (-h)
     *             -output The output file formatted as <SEQUENCE>/t<RETENTION_TIME>
     */
    public static void main(String[] args) throws FileNotFoundException {
        doRetentionTimePredictor(args);
    }

    public static void doRetentionTimePredictor(String [] args) {
        try {
            Options lOptions = new Options();
            createOptions(lOptions);

            DefaultParser parser = new DefaultParser();
            CommandLine line = parser.parse(lOptions, args);

            if (isValidStartup(line) == false) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("RetentionTime", lOptions);
            } else {
                logger.debug("parameters ok!");
                logger.info("Starting new RetentionTimePredictor task");

                String lInput = line.getOptionValue("input");
                BufferedReader lReader = Files.newReader(new File(lInput), Charset.defaultCharset());

                String lInputLine = "";
                HashSet<PeptideInputBean> lPeptideInputBeans = new HashSet<PeptideInputBean>();
                while ((lInputLine = lReader.readLine()) != null) {
                    String[] lSplit = lInputLine.split(" ");
                    PeptideInputBean lPeptideInputBean = new PeptideInputBean(lSplit[0]);
                    lPeptideInputBeans.add(lPeptideInputBean);
                }

                Set<PeptideOutputBean> lPeptideOutputBeans = RetentionTimePredictionTask.predictRetentionTimes(lPeptideInputBeans);

                String lOutput = line.getOptionValue("output");
                File lOutputFile = new File(lOutput);
                if (lOutputFile.exists()) {
                    lOutputFile.delete();
                }
                lOutputFile.createNewFile();

                BufferedWriter lWriter = Files.newWriter(lOutputFile, Charset.defaultCharset());
                for (PeptideOutputBean lOutputBean : lPeptideOutputBeans) {
                    Joiner join = Joiner.on("\t");
                    lWriter.write(join.join(lOutputBean.getPeptideSequence(), lOutputBean.getRetentionTime()));
                    lWriter.newLine();

                }
                lWriter.flush();
                lWriter.close();

                logger.info("exiting");
                //System.exit(0);

            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (EludeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void createOptions(Options aOptions) {
        // Prepare.

        // Set.
        aOptions.addOption("input", true, "The input with peptides. Format: <SEQUENCE>");
        aOptions.addOption("output", true, "The output file.");
    }

    /**
     * Verifies the command line start parameters.
     *
     * @return
     */
    public static boolean isValidStartup(CommandLine aLine) {
        // No params.
        if (aLine.getOptions().length == 0) {
            return false;
        }

        // Required params.
        if (aLine.getOptionValue("input") == null || aLine.getOptionValue("output") == null) {
            logger.debug("input/output file not given!!");

            return false;
        }

        // input exists?
        String lFile = aLine.getOptionValue("input");
        File lInputFile = new File(lFile);
        if (lInputFile.exists() == false) {
            logger.debug("input file string: " + lFile);
            logger.debug("input file does not exist!!");
            return false;
        }

        // if output given, does it exist? if not, make it!
        String lOutputFileName = aLine.getOptionValue("output");
        if (lOutputFileName != null) {
            File lOutputFile = new File(lOutputFileName);
            if (lOutputFile.exists() == false) {
                try {
                    lOutputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        // All is fine!
        return true;
    }
}
