import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ProductCategorizer {
    private Map<String, List<String>> categoryKeywords;

    public ProductCategorizer() {
        CategoryInitializer initializer = new CategoryInitializer();
        categoryKeywords = initializer.initializeCategories();
    }

    public String correctWord(String inputWord) {
        String correctedWord = inputWord;
        int minDistance = Integer.MAX_VALUE;

        try (Connection conn = DriverManager.getConnection(CategoryInitializer.DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT word FROM words")) {

            while (rs.next()) {
                String word = rs.getString("word");
                int distance = calculateLevenshteinDistance(inputWord, word);
                if (distance < minDistance) {
                    correctedWord = word;
                    minDistance = distance;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return correctedWord;
    }

    public void addProductToDatabase(String productName, String category) {
        try (Connection conn = DriverManager.getConnection(CategoryInitializer.DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO products (product_name, category) VALUES (?, ?)")) {
            pstmt.setString(1, productName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
            System.out.println("Продукт успешно добавлен в базу данных.");
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении продукта в базу данных: " + e.getMessage());
        }
    }

    public String categorizeProduct(String productName) {
        String bestCategory = "";
        int bestDistance = Integer.MAX_VALUE;

        // проходим по всем категориям и их ключевым словам
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            // проходим по всем ключевым словам
            for (String keyword : keywords) {
                // вычисляем расстояние Левенштейна между продуктом и ключевым словом
                int distance = calculateLevenshteinDistance(productName, keyword);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestCategory = category;
                }
            }
        }

        return bestCategory;
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost);
            }
        }

        return distance[s1.length()][s2.length()];
    }

    private static void insertProductToTable(String productName, String category, LocalDate date, double price) {
        try (Connection conn = DriverManager.getConnection(CategoryInitializer.DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (product_name, category, date, price) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, productName);
            stmt.setString(2, category);
            stmt.setString(3, date.toString());
            stmt.setDouble(4, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        Scanner scan = new Scanner(System.in);

        String inputProduct = "";
        ProductCategorizer categorizer = new ProductCategorizer();

        while (!inputProduct.equals("стоп")) {
            inputProduct = scan.nextLine();
            String[] parts = inputProduct.split("\\.");

            if (parts.length == 3) {
                String dateString = parts[0];
                String productName = parts[1];
                String priceString = parts[2];

                try {
                    LocalDate date = LocalDate.parse(dateString);
                    double price = Double.parseDouble(priceString);

                    String correctedProductName = categorizer.correctWord(productName);
                    String category = categorizer.categorizeProduct(correctedProductName);

                    // Внесение записи в таблицу products
                    insertProductToTable(correctedProductName, category, date, price);

                    System.out.println("Товар: " + productName);
                    System.out.println("Исправленное название: " + correctedProductName);
                    System.out.println("Категория: " + category);
                    System.out.println("Дата: " + date);
                    System.out.println("Стоимость: " + price);
                    System.out.println();
                } catch (DateTimeParseException | NumberFormatException e) {
                    System.out.println("Ошибка в формате ввода!");
                    System.out.println();
                }
            } else {
                System.out.println("Ошибка в формате ввода!");
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        test();
    }



}
