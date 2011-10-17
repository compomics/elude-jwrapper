package com.compomics.jwrapper.elude.beans;

import java.util.Set;

public class PeptideOutputBean {
    private String iPeptideSequence;
    private double iRetentionTime;

    public PeptideOutputBean(String aPeptideSequence, double aRetentionTime) {
        iPeptideSequence = aPeptideSequence;
        iRetentionTime = aRetentionTime;
    }

    public PeptideOutputBean() {
    }

    public String getPeptideSequence() {
        return iPeptideSequence;
    }

    public void setPeptideSequence(String aPeptideSequence) {
        iPeptideSequence = aPeptideSequence;
    }

    public double getRetentionTime() {
        return iRetentionTime;
    }

    public void setRetentionTime(double aRetentionTime) {
        iRetentionTime = aRetentionTime;
    }
}
