package rs.onako2.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import rs.onako2.SQLite;
import rs.onako2.TimeConverter;

import java.util.List;

import static rs.onako2.Main.twitchClient;

public class WatchTime {

    public void watchtime(ChannelMessageEvent event) {
        List<String> list = new java.util.ArrayList<>(List.of("Watchtime: "));

        String userId = event.getUser().getId();
        long watchtimeTime = SQLite.getWatchtime(Integer.parseInt(userId));

        String watchtime = new TimeConverter().getTime(list, watchtimeTime);
        if (watchtime.equals("Watchtime: ")) {
            twitchClient.getChat().sendMessage(event.getChannel().getName(), "@" + event.getUser().getName() + " deine Watchtime ist momentan nicht verfügbar.");
        } else {
            twitchClient.getChat().sendMessage(event.getChannel().getName(), watchtime);
        }


    }

}
