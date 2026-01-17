package de.erdbeerbaerlp.htdcintegration.hytaleCommands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;

import java.util.ArrayList;
import java.util.List;

public class HTDiscordSubCommandRegistry {

    /**
     * Registered commands
     */
    private static final List<AbstractCommand> commands = new ArrayList<>();

    /**
     * Registers an {@link AbstractCommand}<br>
     * This has to be done before commands get registered serverside!
     *
     * @param cmd command
     * @return true if the registration was successful
     */
    public static boolean registerCommand(AbstractCommand cmd) {
        if (DiscordPlugin.getInstance().started != -1) {
            DiscordPlugin.getInstance().getLogger().atSevere().log("Attempted to register mc command " + cmd.getName() + "after server finished loading");
            return false;
        }
        commands.add(cmd);
        return true;
    }

    public static List<AbstractCommand> getCommands() {
        return commands;
    }
    public static void registerDefaultCommands(){
        registerCommand(new LinkCommand());
    }

}