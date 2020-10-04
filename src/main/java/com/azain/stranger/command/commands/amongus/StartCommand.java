package com.azain.stranger.command.commands.amongus;

import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class StartCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws IOException, InterruptedException {
        final TextChannel channel = ctx.getChannel();
        final Member member = ctx.getMember();
        final List<String> args = ctx.getArgs();

        if (args.isEmpty())
        {
            channel.sendMessage("Please provide a valid invite code!\n *Example Invite Code: AFSWRD*").queue();
            return;
        }

        String inviteCode = args.get(0);


        if(!(inviteCode.length() == 6))
        {
            channel.sendMessage("Please provide a valid invite code!\n *Example Invite Code: AFSWRD*").queue();
            return;
        }

        ctx.getMessage().delete().queue();
        final String imageUrl = "http://www.innersloth.com/Images/GAMES/AmongUs/banner_AmongUs.jpg";
        final String thumbnailUrl = "http://www.innersloth.com/Images/GAMES/AmongUs/bannerLogo_AmongUs.png";

        EmbedBuilder inviteEmbed = new EmbedBuilder();
        inviteEmbed.setImage(imageUrl);
        inviteEmbed.setThumbnail(thumbnailUrl);
        inviteEmbed.setTitle("Among Us");
        inviteEmbed.setDescription("Hey! Everyone **" + member.getUser().getAsTag() + "** is hosting a game in Among Us");
        inviteEmbed.addField("Host" , "" + member.getAsMention() + "", true);
        inviteEmbed.addField("Invite Code", "`" + args.get(0) + "`", true);
        inviteEmbed.setColor(Color.ORANGE);
        inviteEmbed.setTimestamp(Instant.now());
        inviteEmbed.setFooter(member.getUser().getAsTag(), member.getUser().getEffectiveAvatarUrl());


        channel.sendMessage(inviteEmbed.build()).queue((message) -> {
            message.addReaction("\uD83D\uDD08").queue();
            message.addReaction("\uD83D\uDD07").queue();
        });

    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getHelp() {
        return "Invite friends on server to play **Among Us**";
    }
}
