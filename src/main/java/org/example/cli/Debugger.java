package org.example.cli;

public class Debugger {
    private boolean active;
    private int checkPoint;

    public Debugger() {
        active = true;
        checkPoint = 0;
    }

    public void printIntro() {
        printIntro("");
    }

    public void printIntro(String msg) {
        if(!active) return;
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String jvVer = System.getProperty("java.version");
        String vmVer = System.getProperty("java.vm.specification.version");
        String userDir = System.getProperty("user.dir");
        System.out.println("**************************** DEBUG *****************************");
        System.out.println("OS: " + osName + " (" + osArch + ")");
        System.out.println("Java/vm version: " + jvVer + " / " + vmVer);
        System.out.println("directory: " + userDir);
        if (!msg.isEmpty()) System.out.println(msg);
        System.out.println("****************************************************************");
    }

    public void printMessage(String msg) {
        if(!active) return;
        System.out.println("*********************** DEBUG - MESSAGE ************************");
        System.out.println(msg);
        System.out.println("****************************************************************");
    }

    public void printException(Exception e) {
        if(!active) return;
        Throwable cause = e.getCause();
        System.err.println("******************* DEBUG - CAUGHT EXCEPTION *******************");
        System.err.println("MESSAGE: " + e.getMessage());
        if (cause != null) System.err.println("CAUSE: " + cause.getMessage());
        System.err.println("****************************************************************");
    }

    public void turnOn() { active = true; }
    public void turnOff() { active = false; }
    public void reset() { checkPoint = 0; }
    public boolean isOn() { return active; }
}
