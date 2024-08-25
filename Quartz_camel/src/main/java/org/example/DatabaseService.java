package org.example;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.StringJoiner;

public class DatabaseService {

    private final DataSource dataSource;

    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTableData(String tableName) throws Exception {
        StringJoiner dataJoiner = new StringJoiner(",");
        String query = "SELECT * FROM " + tableName; // Формирование SQL-запроса для получения всех данных из таблицы

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Получение метаданных для извлечения информации о столбцах
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount(); // Определение количества столбцов

            while (resultSet.next()) {
                StringJoiner rowJoiner = new StringJoiner("|"); // Используем разделитель для столбцов в строке
                for (int i = 1; i <= columnCount; i++) {
                    // Получаем значение каждого столбца по его индексу
                    String columnValue = resultSet.getString(i);
                    rowJoiner.add(columnValue);
                }
                dataJoiner.add(rowJoiner.toString());
            }
        }
        return dataJoiner.toString();
    }
}