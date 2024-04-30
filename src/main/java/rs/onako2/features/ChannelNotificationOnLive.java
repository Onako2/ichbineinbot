package rs.onako2.features;

import com.github.twitch4j.events.ChannelGoLiveEvent;

import java.io.FileWriter;

import static rs.onako2.Main.isStreaming;

public class ChannelNotificationOnLive {


    public void onGoLive(ChannelGoLiveEvent event) {
        try {
            FileWriter fWriter = new FileWriter(isStreaming);

            long linuxTime = System.currentTimeMillis() / 1000L;
            fWriter.write(Long.toString(linuxTime));
            fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}