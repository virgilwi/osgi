package org.example;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import javax.sql.DataSource;

public class App {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Конфигурация DataSource
        DataSource dataSource = DatabaseConfig.createDataSource();
        context.getRegistry().bind("dataSource", dataSource);

        // Добавляем маршруты
        context.addRoutes(new LoadAndHashRoute(dataSource));
        context.addRoutes(new BackupRoute(dataSource));
        context.addRoutes(new InsertDataRoute(dataSource));
        context.addRoutes(new AggregateDataRoute(dataSource));
        context.addRoutes(new VerifyAndStoreHashRoute(dataSource));
        context.addRoutes(new ProcessStepsRoute());

        // Запуск Camel контекста
        context.start();
        Thread.sleep(100000);  // Оставьте время для выполнения маршрутов
        context.stop();
    }
}
