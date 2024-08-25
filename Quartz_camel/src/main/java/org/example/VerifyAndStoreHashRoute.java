package org.example;

import org.apache.camel.builder.RouteBuilder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class VerifyAndStoreHashRoute extends RouteBuilder {

    private final DataSource dataSource;

    public VerifyAndStoreHashRoute(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        from("direct:verifyAndStoreHash")
                .process(exchange -> {
                    String routeId = exchange.getIn().getHeader("routeId", String.class);
                    String currentHash = exchange.getIn().getHeader("currentHash", String.class);

                    // Проверяем последний хеш для данного маршрута
                    String selectQuery = "SELECT hash FROM route_hashes WHERE route_id = '" + routeId + "' ORDER BY timestamp DESC LIMIT 1";
                    List<Map<String, Object>> result = exchange.getContext().createProducerTemplate().requestBody("jdbc:dataSource", selectQuery, List.class);

                    if (!result.isEmpty()) {
                        String lastHash = (String) result.get(0).get("hash");

                        if (!lastHash.equals(currentHash)) {
                            throw new RuntimeException("Hash mismatch! Data integrity issue in route: " + routeId);
                        }
                    }

                    // Сохраняем новый хеш
                    String insertQuery = "INSERT INTO route_hashes (route_id, hash) VALUES ('" + routeId + "', '" + currentHash + "')";
                    exchange.getIn().setBody(insertQuery);
                })
                .to("jdbc:dataSource")
                .log("Hash for route ${header.routeId} stored successfully.");
    }
}
