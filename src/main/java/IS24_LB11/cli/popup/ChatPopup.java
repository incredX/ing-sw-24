package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.event.server.ServerMessageEvent;
import IS24_LB11.cli.view.popup.ChatView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.HashMap;
import java.util.function.Consumer;

public class ChatPopup extends Popup {
    private final HashMap<String, JsonArray> chats = new HashMap<>();
    private ClientState state;
    private String currentEndUser = "";
    private int firstLine = 0;
    private int numLines = 0;

    public ChatPopup(ViewHub viewhub, ClientState state) {
        super(viewhub, new ChatView(viewhub.getScreenSize()));
        this.state = state;
        chats.put("", new JsonArray());
    }

    public void newMessage(JsonObject message) {
        String endUser;
        if (message.get("from").getAsString().equals(state.getUsername())) {
            endUser = message.get("to").getAsString();
            message.addProperty("fromUser", true);
        }
        else if (message.get("to").getAsString().isEmpty()) endUser = "";
        else endUser = message.get("from").getAsString();

        if (!chats.containsKey(endUser)) {
            chats.put(endUser, new JsonArray());
        }
        chats.get(endUser).add(message);
        if (visible && endUser.equals(currentEndUser)) updateChat();
    }

    public void newMessage(ServerMessageEvent messageEvent) {
        JsonObject message = new JsonObject();
        message.addProperty("from", messageEvent.from());
        message.addProperty("to", messageEvent.to());
        message.addProperty("message", messageEvent.message());
        newMessage(message);
    }

    @Override
    public String label() { return "chat"; }

    @Override
    public void update() {
        castView(chatView -> {
            chatView.setOffset(firstLine);
            chatView.redraw();
        });
    }

    @Override
    public void show() {
        enable();
        castView(chatView -> {
            chatView.loadChat(chats.get(currentEndUser), currentEndUser);
            numLines = chatView.getChatLength();
            firstLine = numLines > ChatView.DEFAULT_HEIGHT ? numLines - ChatView.DEFAULT_HEIGHT : 0;
            chatView.setOffset(firstLine);
            chatView.redraw();
        });
        super.show();
    }

    @Override
    public void hide() {
        disable(); // an invisible popup is also disabled (invisible => disabled)
        super.hide();
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {
        if (!enabled) return;
        switch (keyStroke.getKeyType()) {
            case ArrowUp -> firstLine -= firstLine > 0 ? 1 : 0;
            case ArrowDown -> firstLine += firstLine < numLines - ChatView.DEFAULT_HEIGHT + 2 ? 1 : 0;
            case ArrowLeft, ArrowRight -> {}
            default -> { return; }
        }
        update();
        state.consumeKey();
    }

    public void updateChat() {
        castView(chatView -> {
            chatView.loadChat(chats.get(currentEndUser), currentEndUser);
            numLines = chatView.getChatLength();
            if (numLines >= ChatView.DEFAULT_HEIGHT-2) firstLine = numLines;
            chatView.setOffset(firstLine);
            chatView.redraw();
        });
    }

    public void setCurrentEndUser(String currentEndUser) {
        this.currentEndUser = currentEndUser;
        if (!chats.containsKey(currentEndUser)) chats.put(currentEndUser, new JsonArray());
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        this.state = (ClientState) playerState;
    }

    public String getCurrentEndUser() {
        return currentEndUser;
    }

    private void castView(Consumer<ChatView> consumer) {
        consumer.accept((ChatView) popView);
    }
}
