package com.azain.stranger;

import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import com.azain.stranger.command.commands.*;
import com.azain.stranger.command.commands.amongus.StartCommand;
import com.azain.stranger.command.commands.admin.SetPrefixCommand;
import com.azain.stranger.command.commands.music.JoinCommand;
import com.azain.stranger.command.commands.music.LeaveCommand;
import com.azain.stranger.command.commands.music.PlayCommand;
import com.azain.stranger.command.commands.music.StopCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new PingCommand());
        addCommand(new HelpCommand(this));
        addCommand(new PasteCommand());
        addCommand(new HasteCommand());
        addCommand(new KickCommand());
        addCommand(new MemeCommand());
        addCommand(new WebhookCommand());
        addCommand(new UserInfoCommand());
        addCommand(new InstagramCommand());
        addCommand(new MinecraftCommand());
        addCommand(new WeatherCommand());
        addCommand(new SetPrefixCommand());
        addCommand(new JokeCommand());
        addCommand(new LeaveCommand());
        addCommand(new StartCommand());
        addCommand(new JoinCommand());
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }

        return null;
    }

    void handle(GuildMessageReceivedEvent event) throws IOException, InterruptedException{
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }

}
