package com.azain.stranger.command.commands;

import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel()
                        .sendMessage("Calculating....").queue((message) -> {
                            message.editMessageFormat("**Rest ping:** `%sms`\n**WS ping:** `%sms`", ping, jda.getGatewayPing()).queue();
                        })
        );
    }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the discord servers";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
