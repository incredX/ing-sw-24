package IS24_LB11.cli.event;

import IS24_LB11.game.Board;

//TODO: new event ServerGetPublicDataEvent
public class ServerUpdateEvent implements ServerEvent {
    private final String username;
    private final Board board;

    public ServerUpdateEvent(String username, Board board) {
        this.username = username;
        this.board = board;
    }

    public String getUsername() {
        return username;
    }

    public Board getBoard() {
        return board;
    }
}
