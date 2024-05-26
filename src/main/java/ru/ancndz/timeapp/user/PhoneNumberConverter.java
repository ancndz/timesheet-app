package ru.ancndz.timeapp.user;

/**
 * Класс для преобразования номера телефона.
 *
 * @author Anton Utkaev
 * @since 2024.05.01
 */
public class PhoneNumberConverter {

    public static String convertPhoneNumber(final String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        if (phoneNumber.length() != 12) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        return String.format("%s(%s)%s-%s-%s",
                phoneNumber.substring(0, 2),
                phoneNumber.substring(2, 5),
                phoneNumber.substring(5, 8),
                phoneNumber.substring(8, 10),
                phoneNumber.substring(10, 12));
    }

    public static boolean isValidPhoneNumber(final String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() == 12;
    }

}
