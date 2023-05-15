import java.sql.*;

public class RemoveDuplicates {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
             Statement statement = connection.createStatement()) {

            // Выполнение скрипта запроса для удаления дубликатов
            String query = "DELETE FROM words " +
                    "WHERE id NOT IN (" +
                    "    SELECT MIN(id) " +
                    "    FROM words " +
                    "    GROUP BY word, category " +
                    ")";
            statement.executeUpdate(query);

            System.out.println("Дубликаты успешно удалены из таблицы.");
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }
}
