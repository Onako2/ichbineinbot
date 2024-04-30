package rs.onako2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


// partially created with GitHub Copilot
public class SQLite {

    // Database URL
    private static final String URL = "jdbc:sqlite:twitch_watchtime.db";

    // Method to initialize the SQLite database
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS watchtime (id INTEGER PRIMARY KEY, user_id INTEGER, username TEXT, watchtime_seconds INTEGER)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }


    public static String getTopWatchtime() {
        String sql = "SELECT * FROM watchtime ORDER BY watchtime_seconds DESC LIMIT 5";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<WatchtimeEntry> entries = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                Long watchtime = rs.getLong("watchtime_seconds");
                entries.add(new WatchtimeEntry(id, userId, username, watchtime));
            }
            // Sort the entries based on watch time in descending order
            Collections.sort(entries, Comparator.comparing(WatchtimeEntry::getWatchtime).reversed());
            // Print the sorted entries
            List<String> returnMessage = new ArrayList<>(List.of());
            int i = 1;
            for (WatchtimeEntry entry : entries) {
                List<String> list = new ArrayList<>();
                String thingToAdd = getEmoji(i) + " " + entry.username + ": " + new TimeConverter().getTime(list, entry.watchtime);
                returnMessage.add(thingToAdd);
                i++;
            }
            return "Top Watchtime: " + returnMessage.toString().replace("[", "").replace("]", "");
        } catch (SQLException e) {
            return "Datenbankabfrage ist fehlgeschlagen! Bitte versuche es später nochmal oder kontaktiere meinen Entwickler ichbinkeinbotc00l/Onako2!";
        }
    }

    public static String getEmoji(int i) {
        //switch statement that converts integer into emoji
        return switch (i) {
            case 1 -> "①";
            case 2 -> "②";
            case 3 -> "③";
            case 4 -> "④";
            case 5 -> "⑤";
            default -> "\uD83D\uDC80";
        };
    }
    public static void printAllData() {
        String sql = "SELECT * FROM watchtime";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                long watchtime = rs.getLong("watchtime_seconds");
                System.out.println("ID: " + id + ", User ID: " + userId + ", Username: " + username + ", Watch Time: " + watchtime + " seconds");
            }
        } catch (SQLException e) {
            System.out.println("Error printing data: " + e.getMessage());
        }
    }

    // Method to add watch time for a user
    public static void addWatchtime(int userId, String username, Long seconds) {
        String sqlCheck = "SELECT COUNT(*) AS count FROM watchtime WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
            pstmtCheck.setInt(1, userId);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next() && rs.getInt("count") == 0) {
                    // User doesn't exist, insert new record
                    String sqlInsert = "INSERT INTO watchtime(user_id, username, watchtime_seconds) VALUES(?, ?, ?)";
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                        pstmtInsert.setInt(1, userId);
                        pstmtInsert.setString(2, username);
                        pstmtInsert.setLong(3, seconds);
                        pstmtInsert.executeUpdate();
                    }
                } else {
                    // User already exists, update watch time
                    String sqlUpdate = "UPDATE watchtime SET watchtime_seconds = watchtime_seconds + ? WHERE user_id = ?";
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        pstmtUpdate.setLong(1, seconds);
                        pstmtUpdate.setInt(2, userId);
                        pstmtUpdate.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding watch time: " + e.getMessage());
        }
    }

    public static void resetWatchtime(int userId, String username) {
        String sql = "UPDATE watchtime SET watchtime_seconds = 0 WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error resetting watch time: " + e.getMessage());
        }
    }

    // Method to retrieve watch time for a user
    public static Long getWatchtime(int userId) {
        String sql = "SELECT watchtime_seconds FROM watchtime WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("watchtime_seconds");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving watch time: " + e.getMessage());
        }
        return 0L; // Default to 0 if user not found or other error
    }

    static class WatchtimeEntry {
        private int id;
        private int userId;
        private String username;
        private Long watchtime;

        public WatchtimeEntry(int id, int userId, String username, Long watchtime) {
            this.id = id;
            this.userId = userId;
            this.username = username;
            this.watchtime = watchtime;
        }

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public Long getWatchtime() {
            return watchtime;
        }
    }
}
