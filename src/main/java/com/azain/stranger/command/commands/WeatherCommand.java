package com.azain.stranger.command.commands;

import com.azain.stranger.Config;
import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import com.azain.stranger.utils.WeatherRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx)
    {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getChannel();
        final User user = ctx.getAuthor();

        if (args.isEmpty()) {
            channel.sendMessage("Usage : =weather <city name>").queue();
            return;
        }

        final String location = args.get(0);

        try
        {
            WeatherRequest weatherRequest = new WeatherRequest(location);
            JSONObject data = new JSONObject(weatherRequest.getResponse().body());
            JSONObject locationData = data.getJSONObject("location");
            JSONObject currentObservationData = data.getJSONObject("current_observation");
            JSONObject windData = currentObservationData.getJSONObject("wind");
            JSONObject atmosphereData = currentObservationData.getJSONObject("atmosphere");
            JSONObject astronomyData = currentObservationData.getJSONObject("astronomy");
            JSONObject conditionData = currentObservationData.getJSONObject("condition");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone(locationData.getString("timezone_id")));

            String iconURL = Config.get("ICON_URL").replace("{code}", String.valueOf(conditionData.get("code")));
            EmbedBuilder weatherEmbed = new EmbedBuilder();
            weatherEmbed.setThumbnail(iconURL);
            weatherEmbed.setTitle((int) conditionData.get("temperature") + "℃ " + conditionData.getString("text"));
            weatherEmbed.addField("Humidity", (int) atmosphereData.get("humidity") + "%", true);
            weatherEmbed.addField("Wind Speed", (Double) windData.get("speed") + "km/h", true);
            weatherEmbed.addField("Visibility", (Double) windData.get("speed") + "km", true);
            weatherEmbed.addField("Sunrise", astronomyData.getString("sunrise"), true);
            weatherEmbed.addField("Sunset", astronomyData.getString("sunset"), true);
            weatherEmbed.addField("Time", dateFormat.format(new Date()), true);
            weatherEmbed.setFooter("Current Weather for " + locationData.getString("city") + ", " + locationData.getString("country"));
            weatherEmbed.setColor(0x5865F2);
            channel.sendMessage(weatherEmbed.build()).queue();

        }

        catch(Exception e)
        {
            channel.sendMessage("Oops! something went wrong!").queue();
        }
    }

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getHelp() {
        return "Get weather details of a city!" +
                "\nUsage : =weather <city name>";
    }
}
