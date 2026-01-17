package de.erdbeerbaerlp.htdcintegration.storage;


import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import de.erdbeerbaerlp.htdcintegration.discordCommands.CommandLink;
import de.erdbeerbaerlp.htdcintegration.discordCommands.CommandUptime;
import de.erdbeerbaerlp.htdcintegration.discordCommands.DiscordCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandRegistry {
    /**
     * Commands registered to Discord
     */
    public static final HashMap<String, DiscordCommand> registeredCMDs = new HashMap<>();
    /**
     * Registered commands
     */
    private static List<DiscordCommand> commands = new ArrayList<>();

    /**
     * Registers all default commands and custom commands from config
     */
    public static void registerDefaultCommands() {
        if (DiscordPlugin.getInstance().cmdconf.get().enableUptime)
            registerCommand(new CommandUptime());
        if (DiscordPlugin.getInstance().linkconf.get().enableLinking)
            registerCommand(new CommandLink());
    }

    /**
     * Registers all commands to discord if changed
     */
    public static void updateSlashCommands() throws IllegalStateException {
        final GuildMessageChannel channel = DiscordPlugin.getInstance().discord.getChannel();
        if (channel == null)
            throw new IllegalStateException("Channel does not exist, check channel ID and bot permissions on both channel and category. Also make sure to enable all intents for the bot on https://discord.com/developers/applications/" + DiscordPlugin.getInstance().discord.getJda().getSelfUser().getApplicationId() + "/bot");
        final List<Command> globalCmds = DiscordPlugin.getInstance().discord.getJda().retrieveCommands().complete();

        boolean regenGlobal = false;

        if (commands.size() == globalCmds.size()) {
            for (final DiscordCommand cmd : commands) {
                Command cm = null;
                for (final Command c : globalCmds) {
                    if (((CommandData) cmd).getName().equals(c.getName())) {
                        cm = c;
                        break;
                    }
                }
                if (cm == null) {
                    regenGlobal = true;
                    break;
                }
                if (!optionsEqual(cmd.getOptions(), cm.getOptions())) {
                    regenGlobal = true;
                    break;
                }
            }
        } else regenGlobal = true;

        if (regenGlobal) {
            DiscordPlugin.getInstance().getLogger().atInfo().log("Regenerating global commands...");
            CommandListUpdateAction commandListUpdateAction = DiscordPlugin.getInstance().discord.getJda().updateCommands();

            for (DiscordCommand cmd : commands) {
                commandListUpdateAction = commandListUpdateAction.addCommands(cmd);
            }
            final CompletableFuture<List<Command>> submit = commandListUpdateAction.submit();
            submit.thenAccept(CommandRegistry::addCmds);
        } else {
            DiscordPlugin.getInstance().getLogger().atInfo().log("No need to regenerate global commands");
            addCmds(globalCmds);
        }
    }

    @SuppressWarnings({"LoopStatementThatDoesntLoop", "UnusedAssignment"})
    private static boolean optionsEqual(List<OptionData> data, List<Command.Option> options) {
        if (data.size() != options.size()) return false;
        for (int i = 0; i < data.size(); i++) {
            final OptionData optionData = data.get(i);
            final Command.Option option = options.get(i);
            return option.getName().equals(optionData.getName()) && option.getChoices().equals(optionData.getChoices()) && option.getDescription().equals(optionData.getDescription()) && option.isRequired() == optionData.isRequired() && option.getType().equals(optionData.getType());
        }
        return true;
    }


    /**
     * Registers an {@link DiscordCommand}<br>
     * This has to be done before the server is fully started!
     *
     * @param cmd command
     * @return true if the registration was successful
     */
    public static boolean registerCommand(DiscordCommand cmd) {
        if (DiscordPlugin.getInstance().started != -1) {
            DiscordPlugin.getInstance().getLogger().atInfo().log("Attempted to register command {} after server finished loading", cmd.getName());
            return false;
        }

        final ArrayList<DiscordCommand> toRemove = new ArrayList<>();
        for (final DiscordCommand c : commands) {
            if (!cmd.isConfigCommand() && cmd.equals(c)) return false;
            else if (cmd.isConfigCommand() && cmd.equals(c)) toRemove.add(c);
        }
        for (final DiscordCommand cm : toRemove)
            commands.remove(cm);
        commands.add(cmd);
        /*if (cmd instanceof CommandFromConfig) {
            if (cmd.isUsingArgs()) cmd.addOption(OptionType.STRING, "args", cmd.getArgText());
        }*/
        return true;

    }

    private static void addCmds(List<Command> cmds) {
        for (final Command cmd : cmds) {
            for (final DiscordCommand cfcmd : commands) {
                if (cmd.getName().equals(((CommandData) cfcmd).getName())) {
                    registeredCMDs.put(cmd.getId(), cfcmd);
                    DiscordPlugin.getInstance().getLogger().atInfo().log("Added command {} with ID {}", cmd.getName(), cmd.getIdLong());
                }
            }
        }
    }


    private static ArrayList<Role> getAdminRoles(Guild g) {
        final List<Role> gRoles = g.getRoles();
        final ArrayList<Role> adminRoles = new ArrayList<>();

        for (final Role r : gRoles) {
            if (/*ArrayUtils.contains(Configuration.instance().commands.adminRoleIDs, r.getId())*/false)
                adminRoles.add(r);
        }

        return adminRoles;
    }

    /**
     * Attempts to reload all commands
     */
    public static void reRegisterAllCommands() {
        final List<DiscordCommand> cmds = commands;
        DiscordPlugin.getInstance().getLogger().atInfo().log("Reloading {} commands", cmds.size());
        commands = new ArrayList<>();

        for (final DiscordCommand cmd : cmds) {
            if (cmd.isConfigCommand()) continue;
            commands.add(cmd);
        }

        DiscordPlugin.getInstance().getLogger().atInfo().log("Registered {} commands", commands.size());
    }

    /**
     * @return A list of all registered commands
     */

    public static List<DiscordCommand> getCommandList() {
        return commands;
    }
}