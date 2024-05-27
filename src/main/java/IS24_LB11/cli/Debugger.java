package IS24_LB11.cli;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Debugger {
    public static final String DIR_NAME = "debugger-logs/";
    private static final String CLOSING_LINE = "****************************************************************";

    private boolean active;
    private PrintWriter out;

    private static Debugger instance = null;

    public static void startDebugger(PrintStream out) {
        if (instance != null) closeDebugger();
        instance = new Debugger(out);
        instance.active = true;
        instance.printIntro();
    }

    public static void startDebugger() {
        if (instance != null) { return; }
        startDebugger(System.out);
    }

    public static void startDebugger(String dirName) throws FileNotFoundException {
        if (instance != null) { return; }
        startDebugger(new PrintStream(new FileOutputStream(new File(getNextFileName(dirName)))));
    }

    public static void closeDebugger() {
        if (instance != null) {
            instance.active = false;
            instance.out.close();
        }
    }

    private Debugger(PrintStream out) {
        this.active = false;
        this.out = new PrintWriter(out);
    }

    public static void print(String msg) {
        if (instance == null) return;
        synchronized (instance) {
            instance.printMessage(msg);
        }
    }

    public static void print(Exception e) {
        if (instance == null) return;
        synchronized (instance) {
            instance.printException(e);
        }
    }


    private void printIntro() {
        if(!active) return;
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String jvVer = System.getProperty("java.version");
        String vmVer = System.getProperty("java.vm.specification.version");
        String userDir = System.getProperty("user.dir");
        out.println("**************************** DEBUG *****************************");
        out.println("OS: " + osName + " (" + osArch + ")");
        out.println("Java/vm version: " + jvVer + " / " + vmVer);
        out.println("directory: " + userDir);
        out.println(CLOSING_LINE);
        out.flush();
    }

    private void printMessage(String msg) {
        if(!active) return;
        //out.println("*********************** DEBUG - MESSAGE ************************");
        out.printf("in <%s> => %s\n", Thread.currentThread().getName(), msg);
        out.flush();
    }

    private void printException(Exception e) {
        if(!active) return;
        String message = e.getMessage();
        Throwable cause = e.getCause();
        out.println("******************* DEBUG - CAUGHT EXCEPTION *******************");
        out.printf("in <%s> :\n", Thread.currentThread().getName());
        out.println("CLASS: " + e.getClass().getName());
        if (message != null) out.println("MESSAGE: " + message);
        if (cause != null) out.println("CAUSE: " + cause.getMessage());
        out.println(CLOSING_LINE);
    }

    private static String getNextFileName(String dirName) throws FileNotFoundException, IllegalArgumentException {
        File dir = new File(dirName);
        if (!dir.exists()) { throw new FileNotFoundException(dirName); }
        if (!dir.isDirectory()) { throw new IllegalArgumentException("expected directory"); }

        ArrayList<File> files = new ArrayList<>(Arrays.stream(dir.listFiles()).toList());
        if (files == null) { throw new IllegalArgumentException(); }

        ArrayList<Integer> values = new ArrayList<>(files.stream()
                .filter(file -> file.getName().endsWith(".txt"))
                .map(file -> file.getName().replace(".txt", ""))
                .map(name -> {
                    //client_log_000.txt
                    try { return Integer.parseInt(name.substring(name.length() - 3)); }
                    catch (NumberFormatException e) { return 0; }
                }).toList());
        int index = values.stream().max((a, b) -> Integer.compare(a, b)).orElse(0)+1;
        return String.format("%sclient_log_%03d.txt", DIR_NAME, index);
    }
}
