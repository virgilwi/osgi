package org.example;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;

public class CamelPropertiesExample {

    public static void main(String[] args) {
        // Использование try-with-resources для автоматического закрытия CamelContext
        try (CamelContext context = new DefaultCamelContext()) {

            // Создаем и настраиваем DataSource
            DataSource dataSource = setupDataSource(context);

            // Регистрируем DataSource в CamelContext
            context.getRegistry().bind("yourDataSource", dataSource);

            // Настраиваем PropertiesComponent для загрузки файла свойств
            context.getPropertiesComponent().setLocation("classpath:application.properties");

            // Добавляем маршруты Camel
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {

                    // Используем свойства для настройки подключения к базе данных
                    from("timer://foo?period=10000")
                            .setBody(constant("SELECT * FROM your_table"))
                            .to("jdbc:yourDataSource")
                            .log("Data fetched from DB: ${body}");
                }
            });

            // Запускаем CamelContext
            context.start();
            Thread.sleep(30000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Контекст будет автоматически закрыт здесь
    }

    // Метод для создания и настройки DataSource
    private static @NotNull DataSource setupDataSource(CamelContext context) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(context.resolvePropertyPlaceholders("{{db.driverClassName}}"));
        ds.setUrl(context.resolvePropertyPlaceholders("{{db.url}}"));
        ds.setUsername(context.resolvePropertyPlaceholders("{{db.username}}"));
        ds.setPassword(context.resolvePropertyPlaceholders("{{db.password}}"));
        return ds;
    }
}