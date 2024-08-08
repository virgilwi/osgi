package org.example;

import org.apache.camel.builder.RouteBuilder;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        String jdbcUrl = "{{db.url}}";
        String jdbcUsername = "{{db.username}}";
        String jdbcPassword = "{{db.password}}";
        String jdbcDriver = "{{db.driverClassName}}";

        // Извлечение данных из базы данных
        fromF("timer://runOnce?repeatCount=1")
                .setBody(constant("SELECT * FROM your_table"))
                .toF("jdbc:yourDataSource?dataSource.url=%s&dataSource.username=%s&dataSource.password=%s&dataSource.driverClassName=%s",
                        jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver)
                .log("Data fetched from DB: ${body}");
    }

}
