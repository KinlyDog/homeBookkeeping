import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateWordIds {
    public static void main(String[] args) {
        String databaseURL = "jdbc:sqlite:identifier.sqlite";

        try (Connection connection = DriverManager.getConnection(databaseURL);
             Statement statement = connection.createStatement()) {

            // Создаем новую временную таблицу
            statement.execute("CREATE TABLE temp_words AS SELECT * FROM words ORDER BY id");

            // Удаляем исходную таблицу
            statement.execute("DROP TABLE words");

            // Переименовываем временную таблицу
            statement.execute("ALTER TABLE temp_words RENAME TO words");

            System.out.println("Изменение ID слов выполнено успешно.");

        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }
}
