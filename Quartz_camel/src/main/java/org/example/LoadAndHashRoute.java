package org.example;

import org.apache.camel.builder.RouteBuilder;
import javax.sql.DataSource;

public class LoadAndHashRoute extends RouteBuilder {

    private final DataSource dataSource;

    public LoadAndHashRoute(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        from("quartz://mainInstance/schedule?cron=0/10+*+*+*+*+?")
                .routeId("loadAndHashRoute")
                .process(exchange -> {
                    // Получаем имя таблицы из заголовка сообщения или используем значение по умолчанию
                    String tableName = exchange.getIn().getHeader("tableName", "my_table", String.class);
                    String data = new DatabaseService(dataSource).getTableData(tableName);
                    String hash = DataHasher.hashData(data);

                    exchange.getIn().setHeader("routeId", "loadAndHashRoute");
                    exchange.getIn().setHeader("currentHash", hash);
                })
                .to("direct:verifyAndStoreHash")  // Переход к маршруту проверки и записи хеша
                .log("Data loaded, hashed, and stored successfully")
                .to("direct:nextStep");  // Переход к следующему шагу
    }
}
