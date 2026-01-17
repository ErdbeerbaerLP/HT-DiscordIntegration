package de.erdbeerbaerlp.htdcintegration.discordCommands;

import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import de.erdbeerbaerlp.htdcintegration.util.MessageUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;


public class CommandUptime extends DiscordCommand {
    public CommandUptime() {
        super("uptime", DiscordPlugin.getInstance().messages.get().cmdUptimeDescription);
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev, ReplyCallbackAction reply) {
        reply.setContent(DiscordPlugin.getInstance().messages.get().cmdUptimeMessage.replace("%uptime%", MessageUtil.getFullUptime())).setEphemeral(DiscordPlugin.getInstance().cmdconf.get().hideUptimeCommand).queue();
    }

}