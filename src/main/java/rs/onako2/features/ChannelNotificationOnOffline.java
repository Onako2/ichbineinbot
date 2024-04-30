package rs.onako2.features;

import java.io.FileWriter;

import static rs.onako2.Main.isStreaming;

public class ChannelNotificationOnOffline {

    public void onGoOffline() {
        try {
            FileWriter fWriter = new FileWriter(isStreaming);
            fWriter.write(0);
            fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}