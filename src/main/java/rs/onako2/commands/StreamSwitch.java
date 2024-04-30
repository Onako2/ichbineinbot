package rs.onako2.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static rs.onako2.Main.*;

public class StreamSwitch {

    FileWriter isStreaming = new FileWriter("isStreaming");

    public StreamSwitch() throws IOException {
    }

    public void streamon(ChannelMessageEvent event) throws IOException {
        boolean isModerator = new String(Files.readAllBytes(Paths.get(allowModeratorAction.getName()))).contains(event.getUser().getId() + ";");

        if (isModerator) {

            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Die Watchtime wird wieder aufgezeichnet!");
            try {
                long linuxTime = System.currentTimeMillis() / 1000L;
                isStreaming.write(Long.toString(linuxTime));
                isStreaming.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Du hast keine Berechtigungen!");
        }
    }

    public void streamoff(ChannelMessageEvent event) throws IOException {
        boolean isModerator = new String(Files.readAllBytes(Paths.get(allowModeratorAction.getName()))).contains(event.getUser().getId() + ";");

        if (isModerator) {

            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Die Watchtime wird nicht mehr aufgezeichnet!");
            try {
                isStreaming.write("false");
                isStreaming.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Du hast keine Berechtigungen!");
        }
    }
}
