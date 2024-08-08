package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesCheck {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream input = MainApp.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            // Load properties file
            properties.load(input);

            // Print out the properties to check
            System.out.println("db.driverClassName: " + properties.getProperty("db.driverClassName"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
