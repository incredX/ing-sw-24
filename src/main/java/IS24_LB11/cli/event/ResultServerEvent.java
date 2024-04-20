package IS24_LB11.cli.event;

import IS24_LB11.game.Result;

public record ResultServerEvent(Result<ServerEvent> result) implements Event {
}
