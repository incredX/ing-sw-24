package org.example.cli.event;

public class CommandEvent implements Event {
    private final String command;

    public CommandEvent(String command) {
        this.command = command;
    }

    public String getCommand() { return command; }
}
