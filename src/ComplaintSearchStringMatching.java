import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PatternSearch {

    // ================= KMP =================

    static int[] computeLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }

    static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();

        if (pattern.isEmpty()) {
            return matches;
        }

        int[] lps = computeLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }

            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return matches;
    }


    // ================= Z Algorithm =================

    static List<Integer> zSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();

        if (pattern.isEmpty()) {
            return matches;
        }

        String combined = pattern + "$" + text;

        int[] z = new int[combined.length()];

        int left = 0;
        int right = 0;

        for (int i = 1; i < combined.length(); i++) {

            if (i < right) {
                z[i] = Math.min(right - i, z[i - left]);
            }

            while (i + z[i] < combined.length()
                    && combined.charAt(z[i]) == combined.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] > right) {
                left = i;
                right = i + z[i];
            }
        }

        for (int i = pattern.length() + 1; i < combined.length(); i++) {
            if (z[i] == pattern.length()) {
                matches.add(i - pattern.length() - 1);
            }
        }

        return matches;
    }


    // ================= Rabin-Karp =================

    static List<Integer> rabinKarpSearch(String text, String pattern) {

        List<Integer> matches = new ArrayList<>();

        if (pattern.isEmpty() || pattern.length() > text.length()) {
            return matches;
        }

        int base = 256;
        int prime = 101;

        int m = pattern.length();
        int n = text.length();

        int patternHash = 0;
        int textHash = 0;
        int h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }

        for (int i = 0; i < m; i++) {
            patternHash =
                    (base * patternHash + pattern.charAt(i)) % prime;

            textHash =
                    (base * textHash + text.charAt(i)) % prime;
        }

        for (int i = 0; i <= n - m; i++) {

            if (patternHash == textHash) {

                boolean found = true;

                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    matches.add(i);
                }
            }

            if (i < n - m) {

                textHash =
                        (base * (textHash - text.charAt(i) * h)
                        + text.charAt(i + m)) % prime;

                if (textHash < 0) {
                    textHash += prime;
                }
            }
        }

        return matches;
    }


    // ================= Main =================

    public static void main(String[] args) {

        String filePath = "data/corpus.txt";

        try {

            String corpus = Files.readString(
                    Paths.get(filePath)
            );

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter search string: ");
            String pattern = scanner.nextLine();

            // KMP
            List<Integer> kmpResults =
                    kmpSearch(corpus, pattern);

            // Z Algorithm
            List<Integer> zResults =
                    zSearch(corpus, pattern);

            // Rabin-Karp
            List<Integer> rabinResults =
                    rabinKarpSearch(corpus, pattern);


            System.out.println("\n===== SEARCH RESULTS =====");

            System.out.println("\nKMP:");
            System.out.println("Matches found: "
                    + kmpResults.size());
            System.out.println(kmpResults);


            System.out.println("\nZ-Algorithm:");
            System.out.println("Matches found: "
                    + zResults.size());
            System.out.println(zResults);


            System.out.println("\nRabin-Karp:");
            System.out.println("Matches found: "
                    + rabinResults.size());
            System.out.println(rabinResults);

            scanner.close();

        } catch (IOException e) {

            System.out.println(
                    "Error reading corpus: " + e.getMessage()
            );
        }
    }
}
