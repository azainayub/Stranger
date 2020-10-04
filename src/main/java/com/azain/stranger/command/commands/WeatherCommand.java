package com.azain.stranger.command.commands;

import com.azain.stranger.command.CommandContext;
import com.azain.stranger.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class WeatherCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws IOException, InterruptedException
    {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getChannel();
        final User user = ctx.getAuthor();

        if (args.isEmpty()) {
            channel.sendMessage("You must provide a city name to get weather details").queue();
            return;
        }

        final String location = args.get(0);
        final String appId = "QSyGlTLz";
        final String consumerKey = "dj0yJmk9WEFUREpIcnBXWk10JmQ9WVdrOVVWTjVSMnhVVEhvbWNHbzlNQT09JnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWEz";
        final String consumerSecret = "66635b08751c3b7d13c5708227d7edf65781180e";
        final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        String oauthNonce = new String(nonce).replaceAll("\\W", "");

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        // Make sure value is encoded
        parameters.add("location=" + URLEncoder.encode(location, "UTF-8"));
        parameters.add("format=json");
        parameters.add("u=c");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }

        String signatureString = "GET&" +
                URLEncoder.encode(url, "UTF-8") + "&" +
                URLEncoder.encode(parametersList.toString(), "UTF-8");

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            Base64.Encoder encoder = Base64.getEncoder();
            signature = encoder.encodeToString(rawHMAC);
        } catch (Exception e) {
            System.err.println("Unable to append signature");
            System.exit(0);
        }

        String authorizationLine = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?location=" + location + "&format=json&u=c"))
                .header("Authorization", authorizationLine)
                .header("X-Yahoo-App-Id", appId)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        try
        {
            JSONObject data = new JSONObject(response.body());

            JSONObject locationData = data.getJSONObject("location");
            JSONObject currentObservationData = data.getJSONObject("current_observation");

            JSONObject windData = currentObservationData.getJSONObject("wind");
            JSONObject atmosphereData = currentObservationData.getJSONObject("atmosphere");
            JSONObject astronomyData = currentObservationData.getJSONObject("astronomy");
            JSONObject conditionData = currentObservationData.getJSONObject("condition");


            //Location data
            String city = locationData.getString("city");
            String region = locationData.getString("region");
            String country = locationData.getString("country");
            Double latitude = (Double) locationData.get("lat");
            Double longitude = (Double) locationData.get("long");
            String timezone_id = locationData.getString("timezone_id");
            TimeZone tz = TimeZone.getTimeZone(timezone_id);

            //Current Observation data
            int date = (int) currentObservationData.get("pubDate");
            Date date2 = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone_id));



            //Wind data
            Double windSpeed = (Double) windData.get("speed");

            //Atmosphere data
            int humidity = (int) atmosphereData.get("humidity");
            Double visibility = (Double) atmosphereData.get("visibility");

            //Astronomy data
            String sunrise = astronomyData.getString("sunrise");
            String sunset = astronomyData.getString("sunset");

            //Condition data
            String currentCondition = conditionData.getString("text");
            int temperature = (int) conditionData.get("temperature");
            int code = (int) conditionData.get("code");


            //forecasts data
            JSONArray forecastsArray = data.getJSONArray("forecasts");
            int[] forecastLow = new int[forecastsArray.length()];
            int[] forecastHigh = new int[forecastsArray.length()];
            int[] forecastCode = new int[forecastsArray.length()];
            String[] forecastCondition = new String[forecastsArray.length()];
            String[] forecastDay = new String[forecastsArray.length()];

            for(int i = 0; i < forecastsArray.length(); i++)
            {
                JSONObject forecast = forecastsArray.getJSONObject(i);
                forecastLow[i] = (int)forecast.get("low");
                forecastHigh[i] = (int)forecast.get("high");
                forecastCode[i] = (int)forecast.get("code");
                forecastCondition[i] = forecast.getString("text");
                forecastDay[i] = forecast.getString("day");


            }

            forecastDay[0] = "Today";
            StringBuilder dayString = new StringBuilder();
            StringBuilder conditionString = new StringBuilder();
            StringBuilder highLowString = new StringBuilder();

            for(int i = 0; i < forecastsArray.length(); i++)
            {

                dayString.append(forecastDay[i] + "\n");
                conditionString.append(forecastCondition[i] +"\n");
                highLowString.append(forecastHigh[i] + "℃" + "/" + forecastLow[i] + "℃" + "\n");
            }



            String iconURL = "http://l.yimg.com/a/i/us/we/52/" + code  + ".gif";
            EmbedBuilder weatherEmbed = new EmbedBuilder();
            weatherEmbed.setThumbnail(iconURL);
            weatherEmbed.setTitle("Weather of " + city + ", " + country);
            weatherEmbed.addField(":earth_asia: Current Condition", currentCondition, true);
            weatherEmbed.addField(":thermometer: Temperature", temperature + "℃", true);
            weatherEmbed.addField(":sweat_drops: Humidity", humidity + "%", true);
            weatherEmbed.addField(":wind_blowing_face: Wind Speed", windSpeed + "km/h", true);
            weatherEmbed.addField(":fog: Visibility", windSpeed + "km", true);
            weatherEmbed.addField(":sunrise: Sunrise", sunrise, true);
            weatherEmbed.addField(":city_sunset: Sunset", sunset, true);
            weatherEmbed.addField(":clock: Time", dateFormat.format(date2), true);
            weatherEmbed.addField(":globe_with_meridians: Timezone", tz.getDisplayName(), true);
            weatherEmbed.addField("Forecast:", "*Forecast for " + city + ", " + country + "*", false);
            weatherEmbed.addField(":calendar: Day" , dayString.toString(), true);
            weatherEmbed.addField(":white_sun_small_cloud: Condition" , conditionString.toString(), true);
            weatherEmbed.addField(":thermometer: High/Low" , highLowString.toString(), true);
            ctx.getMessage().getTimeCreated();
            weatherEmbed.setTimestamp(Instant.now());
            weatherEmbed.setColor(0xFF990F);
            channel.sendMessage(weatherEmbed.build()).queue();

        }

        catch(JSONException e)
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
