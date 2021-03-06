package com.azain.stranger.command.commands;

import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;


public class UserInfoCommand implements ICommand
{
    static DateTimeFormatter format = DateTimeFormatter.ofPattern("E, MMM d, u H:m a");

    @Override
    public void handle(CommandContext ctx)
    {
        final User user;
        final Member member;
        final TextChannel channel = ctx.getChannel();

        if(ctx.getMessage().getMentionedUsers().isEmpty() && ctx.getMessage().getMentionedMembers().isEmpty())
        {
            user = ctx.getAuthor();
            member = ctx.getMember();
        }

        else
        {
            user = ctx.getMessage().getMentionedUsers().get(0);
            member = ctx.getMessage().getMentionedMembers().get(0);
        }

        EmbedBuilder userinfo = new EmbedBuilder();
        userinfo.setTitle(user.getAsTag(), user.getAvatarUrl());
        userinfo.addField("**Joined Discord at**", user.getTimeCreated().format(format) , true);
        userinfo.addField("**Joined Server at**", member.getTimeJoined().format(format), true);
        userinfo.addField("**Online Status**", member.getOnlineStatus().toString().toLowerCase() , true);
        userinfo.addField("**Boosting Since**", member.getTimeBoosted() != null? member.getTimeBoosted().format(format) : "Not boosting", true);
        userinfo.setThumbnail(user.getAvatarUrl());
        userinfo.setColor(0xf45642);
        userinfo.setFooter("Requested by " + ctx.getMessage().getAuthor().getAsTag() , ctx.getMember().getUser().getAvatarUrl());
        channel.sendMessage(userinfo.build()).queue();
    }

    @Override
    public String getName() {
        return "user-info";
    }

    @Override
    public String getHelp() {
        return "Shows information about the User\n" +
                "Usage: `=user-info <@User>`";
    }
}
