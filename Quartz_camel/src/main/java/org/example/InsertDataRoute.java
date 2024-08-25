package org.example;

import org.apache.camel.builder.RouteBuilder;

import javax.sql.DataSource;

public class InsertDataRoute extends RouteBuilder {

    private final DataSource dataSource;

    public InsertDataRoute(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        from("direct:insertData")
                .routeId("insertDataRoute")
                .setHeader("routeId", constant("insertDataRoute"))
                .setBody(constant("INSERT INTO my_table (column1, column2) VALUES ('value1', 'value2')"))
                .to("jdbc:dataSource")
                .log("Data inserted successfully")
                .process(exchange -> {
                    String tableName = exchange.getIn().getHeader("tableName", "my_table", String.class);
                    String data = "value1,value2";  // Данные, которые были вставлены
                    String hash = DataHasher.hashData(data);
                    exchange.getIn().setHeader("currentHash", hash);
                })
                .to("direct:verifyAndStoreHash");  // Переход к маршруту проверки и записи хеша
    }
}
