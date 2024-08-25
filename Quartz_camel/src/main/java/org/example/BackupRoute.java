package org.example;

import org.apache.camel.builder.RouteBuilder;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class BackupRoute extends RouteBuilder {

    private final DataSource dataSource;

    public BackupRoute(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        from("quartz://backupInstance/check?cron=0/15+*+*+*+*+?")
                .routeId("backupRoute")
                .setBody(constant("SELECT hash FROM route_hashes WHERE route_id = 'loadAndHashRoute' ORDER BY timestamp DESC LIMIT 1"))
                .to("jdbc:dataSource")
                .process(exchange -> {
                    List<Map<String, Object>> result = exchange.getIn().getBody(List.class);
                    if (result.isEmpty()) {
                        throw new RuntimeException("No previous hash found for loadAndHashRoute. Cannot verify data integrity.");
                    }

                    String lastHash = (String) result.get(0).get("hash");

                    // Получаем имя таблицы из заголовка сообщения или используем значение по умолчанию
                    String tableName = exchange.getIn().getHeader("tableName", "my_table", String.class);
                    String data = new DatabaseService(dataSource).getTableData(tableName);
                    String currentHash = DataHasher.hashData(data);

                    exchange.getIn().setHeader("lastHash", lastHash);
                    exchange.getIn().setHeader("currentHash", currentHash);
                })
                .choice()
                .when(header("lastHash").isNotEqualTo(header("currentHash")))
                .log("Backup instance: Hashes do not match! Aborting.")
                .stop()
                .otherwise()
                .log("Backup instance: Hashes match. Data is consistent. Resuming process.")
                .end()
                .to("direct:nextStep");  // Переход к следующему шагу, если хеши совпадают
    }
}
