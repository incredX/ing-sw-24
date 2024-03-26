package IS24_LB11.cli;

import IS24_LB11.cli.event.CommandEvent;
import IS24_LB11.cli.event.Event;
import IS24_LB11.cli.event.KeyboardEvent;
import IS24_LB11.cli.event.ResizeEvent;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class Controller {
    private static final int QUEUE_CAPACITY = 64;
    private static final String LOREM = "Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit.\nNunc ut fringilla lorem.";

    private final ArrayBlockingQueue<Event> queue;
    private final ViewHub hub;
    private final CommandLine cmdLine;
    private boolean running;
    private boolean popUpOn;

    private Controller() throws IOException {
        queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        hub = new ViewHub();
        cmdLine = new CommandLine(hub.getTerminal().getTerminalSize().getColumns());
        running = true;
        popUpOn = false;
    }

    public static void main(String[] args) {
        Debugger dbg = new Debugger();
        Controller ctrl;
        InputListener inListener;
        ResizeListener reListener;
        HashMap<String, Thread> threadMap = new HashMap<>();

        try {
            ctrl = new Controller();
            inListener = new InputListener(ctrl.hub.getTerminal(), ctrl.queue);
            reListener = new ResizeListener(ctrl.hub.getTerminal(), ctrl.queue);
        } catch (IOException e) {
            dbg.printException(e);
            return;
        }

        dbg.printIntro("init DONE.");

        threadMap.put("input", new Thread(inListener));
        threadMap.put("resize", new Thread(reListener));
        threadMap.put("views", new Thread(ctrl.hub));

        for (Thread t: threadMap.values()) t.start();

        ctrl.start();

        dbg.printMessage("closing controller.");

        try { System.in.close(); }
        catch (IOException e) { dbg.printException(e); }
        threadMap.get("resize").interrupt();
        threadMap.get("views").interrupt();
    }

    private void start() {
        hub.addPopUp(LOREM, " 1/1 : LOREM ");
        popUpOn = true;

        while (running) {
            Event event;
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try { queue.wait(); }
                    catch (InterruptedException e) { break; }
                }
                event = queue.remove();
            }
            eventSorting(event);
        }
    }

    private void eventSorting(Event event) {
        switch (event) {
            case KeyboardEvent kb -> processKeyStroke(kb.getKeyStroke());
            case CommandEvent cmd -> processCommand(cmd.command());
            case ResizeEvent re -> processResize(re.size());
            default -> System.out.println("Unknown event");
        }
    }

    private void processCommand(String command) {
        if (command.equalsIgnoreCase("quit")) running = false;
        System.out.println("command: \"" + command + "\"");
        String[] tokens = command.split(" ", 3);
        if (tokens[0].equalsIgnoreCase("popup")) {
            hub.addPopUp(tokens[2], tokens[1]);
            popUpOn = true;
        }
    }

    private void processResize(TerminalSize size) {
        //System.out.printf("size : %d x %d\n", size.getColumns(), size.getRows());
        cmdLine.setWidth(size.getColumns());
        hub.resize(size, cmdLine);
    }

    private void processKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case Character:
                cmdLine.insertChar(keyStroke.getCharacter());
                break;
            case Backspace:
                cmdLine.deleteChar();
                break;
            case Enter:
                boolean offerAccepted;
                synchronized (queue) {
                    offerAccepted = queue.offer(new CommandEvent(cmdLine.getFullLine()));
                    if (offerAccepted) queue.notify();
                }
                cmdLine.clearLine();
                break;
            case ArrowLeft:
                cmdLine.moveCursor(-1);
                break;
            case ArrowRight:
                cmdLine.moveCursor(1);
                break;
            case Escape:
                if (popUpOn) {
                    popUpOn = false;
                    hub.removePopUp();
                    hub.update();
                } else {
                    running = false;
                }
            default:
                break;
        }
        hub.updateCommandLine(cmdLine);
    }
}
