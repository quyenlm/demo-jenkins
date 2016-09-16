package phn.nts.ams.fe.domain;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/6/13 1:46 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class InvalidActionException extends Exception {
    private String messageCode;
    private String[] args = new String[]{};

    public static final String MSG_SC_019 = "MSG_SC_019";
    public static final String MSG_SC_022 = "MSG_SC_022";
    public static final String MSG_SC_023 = "MSG_SC_023";
    public static final String MSG_SC_025 = "MSG_SC_025";
    public static final String MSG_SC_062 = "MSG_SC_062";
    public static final String MSG_SC_067 = "MSG_SC_067";
    public static final String MSG_SC_068 = "MSG_SC_068";
    public static final String MSG_SC_069 = "MSG_SC_069";
    public static final String MSG_SC_070 = "MSG_SC_070";
    public static final String MSG_SC_046 = "MSG_SC_046";

    public InvalidActionException(String messageCode) {
        this.messageCode = messageCode;
    }

    public InvalidActionException(String message, String messageCode) {
        super(message);
        this.messageCode = messageCode;
    }

    public InvalidActionException(String message, Throwable cause, String messageCode) {
        super(message, cause);
        this.messageCode = messageCode;
    }

    public InvalidActionException(Throwable cause, String messageCode, String[] args) {
        super(cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    public InvalidActionException(String message, Throwable cause, String messageCode, String[] args) {
        super(message, cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    public InvalidActionException(String message, String messageCode, String[] args) {
        super(message);
        this.messageCode = messageCode;
        this.args = args;
    }

    public InvalidActionException(String messageCode, String[] args) {
        this.messageCode = messageCode;
        this.args = args;
    }

    public InvalidActionException(Throwable cause, String messageCode) {
        super(cause);
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
