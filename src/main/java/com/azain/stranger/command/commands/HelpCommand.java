package com.azain.stranger.command.commands;

import com.azain.stranger.CommandManager;
import com.azain.stranger.Config;
import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            manager.getCommands().stream().map(ICommand::getName).forEach(
                    (it) -> builder.append('`').append(Config.get("prefix")).append(it).append("`\t")
            );

            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("🕵 **Stranger Command List**");
            info.setDescription(builder.toString());
            info.setColor(0xf45642);
            info.setFooter("Created by AzaiN", ctx.getMember().getUser().getAvatarUrl());

            channel.sendMessage(info.build()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            EmbedBuilder helpEmbed = new EmbedBuilder();
            helpEmbed.setTitle(search + " info");
            helpEmbed.addField("Description:" , "nothing found for " + search, false);
            helpEmbed.setColor(0x07B6E9);
            channel.sendMessage(helpEmbed.build()).queue();
            return;
        }

        EmbedBuilder helpEmbed = new EmbedBuilder();
        helpEmbed.setTitle(command.getName() + " info");
        helpEmbed.addField("Description:" , command.getHelp(), false);
        helpEmbed.addField("Usage: ", Config.get("prefix") + command.getName(), false);
        helpEmbed.setColor(0x07B6E9);
        channel.sendMessage(helpEmbed.build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list with commands in the bot\n" +
                "Usage: `=help [command]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}
