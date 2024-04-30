package rs.onako2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GPTUtil {

    private static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    public static String generateGPT(String input) {

        try {
            URL url = new URL("http://192.168.101.34:8080/?prompt=" + encodeValue(input));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (Exception e) {
            // Exceptions generally may happen if the model file fails to load
            // for a number of reasons such as a file not found.
            // It is possible that Java may not be able to dynamically load the native shared library or
            // the llmodel shared library may not be able to dynamically load the backend
            // implementation for the model file you provided.
            //
            // Once the LLModel class is successfully loaded into memory the text generation calls
            // generally should not throw exceptions.
            e.printStackTrace(); // Printing here but in a production system you may want to take some action.
            return "Ups! Ich bin müde und muss schlafen gehen! Aktuell ist der Server nicht erreichbar für die Generierung!";
        }
    }
}