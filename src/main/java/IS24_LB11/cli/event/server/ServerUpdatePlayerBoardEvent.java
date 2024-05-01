package IS24_LB11.cli.event.server;

import IS24_LB11.game.Board;

public class ServerUpdatePlayerBoardEvent implements ServerEvent {
    private final String username;
    private final Board board;

    public ServerUpdatePlayerBoardEvent(String username, Board board) {
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
