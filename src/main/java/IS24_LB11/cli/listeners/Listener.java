package IS24_LB11.cli.listeners;

import IS24_LB11.cli.controller.ClientState;

public abstract class Listener {
    protected final ClientState state;

    public Listener(ClientState state) {
        this.state = state;
    }
}
