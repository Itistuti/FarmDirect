package agriconnect.farming.auth;

import java.util.Locale;

public final class JsonSupport {
    private JsonSupport() {
    }

    public static String authJson(User user) {
        return "{" +
                "\"authenticated\":true," +
                "\"id\":\"" + escape(user.getId().toString()) + "\"," +
                "\"fullName\":\"" + escape(user.getFullName()) + "\"," +
                "\"email\":\"" + escape(user.getEmail()) + "\"," +
                "\"role\":\"" + escape(user.getRole().name()) + "\"," +
                "\"location\":\"" + escape(user.getLocation() != null ? user.getLocation() : "") + "\"" +
                "}";
    }

    public static String messageJson(String message) {
        return "{\"message\":\"" + escape(message) + "\"}";
    }

    public static String roleMessageJson(User user, String message) {
        return "{" +
                "\"message\":\"" + escape(message) + "\"," +
                "\"role\":\"" + escape(user.getRole().name()) + "\"" +
                "}";
    }

    public static String errorJson(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(value.length() + 16);
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> {
                    if (c < 0x20) {
                        builder.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    } else {
                        builder.append(c);
                    }
                }
            }
        }
        return builder.toString();
    }
}

