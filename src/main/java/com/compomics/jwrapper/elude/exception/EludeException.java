package com.compomics.jwrapper.elude.exception;

/**
 * Created by IntelliJ IDEA.
 * User: niels
 * Date: 14/10/11
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class EludeException extends Exception {

    private String iMessage;

    public EludeException(String aMessage){
        iMessage = aMessage;
    }

    @Override
    public String getMessage() {
        return iMessage;
    }
}
