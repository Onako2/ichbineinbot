package rs.onako2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public String getConfig(String configKey) {
        try {
            String configFilePath = "config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);

            return prop.getProperty(configKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configKey;
    }
}
