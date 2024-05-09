package IS24_LB11.cli.view.popup;

import IS24_LB11.cli.utils.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.List;

public class ChatView extends PopupView {
    private static final int DEFAULT_WIDTH = 48;
    public static final int DEFAULT_HEIGHT = 32;

    private final ArrayList<String> lines = new ArrayList<>();
    private String title = "";
    private int offset = 0;

    public ChatView(TerminalSize parentSize) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawTitle();
        drawChat();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void loadChat(JsonArray array, String endUser) {
        if (endUser.isEmpty()) title = String.format("chat : general", endUser);
        else title = String.format("chat : %s", endUser);
        lines.clear();
        for (int i = 0; i < array.size(); i++) {
            JsonObject message = array.get(i).getAsJsonObject();
            String head = message.get("from").getAsString();
            List<String> body = getMessageBodyAsLines(message.get("message").getAsString());
            if (message.has("fromUser")) {
                head = head + " <";
                head = " ".repeat(innerWidth() - head.length()) + head;
                body = body.stream().map(l -> " ".repeat(innerWidth() - l.length()-2)+l+"  ").toList();
            } else {
                head = " > " + head;
                body = body.stream().map(l -> "   "+l).toList();
            }
            lines.add(head);
            lines.addAll(body);
            lines.add(" ");
        }
        //offset = lines.size() > DEFAULT_HEIGHT ? lines.size()-DEFAULT_HEIGHT : 0;
    }

    private List<String> getMessageBodyAsLines(String message) {
        ArrayList<String> lines = new ArrayList<>();
        while (message.length() > 0) {
            int endIndex = Integer.min(innerWidth()-8, message.length());
            lines.add(message.substring(0, endIndex));
            message = message.substring(endIndex);
        }
        return lines;
    }

    private void drawChat() {
        int i = firstRow();
        for (String line : lines.subList(offset, lines.size())) {
            if (i > lastRow()) break;
            fillRow(i, line);
            i++;
        }
    }

    private void drawTitle() {
        fillRow(borderArea.side(Side.NORD), firstColumn()+1, String.format("[%s]", title));
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getChatLength() {
        return lines.size();
    }
}
