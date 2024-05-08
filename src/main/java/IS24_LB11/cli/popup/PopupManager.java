package IS24_LB11.cli.popup;

import com.googlecode.lanterna.input.KeyStroke;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class PopupManager {
    private final HashMap<String, Popup> popups;
    private Popup focusedPopup;

    public PopupManager(Popup... popups) {
        this.popups = new HashMap<>();
        this.focusedPopup = null;
        for (Popup popup : popups) this.popups.put(popup.label(), popup);
    }

    public void addPopup(Popup... popups) {
        for (Popup popup : popups) {
            if (this.popups.containsKey(popup.label())) return;
            this.popups.put(popup.label(), popup);
        }
    }

    public void forEachPopup(Consumer<Popup> consumer) {
        for (Popup popup : popups.values()) consumer.accept(popup);
    }

    public void consumeKeyStroke(KeyStroke keyStroke) {
        getFocusedPopup().ifPresent(popup -> popup.consumeKeyStroke(keyStroke));
    }

    public void updatePopups() {
        popups.values().forEach(Popup::update);
    }

    public void resizePopups() {
        popups.values().forEach(popup -> popup.resize());
        popups.values().stream()
                .filter(this::inOverlapping)
                .forEach(this::hidePopup);
    }

    public void showPopup(String label) {
        getOptionalPopup(label).ifPresent(popup -> {
            if (!popup.isReadOnly()) {
                if (focusedPopup != null) focusedPopup.disable();
                focusedPopup = popup;
            }
            popup.show();
            if (popup.canOverlap()) return;
            popups.entrySet().stream()
                    .map(e -> e.getValue())
                    .filter(p -> p != popup && p.visible && !p.canOverlap() && popup.overlapping(p))
                    .forEach(this::hidePopup);
        });
    }

    public void hidePopup(String label) {
        getOptionalPopup(label).ifPresent(this::hidePopup);
    }

    private void hidePopup(Popup popup) {
        if (focusedPopup != null && focusedPopup.label().equals(popup.label())) hideFocusedPopup();
        else popup.hide();
    }

    public void hideAllPopups(){
        forEachPopup(Popup::hide);
    }

    public void hideFocusedPopup() {
        if (focusedPopup != null)
            focusedPopup.hide();
        focusedPopup = popups.values().stream()
                .filter(Popup::isVisible)
                .filter(popup -> !popup.isReadOnly())
                .findFirst().orElse(null);
        if (focusedPopup != null)
            focusedPopup.enable();
    }

    public Optional<Popup> getOptionalPopup(String label) {
        return Optional.ofNullable(popups.get(label.toLowerCase()));
    }

    public Popup getPopup(String label) {
        return popups.get(label.toLowerCase());
    }

    public Optional<Popup> getFocusedPopup() {
        return Optional.ofNullable(focusedPopup);
    }

    public boolean hasPopup(String label) {
        return popups.containsKey(label.toLowerCase());
    }

    private boolean inOverlapping(Popup popup) {
        return popups.values().stream()
                .filter(p -> p.visible && !(p.canOverlap() || p.label().equals(popup.label())))
                .anyMatch(popup::overlapping);
    }
}
