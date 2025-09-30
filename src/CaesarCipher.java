import java.util.*;
import java.io.*;

public class CaesarCipher {
    private static final char[] RUSSIAN_LOWER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();
    private static final char[] RUSSIAN_UPPER = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();
    private static final char[] ENGLISH_LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] ENGLISH_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ШИФР ЦЕЗАРЯ ===");
            System.out.println("1. Зашифровать текст");
            System.out.println("2. Расшифровать текст с ключом");
            System.out.println("3. Взломать перебором (brute force)");
            System.out.println("4. Выйти");
            System.out.print("Выберите режим: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1: encryptText(scanner); break;
                    case 2: decryptWithKey(scanner); break;
                    case 3: bruteForceDecrypt(scanner); break;
                    case 4:
                        System.out.println("Выход...");
                        return;
                    default:
                        System.out.println("Ошибка: выберите число от 1 до 4");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    private static int findCharIndex(char[] alphabet, char c) {
        try {
            for (int i = 0; i < alphabet.length; i++) {
                if (alphabet[i] == c) {
                    return i;
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private static char processChar(char c, int key, boolean encrypt) {
        try {
            char[] alphabet = null;
            int index = -1;

            if ((index = findCharIndex(RUSSIAN_LOWER, c)) != -1) {
                alphabet = RUSSIAN_LOWER;
            } else if ((index = findCharIndex(RUSSIAN_UPPER, c)) != -1) {
                alphabet = RUSSIAN_UPPER;
            } else if ((index = findCharIndex(ENGLISH_LOWER, c)) != -1) {
                alphabet = ENGLISH_LOWER;
            } else if ((index = findCharIndex(ENGLISH_UPPER, c)) != -1) {
                alphabet = ENGLISH_UPPER;
            }

            if (alphabet == null || index == -1) {
                return c;
            }

            int alphabetSize = alphabet.length;
            if (alphabetSize == 0) return c;

            int normalizedKey = key % alphabetSize;

            if (encrypt) {
                int newIndex = (index + normalizedKey) % alphabetSize;
                return alphabet[newIndex];
            } else {
                int newIndex = (index - normalizedKey + alphabetSize) % alphabetSize;
                return alphabet[newIndex];
            }
        } catch (Exception e) {
            return c;
        }
    }

    private static String processText(String text, int key, boolean encrypt) {
        try {
            if (text == null) return "";

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                result.append(processChar(c, key, encrypt));
            }
            return result.toString();
        } catch (Exception e) {
            System.out.println("Ошибка при обработке текста: " + e.getMessage());
            return "";
        }
    }

    private static boolean validateFile(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                System.out.println("Ошибка: путь к файлу не может быть пустым");
                return false;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Ошибка: файл не существует - " + filePath);
                return false;
            }
            if (!file.canRead()) {
                System.out.println("Ошибка: нет прав на чтение файла - " + filePath);
                return false;
            }
            if (file.length() == 0) {
                System.out.println("Ошибка: файл пустой - " + filePath);
                return false;
            }
            if (file.isDirectory()) {
                System.out.println("Ошибка: указанный путь является папкой, а не файлом - " + filePath);
                return false;
            }
            return true;
        } catch (SecurityException e) {
            System.out.println("Ошибка безопасности при доступе к файлу: " + filePath);
            return false;
        } catch (Exception e) {
            System.out.println("Ошибка при проверке файла: " + e.getMessage());
            return false;
        }
    }

    private static boolean validateOutputPath(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                System.out.println("Ошибка: путь для сохранения не может быть пустым");
                return false;
            }

            File file = new File(filePath);
            File parentDir = file.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                System.out.println("Ошибка: директория для сохранения не существует - " + parentDir.getPath());
                return false;
            }

            if (parentDir != null && !parentDir.canWrite()) {
                System.out.println("Ошибка: нет прав на запись в директорию - " + parentDir.getPath());
                return false;
            }

            if (file.exists() && !file.canWrite()) {
                System.out.println("Ошибка: нет прав на запись в файл - " + filePath);
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при проверке пути для сохранения: " + e.getMessage());
            return false;
        }
    }

    private static boolean validateKey(int key, String language) {
        try {
            if (key < 0) {
                System.out.println("Ошибка: ключ не может быть отрицательным");
                return false;
            }

            int maxKey;
            if (language.equals("russian")) {
                maxKey = RUSSIAN_LOWER.length - 1;
            } else if (language.equals("english")) {
                maxKey = ENGLISH_LOWER.length - 1;
            } else {
                maxKey = Math.max(RUSSIAN_LOWER.length, ENGLISH_LOWER.length) - 1;
            }

            if (key > maxKey) {
                System.out.println("Предупреждение: ключ " + key + " превышает максимальный (" + maxKey + "), будет использован остаток от деления");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при проверке ключа: " + e.getMessage());
            return false;
        }
    }

    private static String detectLanguage(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return "unknown";
            }

            int russianCount = 0;
            int englishCount = 0;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (findCharIndex(RUSSIAN_LOWER, c) != -1 || findCharIndex(RUSSIAN_UPPER, c) != -1) {
                    russianCount++;
                } else if (findCharIndex(ENGLISH_LOWER, c) != -1 || findCharIndex(ENGLISH_UPPER, c) != -1) {
                    englishCount++;
                }
            }

            if (russianCount > englishCount) return "russian";
            if (englishCount > russianCount) return "english";
            return "unknown";
        } catch (Exception e) {
            System.out.println("Ошибка при определении языка: " + e.getMessage());
            return "unknown";
        }
    }

    private static String readFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            throw new IOException("Файл не найден: " + filePath, e);
        } catch (SecurityException e) {
            throw new IOException("Нет прав на чтение файла: " + filePath, e);
        } catch (IOException e) {
            throw new IOException("Ошибка ввода-вывода при чтении файла: " + filePath, e);
        }
    }

    private static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (FileNotFoundException e) {
            throw new IOException("Не удалось создать файл: " + filePath, e);
        } catch (SecurityException e) {
            throw new IOException("Нет прав на запись в файл: " + filePath, e);
        } catch (IOException e) {
            throw new IOException("Ошибка ввода-вывода при записи файла: " + filePath, e);
        }
    }

    private static void encryptText(Scanner scanner) {
        try {
            System.out.print("Введите путь к исходному файлу: ");
            String inputFile = scanner.nextLine().trim();

            if (!validateFile(inputFile)) return;

            String text;
            try {
                text = readFile(inputFile);
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
                return;
            }

            if (text.trim().isEmpty()) {
                System.out.println("Ошибка: файл не содержит текста");
                return;
            }

            String language = detectLanguage(text);

            System.out.print("Введите путь для зашифрованного файла: ");
            String outputFile = scanner.nextLine().trim();

            if (!validateOutputPath(outputFile)) return;

            System.out.print("Введите ключ шифрования: ");
            int key;
            try {
                key = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ключ должен быть числом");
                return;
            }

            if (!validateKey(key, language)) return;

            String encryptedText = processText(text, key, true);

            try {
                writeFile(outputFile, encryptedText);
                System.out.println("Успех: текст зашифрован и сохранен в " + outputFile);
            } catch (IOException e) {
                System.out.println("Ошибка при сохранении файла: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Неожиданная ошибка при шифровании: " + e.getMessage());
        }
    }

    private static void decryptWithKey(Scanner scanner) {
        try {
            System.out.print("Введите путь к зашифрованному файлу: ");
            String inputFile = scanner.nextLine().trim();

            if (!validateFile(inputFile)) return;

            String text;
            try {
                text = readFile(inputFile);
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
                return;
            }

            String language = detectLanguage(text);

            System.out.print("Введите путь для расшифрованного файла: ");
            String outputFile = scanner.nextLine().trim();

            if (!validateOutputPath(outputFile)) return;

            System.out.print("Введите ключ шифрования: ");
            int key;
            try {
                key = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ключ должен быть числом");
                return;
            }

            if (!validateKey(key, language)) return;

            String decryptedText = processText(text, key, false);

            try {
                writeFile(outputFile, decryptedText);
                System.out.println("Успех: текст расшифрован и сохранен в " + outputFile);
            } catch (IOException e) {
                System.out.println("Ошибка при сохранении файла: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Неожиданная ошибка при расшифровке: " + e.getMessage());
        }
    }

    private static void bruteForceDecrypt(Scanner scanner) {
        try {
            System.out.print("Введите путь к зашифрованному файлу: ");
            String inputFile = scanner.nextLine().trim();

            if (!validateFile(inputFile)) return;

            String encryptedText;
            try {
                encryptedText = readFile(inputFile);
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
                return;
            }

            String language = detectLanguage(encryptedText);
            int maxKey = language.equals("russian") ? RUSSIAN_LOWER.length - 1 : ENGLISH_LOWER.length - 1;

            System.out.print("Введите папку для результатов: ");
            String outputDir = scanner.nextLine().trim();

            if (outputDir.isEmpty()) {
                System.out.println("Ошибка: путь к папке не может быть пустым");
                return;
            }

            File dir = new File(outputDir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    System.out.println("Ошибка: не удалось создать папку " + outputDir);
                    return;
                }
            }

            if (!dir.canWrite()) {
                System.out.println("Ошибка: нет прав на запись в папку " + outputDir);
                return;
            }

            System.out.println("Начинаю перебор ключей от 1 до " + maxKey + "...");
            int successCount = 0;

            for (int key = 1; key <= maxKey; key++) {
                try {
                    String decryptedText = processText(encryptedText, key, false);
                    String outputFile = outputDir + File.separator + "key_" + key + ".txt";
                    writeFile(outputFile, decryptedText);
                    successCount++;
                    System.out.println("Ключ " + key + " - обработан");
                } catch (IOException e) {
                    System.out.println("Ошибка при сохранении ключа " + key + ": " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Неожиданная ошибка при обработке ключа " + key + ": " + e.getMessage());
                }
            }

            System.out.println("Перебор завершен! Успешно обработано: " + successCount + " из " + maxKey + " ключей");
            System.out.println("Результаты в папке: " + outputDir);

        } catch (Exception e) {
            System.out.println("Неожиданная ошибка при переборе ключей: " + e.getMessage());
        }
    }
}
