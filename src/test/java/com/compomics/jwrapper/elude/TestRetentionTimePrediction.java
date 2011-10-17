package com.compomics.jwrapper.elude;

import com.compomics.jwrapper.elude.beans.PeptideInputBean;
import com.compomics.jwrapper.elude.beans.PeptideOutputBean;
import com.compomics.jwrapper.elude.exception.EludeException;
import com.compomics.jwrapper.elude.playground.RetentionTimePredictor;
import com.google.common.io.Files;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import javax.management.monitor.StringMonitor;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This class is a
 */

public class TestRetentionTimePrediction extends TestCase {

    private File iOutputFile = null;

    /**
     * Create TestRetentionTimePrediction
     *
     * @param testName name of the test case
     */
    public TestRetentionTimePrediction(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TestRetentionTimePrediction.class);
    }

    /**
     * predictRetentionTimes test.
     */
    @org.junit.Test
    public void testPredictRetentionTimes() {
        HashSet<PeptideInputBean> lPeptideInputBeans = new HashSet<PeptideInputBean>();
        lPeptideInputBeans.add(new PeptideInputBean("AAMDNSEIAGEK"));
        lPeptideInputBeans.add(new PeptideInputBean("SIQEELQQLR"));

        Set<PeptideOutputBean> lPeptideOutputBeans = null;
        try {
            lPeptideOutputBeans = RetentionTimePredictionTask.predictRetentionTimes(lPeptideInputBeans);
        } catch (EludeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (PeptideOutputBean lOutputBean : lPeptideOutputBeans ){
            // simple testing!
            if(lOutputBean.getPeptideSequence().equals("AAMDNSEIAGEK")){
                Assert.assertEquals(lOutputBean.getRetentionTime(), 16.156);
            }

            if(lOutputBean.getPeptideSequence().equals("SIQEELQQLR")){
                Assert.assertEquals(lOutputBean.getRetentionTime(), 25.0558);
            }
        }
    }

    /**
     * RetentionTimePredictor test.
     */
    @org.junit.Test
    public void testRetentionTimePredictor() {
        Map<String, Double> lPeptideMap = new HashMap<String, Double>();
        lPeptideMap.put("AAMDNSEIAGEK", 16.156);
        lPeptideMap.put("SIQEELQQLR", 25.0558);
        lPeptideMap.put("DDDIAALVVDNGSGMCK", 36.5357);
        lPeptideMap.put("EHASGSGAQSEAAGR", 4.94369);
        lPeptideMap.put("IGTFDLK", 30.8126);
        lPeptideMap.put("LAETVFNFQEK", 29.533);

        File lInputFile = null;
        try {
            lInputFile = new File(this.getClass().getClassLoader().getResource("input_win.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        RetentionTimePredictor.doRetentionTimePredictor(new String[]{"-input", lInputFile.getAbsolutePath(), "-output", iOutputFile.getAbsolutePath()});

        try {
            FileReader lFileReader = new FileReader(iOutputFile);
            BufferedReader lBufferedReader = new BufferedReader(lFileReader);

            String line = null;
            String[] lElements = null;
            String lSequence = null;
            double lRetentionTime = 0;
            while ((line = lBufferedReader.readLine()) != null){
                lElements = line.split("\t");
                Assert.assertEquals(lPeptideMap.get(lElements[0]), Double.valueOf(lElements[1]), 0.1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        iOutputFile = new File("src/test/resources/output.txt");
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if(iOutputFile.exists()){
            iOutputFile.delete();
        }
    }


}
