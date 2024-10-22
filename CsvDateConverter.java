import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CsvDateConverter {

    //private static final int NUM_THREADS = 8; // Number of threads to use

    public static void main(String[] args) {
       for (int i =1; i <= 12; i++) {
            program(i);
       } 
      
    }

    private static void program(int NUM_THREADS){
        String inputFilePath ="random_data.csv"; // Replace with your input file path
        String outputFilePath = NUM_THREADS+"output.csv"; // Replace with your output file path

        long startTime = System.nanoTime(); // Start the timer

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            List<String> lines = br.lines().collect(Collectors.toList());

            // Divide lines into chunks for parallel processing
            int chunkSize = (int) Math.ceil(lines.size() / (double) NUM_THREADS);
            latch = new CountDownLatch((int) Math.ceil(lines.size() / (double) chunkSize));

            for (int i = 0; i < lines.size(); i += chunkSize) {
                final int start = i;
                final int end = Math.min(i + chunkSize, lines.size());

                executor.submit(() -> {
                    processLines(lines.subList(start, end), outputFilePath);
                    latch.countDown(); // Decrement the latch count
                });
            }

            latch.await(); // Wait for all threads to finish
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long endTime = System.nanoTime(); // Stop the timer
        long durationInNanos = endTime - startTime; // Calculate duration

        // Convert duration to seconds, minutes, and hours
        long durationInSeconds = durationInNanos / 1_000_000_000;
        long hours = durationInSeconds / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        long seconds = durationInSeconds % 60;

        // Display execution duration
        System.out.printf("thread %d Durée d'exécution : %d heures, %d minutes, %d secondes%n",NUM_THREADS, hours, minutes, seconds);
    }

    private static void processLines(List<String> lines, String outputFilePath) {
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})"); // Regex for yyyy-mm-dd
       
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true))) { // Append mode
            for (String line : lines) {
                if (line.trim().isEmpty()) { // Skip empty or whitespace-only lines
                    continue;
                }

                // Replace dates
                Matcher dateMatcher = datePattern.matcher(line);
                line = dateMatcher.replaceAll(m -> {
                    String year = m.group(1);
                    String month = m.group(2);
                    String day = m.group(3);
                    return day + "/" + month + "/" + year; // Convert to dd/mm/yyyy
                });

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
