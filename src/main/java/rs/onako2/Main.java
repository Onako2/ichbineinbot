package rs.onako2;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.ChattersList;
import rs.onako2.features.ChannelNotificationOnLive;
import rs.onako2.features.ChannelNotificationOnOffline;
import rs.onako2.features.WriteChannelChatToConsole;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static File allowClipping = new File("allowClipping");
    public static File config = new File("config.properties");
    public static File allowModeratorAction = new File("allowModeratorAction.txt");
    public static File isStreaming = new File("isStreaming");
    public static File jokeTxt = new File("witz.txt");

    public static String channel = new Config().getConfig("channel");
    public static String channelID = new Config().getConfig("channelID");
    public static OAuth2Credential credential = new OAuth2Credential("twitch", new Config().getConfig("apiToken"));
    static String botName = new Config().getConfig("botName");
    public static List<String> bots = Arrays.asList("michaelxfe5rg", "???", "ThisIsUNREALlol", "00_ella", "00_Aaliyah", "noootnootbot", "StreamElements", botName, channel, "Nightbot", "d0nk7", "Drapsnatt", "ASMR_Miyu", "sunshineboy42", "ZVgn", "rsaI", "SoundAlerts", "00_alissa", "MarkZynk", "AnotherTTVViewer", "regressz", "8HVDES", "8roe", "vlMercy", "psh_aa", "RogueG1rl", "㈇ㅅ㈇", "00_Darla", "0_lonely_egirl", "natotu", "david3cetqd", "CommanderRoot", "ultimate_phoenix_fan", "johnk4c55v", "tarsaI", "mark8bl82w", "richard9oipjx", "jamesapwzaf", "Torti_yo");
    static String clientID = new Config().getConfig("clientID");
    public static TwitchClient twitchClient = TwitchClientBuilder.builder()
            .withClientId(clientID)
            .withDefaultAuthToken(credential)
            .withEnableHelix(true)
            .withEnableChat(true)
            .withChatAccount(credential)
            .withEnablePubSub(true)
            .build();


    // code from https://www.digitalocean.com/community/tutorials/java-download-file-url . Thanks, Pankaj! <3
    public static void download(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }



    public static void main(String[] args) throws IOException {

        if (!config.exists()) {
            download("https://nuc.de.majic.rs/tuutbot/config.properties", "config.properties");
        }

        if (new Config().getConfig("agreeMIT").equals("false")) {
            System.out.println("You need to agree to the MIT license to use this bot! Please read config.properties");
            System.exit(0);
        }

        SQLite.initializeDatabase();


        System.out.println("Hello world!");
        twitchClient.getClientHelper().enableStreamEventListener(channel);
        twitchClient.getChat().joinChannel(channel);
        //twitchClient.getChat().sendMessage(channel, "Der Bot ist jetzt hochgefahren!");

        if (!allowClipping.exists()) {
            System.out.println("Generated allowClipping file!");
            allowClipping.createNewFile();
            FileWriter fWriter = new FileWriter(allowClipping.getName());
            fWriter.write("true");
            fWriter.close();
        }

        if (!jokeTxt.exists()) {
            System.out.println("Generated witz.txt file!");
            jokeTxt.createNewFile();
            FileWriter fWriter = new FileWriter(jokeTxt.getName());
            fWriter.write("First time setup!");
            fWriter.close();
        }


        if (!isStreaming.exists()) {
            System.out.println("Generated isStreaming file!");
            isStreaming.createNewFile();
            FileWriter fWriter = new FileWriter(isStreaming.getName());
            fWriter.write(0);
            fWriter.close();
        }

        if (!allowModeratorAction.exists()) {
            System.out.println("Generated Moderator config file!");
            allowModeratorAction.createNewFile();
            FileWriter fWriter = new FileWriter(allowModeratorAction.getName());
            fWriter.write(channelID + ";");
            fWriter.close();
        }

        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        WriteChannelChatToConsole writeChannelChatToConsole = new WriteChannelChatToConsole(eventHandler);

        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, channelID);


//        eventHandler.onEvent(RewardRedeemedEvent.class, (event) -> {
//            String rewardId = event.getRedemption().getReward().getId();
//            if (commandMap.containsKey(rewardId)) {
//                commandMap.get(rewardId).getAction().execute(twitchClient.getChat(), configuration, executorService);
//            }
//            else {
//                System.out.println("Could not find a command for reward " + rewardId);
//            }
//        });


        eventHandler.onEvent(ChannelGoLiveEvent.class, event -> {
            twitchClient.getChat().sendMessage(channel, "[" + event.getChannel().getName() + "] ist live-gegangen mit dem Titel " + event.getStream().getTitle() + " im Spiel " + event.getStream().getGameName() + "!");
            new ChannelNotificationOnLive().onGoLive(event);
        });

        eventHandler.onEvent(ChannelGoOfflineEvent.class, event -> {
            twitchClient.getChat().sendMessage(channel, "[" + event.getChannel().getName() + "] ist gerade offline gegangen!");
            new ChannelNotificationOnOffline().onGoOffline();
        });


        CompletableFuture.runAsync(() -> {
            while (true) {

                ChattersList chattersList = twitchClient.getHelix().getChatters(credential.getAccessToken(), channelID, "1044533775", 1000, "").execute();
                int chatterListSize = chattersList.getChatters().size();
                for (int i = 0; i < chatterListSize; i++) {

                    int userID = Integer.parseInt(chattersList.getChatters().get(i).getUserId());
                    String userName = chattersList.getChatters().get(i).getUserName();
                    if (!bots.contains(userName)) {
                        SQLite.addWatchtime(userID, userName, 10L);
                    }
                    //System.out.println("User: " + userName + " ID: " + userID);
                }
                //SQLite.printAllData();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
       });

    }
}