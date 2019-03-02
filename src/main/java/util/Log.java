package util;

import java.io.PrintStream;

public class Log {
    private String context;
    final private PrintStream out;

    public Log(String context, PrintStream out) {
        this.context = context;
        this.out = out;
    }

    public void info(String msg) {
        log(msg, "INFO");
    }

    public void warn(String msg) {
        log(msg, "WARN");
    }

    public void error(String msg) {
        log(msg, "ERROR");
    }

    public void response(String msg) {
        log(msg, "RESPONSE");
    }

    public void emptyLine() {
        out.println();
    }

    private void log(String msg, String severity) {
        synchronized (out) {
            out.println(String.format("[%s]: [%s] %s", context, severity, msg));
        }
    }
}
