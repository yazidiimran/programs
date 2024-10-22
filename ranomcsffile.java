import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CSVFileGenerator {

    private static final int NUMBER_OF_LINES = 10_000_000;
    private static final String[] NAMES = {"John", "Alice", "Bob", "Mary", "Charlie", "Emma", "Liam", "Olivia", "Noah", "Ava"};
    private static final String[] CITIES = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "San Antonio", "San Diego", "Dallas", "San Jose", "Austin"};

    public static void main(String[] args) {
        String fileName = "random_data.csv";
        generateCSVFile(fileName);
        System.out.println("CSV file generated: " + fileName);
    }

    private static void generateCSVFile(String fileName) {
        Random random = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write CSV header
            writer.write("ID,Name,Age,City,Date");
            writer.newLine();

            for (int i = 1; i <= NUMBER_OF_LINES; i++) {
                String id = String.valueOf(i);
                String name = NAMES[random.nextInt(NAMES.length)];
                String age = String.valueOf(18 + random.nextInt(63));  // Ages between 18 and 80
                String city = CITIES[random.nextInt(CITIES.length)];
                String date = dateFormat.format(randomDate());

                // Write a line to the CSV
                writer.write(String.join(",", id, name, age, city, date));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Date randomDate() {
        Random random = new Random();
        long currentTimeMillis = System.currentTimeMillis();
        long randomTimeMillis = currentTimeMillis - (random.nextInt(50 * 365) * 24L * 60L * 60L * 1000L);  // Random date within 50 years
        return new Date(randomTimeMillis);
    }
}
