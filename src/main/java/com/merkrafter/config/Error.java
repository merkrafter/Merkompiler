package com.merkrafter.config;

/****
 * This class serves as a container to hold an ErrorCode together
 * with a more detailed error message.
 *
 * @author merkrafter
 ***************************************************************/

public class Error {
    // ATTRIBUTES
    //==============================================================
    private final ErrorCode errorCode;
    private final String errorMessage;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Default constructor
     ***************************************************************/
    public Error(final ErrorCode errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // GETTER
    //==============================================================
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("Error %d: %s", errorCode.id, errorMessage);
    }
}
