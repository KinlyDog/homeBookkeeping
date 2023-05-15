import java.util.*;

public class ProductCategorizer {
    private Map<String, List<String>> categoryKeywords;
    private Set<String> dictionaryWords;

    public ProductCategorizer() {
        categoryKeywords = new HashMap<>();
        categoryKeywords.put("Фрукты", List.of("яблоко", "апельсин", "банан", "груша"));
        categoryKeywords.put("Овощи", List.of("морковь", "картофель", "помидор", "огурец"));
        categoryKeywords.put("Зелень", List.of("укроп", "петрушка", "шпинат", "базилик"));
        // Добавьте другие категории и их ключевые слова, если нужно

        dictionaryWords = new HashSet<>();
        for (List<String> words : categoryKeywords.values()) {
            dictionaryWords.addAll(words);
        }
    }

    public String categorizeProduct(String productName) {
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (productName.contains(keyword)) {
                    return category;
                }
            }
        }

        return "Неизвестная категория";
    }

    public String correctWord(String inputWord) {
        String correctedWord = inputWord;
        int minDistance = Integer.MAX_VALUE;

        for (String word : dictionaryWords) {
            int distance = calculateLevenshteinDistance(inputWord, word);
            if (distance < minDistance) {
                correctedWord = word;
                minDistance = distance;
            }
        }

        return correctedWord;
    }

    public int calculateLevenshteinDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String inputProduct = "";
        ProductCategorizer categorizer = new ProductCategorizer();

        while (!inputProduct.equals("стоп")) {
            inputProduct = scan.nextLine();
            String correctedProduct = categorizer.correctWord(inputProduct);
            String category = categorizer.categorizeProduct(correctedProduct);
            System.out.println("Товар: " + inputProduct);
            System.out.println("Исправленный товар: "  + correctedProduct);
            System.out.println("Категория: " + category);
            System.out.println();
        }
    }
}
