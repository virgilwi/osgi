package org.example;

import org.apache.camel.builder.RouteBuilder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class AggregateDataRoute extends RouteBuilder {

    private final DataSource dataSource;

    public AggregateDataRoute(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        from("direct:aggregateData")
                .routeId("aggregateDataRoute")
                .setHeader("routeId", constant("aggregateDataRoute"))
                .setHeader("tableName", constant("custom_table"))
                .setBody(constant("SELECT column1, COUNT(*) FROM my_table GROUP BY column1"))
                .to("jdbc:dataSource")
                .process(exchange -> {
                    List<Map<String, Object>> result = exchange.getIn().getBody(List.class);
                    StringBuilder data = new StringBuilder();
                    result.forEach(row -> data.append(row.toString()));
                    String hash = DataHasher.hashData(dataSource.toString());
                    exchange.getIn().setHeader("currentHash", hash);
                })
                .to("direct:verifyAndStoreHash")  // Переход к маршруту проверки и записи хеша
                .log("Data aggregated and hash stored successfully.");
    }
}