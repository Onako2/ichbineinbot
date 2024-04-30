package rs.onako2.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.CreateClipList;
import org.json.JSONObject;
import rs.onako2.Main;
import rs.onako2.SQLite;
import rs.onako2.TimeConverter;
import rs.onako2.commands.ClipSwitch;
import rs.onako2.commands.StreamSwitch;
import rs.onako2.commands.WatchTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static rs.onako2.Main.*;

public class WriteChannelChatToConsole {

    public WriteChannelChatToConsole(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    public boolean isModerator(ChannelMessageEvent event) {
        try {
            boolean isModeratorBool = new String(Files.readAllBytes(Paths.get(allowModeratorAction.getName()))).contains(event.getUser().getId() + ";");
            return isModeratorBool;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onChannelMessage(ChannelMessageEvent event) {



        CompletableFuture.runAsync(() -> {
            switch (event.getMessage().replace(" \uDB40\uDC00", "").toLowerCase()) {

                case "!watchtime": {
                    new WatchTime().watchtime(event);
                    break;
                }
                case "!topwatchtime": {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), SQLite.getTopWatchtime());
                    break;
                }

                case "!clip": {
                    try {
                        if (!new String(Files.readAllBytes(Paths.get(allowClipping.getName()))).equals("0")) {

                            CreateClipList clipData = twitchClient.getHelix().createClip(credential.getAccessToken(), event.getChannel().getId(), false).execute();

                            clipData.getData().forEach(clip -> twitchClient.getChat().sendMessage(channel, "@" + event.getUser().getName() + " created Clip: https://clips.twitch.tv/" + clip.getId()));
                            clipData.getData().forEach(clip -> System.out.println("Clip created: https://clips.twitch.tv/" + clip.getId()));
                            if (clipData.getData().isEmpty()) {
                                twitchClient.getChat().sendMessage(channel, "Cliperstellen ist fehlgeschlagen!");
                            }
                        } else {
                            twitchClient.getChat().sendMessage(event.getChannel().getName(), "Clips wurden durch Moderatoren deaktiviert");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "!clipoff": {

                    try {
                        new ClipSwitch().clipoff(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "!clipon": {
                    try {
                        new ClipSwitch().clipon(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "!ziege": {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Ziege! Besser benehmen!");
                    break;
                }

                case "!getid": {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "@" + event.getUser().getName() + " Deine NutzerID ist: " + event.getUser().getId());
                    break;
                }

                case "!streamoff": {

                    try {
                        new StreamSwitch().streamoff(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "!streamon": {
                    try {
                        new StreamSwitch().streamon(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "!ping": {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Ping vom Bot: " + twitchClient.getChat().getLatency());
                    break;
                }

                case "!uptime": {
                    try {
                        long timeDifference = System.currentTimeMillis() / 1000 - Long.parseLong(new String(Files.readAllBytes(Paths.get(isStreaming.getName()))));
                        List<String> list = new java.util.ArrayList<>(List.of(""));
                        twitchClient.getChat().sendMessage(event.getChannel().getName(), "Der Stream ist seit " + new TimeConverter().getTime(list, timeDifference) + " online!");
                    } catch (Exception e) {
                        twitchClient.getChat().sendMessage(event.getChannel().getName(),"Uptime konnte nicht abgerufen werden!");
                        e.printStackTrace();
                    }
                    break;
                }

                case "!witz": {
                    try {
                        String jsonString = new String(Files.readAllBytes(Paths.get(jokeTxt.getName()))).replace("[", "").replace("]", "").replace("Ã¤", "ä").replace("Ã¼", "ü").replace("Ã¶", "ö").replace("ÃŸ", "ß").replace("Ãœ", "Ü").replace("â€ž", "„").replace("â€œ", "“");
                        JSONObject obj = new JSONObject(jsonString);
                        String jokeMessage = obj.getString("text");
                        twitchClient.getChat().sendMessage(event.getChannel().getName(), jokeMessage);
                    } catch (Exception e) {
                        twitchClient.getChat().sendMessage(event.getChannel().getName(), "Die Witze sind ausgegangen!");
                        e.printStackTrace();
                    }

                    try {
                        Main.download("https://witzapi.de/api/joke/?limit=1&category=flachwitze&language=de", "witz.txt");
                    } catch (IOException e) {
                        twitchClient.getChat().sendMessage(event.getChannel().getName(), "Der nächste Witz konnte nicht geladen werden!");
                        e.printStackTrace();
                    }
                    break;
                }
            }

//            if (event.getMessage().startsWith("!gpt")) {
//                twitchClient.getChat().sendMessage(event.getChannel().getName(), GPTUtil.generateGPT(event.getMessage().substring(5)));
//            }



            if (event.getMessage().startsWith("!addwatchtime") && isModerator(event)) {

                String[] split = event.getMessage().split(" ");
                if (split.length == 4) {
                    SQLite.addWatchtime(Integer.parseInt(split[1]), split[2], Long.parseLong(split[3]));
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Watchtime für Nutzer " + split[2] + " um " + split[3] + " Sekunden geändert!");
                } else {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Falsche Syntax! !addwatchtime <NutzerID> <Nutzername> <Sekunden>");
                }
            }

            if (event.getMessage().startsWith("!resetwatchtime") && isModerator(event)) {

                String[] split = event.getMessage().split(" ");
                if (split.length == 3) {
                    SQLite.resetWatchtime(Integer.parseInt(split[1]), split[2]);
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Watchtime für Nutzer " + split[2] + " resetet");
                } else {
                    twitchClient.getChat().sendMessage(event.getChannel().getName(), "Falsche Syntax! !resetwatchtime <NutzerID> <Nutzername>");
                }
            }
        });
        

        System.out.printf(
                "Channel [%s] - User[%s] - Message [%s]%n",
                event.getChannel().getName(),
                event.getUser().getName(),
                event.getMessage()
        );
    }
}