package formatters;

import java.time.LocalDate;

public final class DateFormatter {

    private DateFormatter() {} // impede instanciação

    public static LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        if (!input.matches("\\d{2}/\\d{2}/\\d{4}")){
            return null;
        }

        int day   = Integer.parseInt(input.substring(0, 2));
        int month = Integer.parseInt(input.substring(3, 5));
        int year  = Integer.parseInt(input.substring(6, 10));

        if (month < 1 || month > 12){
            return null;
        }
        if (day < 1 || day > 31) {
            return null;
        }

        return LocalDate.of(year, month, day);
    }

    public static String cleanNumber(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", "");
    }
}