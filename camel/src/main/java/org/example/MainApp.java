package org.example;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class MainApp {

    public static void main(String[] args) {
        try (CamelContext context = new DefaultCamelContext()) {
            // Явная загрузка свойств
            Properties properties = new Properties();
            try (InputStream input = MainApp.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find application.properties");
                    return;
                }
                // Загрузка файла свойств
                properties.load(input);
                System.out.println("Loaded properties:");
                properties.forEach((key, value) -> System.out.println(key + ": " + value));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Пробуем загружать свойства через Camel
            context.getPropertiesComponent().setLocation("classpath:application.properties");

            // Создаем DataSource с использованием загруженных значений
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(properties.getProperty("db.driverClassName"));
            ds.setUrl(properties.getProperty("db.url"));
            ds.setUsername(properties.getProperty("db.username"));
            ds.setPassword(properties.getProperty("db.password"));
            context.getRegistry().bind("yourDataSource", ds);

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("timer://foo?period=10000")
                            .setBody(constant("SELECT * FROM your_table"))
                            .to("jdbc:yourDataSource")
                            .log("Data fetched from DB: ${body}");
                }
            });

            // Запускаем контекст
            context.start();
            Thread.sleep(30000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


