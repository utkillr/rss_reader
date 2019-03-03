package util;

import java.io.PrintStream;

public class Log {
    private String context;
    final private PrintStream out;

    /**
     * Constructor with context of logging and output print stream
     *
     * @param context context which is [String] to append
     * @param out where to write
     */
    public Log(String context, PrintStream out) {
        this.context = context;
        this.out = out;
    }

    /**
     * [INFO] severity logging
     *
     * @param msg message
     */
    public void info(String msg) {
        log(msg, "INFO");
    }

    /**
     * [WARN] severity logging
     *
     * @param msg message
     */
    public void warn(String msg) {
        log(msg, "WARN");
    }

    /**
     * [ERROR] severity logging
     *
     * @param msg message
     */
    public void error(String msg) {
        log(msg, "ERROR");
    }

    /**
     * [RESPONSE] severity logging
     *
     * @param msg message
     */
    public void response(String msg) {
        log(msg, "RESPONSE");
    }

    /**
     * Just log empty line
     */
    public void emptyLine() {
        out.println();
    }

    /**
     * Logging with severity
     *
     * @param msg message
     * @param severity severity level of logging
     */
    private void log(String msg, String severity) {
        synchronized (out) {
            out.println(String.format("[%s]: [%s] %s", context, severity, msg));
        }
    }
}
