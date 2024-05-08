package IS24_LB11.cli.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpView extends PopupView {
    public static final int DEFAULT_WIDTH = 64;
    public static final int DEFAULT_HEIGHT = 32;
    private static final String DASHED_LINE = "-".repeat(DEFAULT_WIDTH-2);
    private static final String SINGLE_LINE = "─".repeat(DEFAULT_WIDTH-2);

    private final ArrayList<String> lines;
    private int offset = 0;

    public HelpView(TerminalSize parentSize) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        setMargins(0);
        lines = new ArrayList<>();
        offset = 0;
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawTextLines();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void loadCommands(JsonArray commandList) {
        for (JsonElement command : commandList) {
            lines.addAll(getAsLines(command.getAsJsonObject()));
            lines.add(SINGLE_LINE);
        }
    }

    private void drawTextLines() {
        int i = firstRow();
        for (String line : lines.subList(offset, lines.size())) {
            if (i > lastRow()) break;
            fillRow(i, line);
            i++;
        }
    }

    private ArrayList<String> getAsLines(JsonObject commandObject) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(getLineFromObjectOrElse(commandObject, "command", ""));
        lines.add(DASHED_LINE);
        if (commandObject.has("argument(s)")) {
            for (JsonElement argument : commandObject.get("argument(s)").getAsJsonArray()) {
                JsonArray argumentArray = argument.getAsJsonArray();
                String argName = String.format("<%s>", getLineFromArray(argumentArray, 0));
                String[] descriptionLines = getLineFromArray(argumentArray, 1).split("\n");
                lines.add(String.format(" %s : %s", argName, descriptionLines[0]));
                for (String line: Arrays.stream(descriptionLines).skip(1).toArray(String[]::new))
                    lines.add(String.format("  %s", line));
                lines.set(0, lines.getFirst()+" "+argName);
            }
            lines.add(" ");
        }

        boolean hasLocation = commandObject.has("location");
        boolean hasKeyboardShortcut = commandObject.has("keyboard-shortcut");
        boolean hasCommandLineShortcut = commandObject.has("commandline-shortcut");

        if (hasLocation) {
            String location = String.format("(%s)", commandObject.get("location").getAsString());
            String spaces = " ".repeat(DEFAULT_WIDTH-4-lines.getFirst().length()-location.length());
            lines.set(0, lines.getFirst()+spaces+location);
        }
        if (hasKeyboardShortcut)
            lines.add(" keyboard-shortcut : " + commandObject.get("keyboard-shortcut").getAsString());
        if (hasCommandLineShortcut)
            lines.add(" commandline-shortcut : " + commandObject.get("commandline-shortcut").getAsString());
        if (hasKeyboardShortcut || hasCommandLineShortcut)
            lines.add(" ");

        lines.addAll(getLineFromObjectOrElse(commandObject, "description", " - ")
                .lines().map(s -> " "+s).toList());
        lines.add(" ");
        return lines;
    }

    private String getLineFromObject(JsonObject object, String key) {
        return object.get(key).getAsString();
    }

    private String getLineFromObjectOrElse(JsonObject object, String key, String defaultValue) {
        if (object.has(key)) return object.get(key).getAsString();
        return defaultValue;
    }

    private String getLineFromArray(JsonArray array, int index) {
        if (array.size() > index) return array.get(index).getAsString();
        return "";
    }

    public int getNumLines() {
        return lines.size();
    }
}
