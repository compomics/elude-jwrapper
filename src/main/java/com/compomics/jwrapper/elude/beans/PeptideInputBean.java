package com.compomics.jwrapper.elude.beans;

public class PeptideInputBean {
    private String iPeptideSequence;

    public PeptideInputBean() {
    }

    public PeptideInputBean(String aPeptideSequence) {
        iPeptideSequence = aPeptideSequence;
    }

    public String getPeptideSequence() {
        return iPeptideSequence;
    }

    public void setPeptideSequence(String aPeptideSequence) {
        iPeptideSequence = aPeptideSequence;
    }

    public String getEludeInputNotation() {
        return iPeptideSequence;
    }
}
