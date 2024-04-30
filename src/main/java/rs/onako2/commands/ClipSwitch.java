package rs.onako2.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static rs.onako2.Main.*;

public class ClipSwitch {

    FileWriter allowClippingWriter = new FileWriter("allowClipping");

    public ClipSwitch() throws IOException {
    }


    public void clipon(ChannelMessageEvent event) throws IOException {
        boolean isModerator = new String(Files.readAllBytes(Paths.get(allowModeratorAction.getName()))).contains(event.getUser().getId() + ";");

        if (isModerator) {

            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Clips wurden aktiviert!");
            try {
                allowClippingWriter.write("true");
                allowClippingWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clipoff(ChannelMessageEvent event) throws IOException {
        boolean isModerator = new String(Files.readAllBytes(Paths.get(allowModeratorAction.getName()))).contains(event.getUser().getId() + ";");

        if (isModerator) {

            twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " Clips wurden deaktiviert!");
            try {
                allowClippingWriter.write("false");
                allowClippingWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
