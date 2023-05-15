import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryInitializer {
    protected static final String DATABASE_URL = "jdbc:sqlite:identifier.sqlite";

    public Map<String, List<String>> initializeCategories() {
        Map<String, List<String>> categoryKeywords = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT category FROM words")) {
            while (rs.next()) {
                String category = rs.getString("category");
                categoryKeywords.put(category, new ArrayList<>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM words")) {
            while (rs.next()) {
                String word = rs.getString("word");
                String category = rs.getString("category");
                if (categoryKeywords.containsKey(category)) {
                    categoryKeywords.get(category).add(word);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryKeywords;
    }
}
