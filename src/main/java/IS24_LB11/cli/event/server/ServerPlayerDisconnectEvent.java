package IS24_LB11.cli.event.server;

public record ServerPlayerDisconnectEvent(String player) implements ServerEvent{

}
