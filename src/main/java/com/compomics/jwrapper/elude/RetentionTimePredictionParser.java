package com.compomics.jwrapper.elude;

import com.compomics.jwrapper.elude.beans.PeptideOutputBean;
import com.compomics.jwrapper.elude.exception.EludeException;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class RetentionTimePredictionParser {

    public static Set<PeptideOutputBean> parse(File aFile) throws IOException, EludeException {
        BufferedReader lReader = Files.newReader(aFile, Charset.defaultCharset());
        HashSet<PeptideOutputBean> lOutputBeans = new HashSet<PeptideOutputBean>();
        String line = null;

        PeptideOutputBean lPeptideOutputBean = null;

        while ((line = lReader.readLine()) != null) {
            if(line.indexOf('#') == 0){
                // do nothing with the file header
                continue;
            }
            else if(line.indexOf("Peptide") == 0){
                // do nothing with the column header
                continue;
            }
            else if(line.equals("")){
                // do nothing with empty line
                continue;
            }
            else if(line.indexOf("Error") == 0){
                throw new EludeException(line);
            }
            else {
                // finish previous PeptideOutputBean?
                if (lPeptideOutputBean != null) {
                    persistPeptideOutputBean(lOutputBeans, lPeptideOutputBean);
                }

                // make a new PeptideOutputBean.
                lPeptideOutputBean = new PeptideOutputBean();

                String[] lElements = line.split("\t");

                lPeptideOutputBean.setPeptideSequence(lElements[0]);
                lPeptideOutputBean.setRetentionTime(Double.parseDouble(lElements[1]));
            }
        }

        // finished reading the file, fence post!
        persistPeptideOutputBean(lOutputBeans, lPeptideOutputBean);

        // return the results.
        return lOutputBeans;

    }

    /**
     * Add the PeptideOutputBean to the PeptideOutputBeans set.
     * @param aOutputBeans
     * @param aPeptideOutputBean
     */
    private static void persistPeptideOutputBean(HashSet<PeptideOutputBean> aOutputBeans, PeptideOutputBean aPeptideOutputBean) {
        // Add the previous PeptideOutputBean.
        aOutputBeans.add(aPeptideOutputBean);
    }
}
