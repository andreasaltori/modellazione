package it.unicam.cs.mpgc.rpg122627.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lettura e scrittura JSON minimale, senza librerie esterne. I valori sono rappresentati con i
 * tipi standard: {@code Map<String,Object>}, {@code List<Object>}, {@code String}, {@code Long},
 * {@code Double}, {@code Boolean} e {@code null}.
 */
public final class Json {

    private Json() {
    }

    // ---- lettura --------------------------------------------------------------------------

    /** Interpreta un testo JSON. Lancia {@link IllegalArgumentException} se non è valido. */
    public static Object parse(String text) {
        Parser parser = new Parser(text);
        Object value = parser.readValue();
        parser.skipWhitespace();
        if (!parser.atEnd()) {
            throw parser.error("contenuto inatteso dopo il valore JSON");
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> object(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new IllegalArgumentException("Atteso un oggetto JSON");
    }

    @SuppressWarnings("unchecked")
    public static List<Object> array(Object value) {
        if (value instanceof List<?> list) {
            return (List<Object>) list;
        }
        throw new IllegalArgumentException("Atteso un array JSON");
    }

    /** Stringa obbligatoria. */
    public static String string(Map<String, Object> object, String key) {
        Object value = object.get(key);
        if (value instanceof String text) {
            return text;
        }
        throw new IllegalArgumentException("Campo testuale mancante o non valido: " + key);
    }

    /** Stringa facoltativa, {@code null} se assente. */
    public static String optionalString(Map<String, Object> object, String key) {
        Object value = object.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String text) {
            return text;
        }
        throw new IllegalArgumentException("Campo testuale non valido: " + key);
    }

    /** Intero obbligatorio. */
    public static int integer(Map<String, Object> object, String key) {
        if (object.get(key) instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalArgumentException("Campo numerico mancante o non valido: " + key);
    }

    /** Array facoltativo, vuoto se assente. */
    public static List<Object> arrayOrEmpty(Map<String, Object> object, String key) {
        return object.containsKey(key) ? array(object.get(key)) : List.of();
    }

    /** Array di stringhe facoltativo. */
    public static List<String> strings(Map<String, Object> object, String key) {
        List<String> result = new ArrayList<>();
        for (Object item : arrayOrEmpty(object, key)) {
            if (!(item instanceof String text)) {
                throw new IllegalArgumentException("Atteso un array di stringhe: " + key);
            }
            result.add(text);
        }
        return result;
    }

    // ---- scrittura ------------------------------------------------------------------------

    /** Serializza un valore con indentazione leggibile. */
    public static String write(Object value) {
        StringBuilder out = new StringBuilder();
        writeValue(out, value, 0);
        return out.append('\n').toString();
    }

    private static void writeValue(StringBuilder out, Object value, int depth) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String text) {
            writeString(out, text);
        } else if (value instanceof Boolean || value instanceof Integer || value instanceof Long) {
            out.append(value);
        } else if (value instanceof Double number) {
            if (number.isNaN() || number.isInfinite()) {
                throw new IllegalArgumentException("Numero non rappresentabile in JSON");
            }
            out.append(number);
        } else if (value instanceof Map<?, ?> map) {
            writeObject(out, map, depth);
        } else if (value instanceof List<?> list) {
            writeArray(out, list, depth);
        } else {
            throw new IllegalArgumentException("Tipo non serializzabile: " + value.getClass().getName());
        }
    }

    private static void writeObject(StringBuilder out, Map<?, ?> map, int depth) {
        if (map.isEmpty()) {
            out.append("{}");
            return;
        }
        out.append("{\n");
        int index = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            indent(out, depth + 1);
            writeString(out, String.valueOf(entry.getKey()));
            out.append(": ");
            writeValue(out, entry.getValue(), depth + 1);
            out.append(++index < map.size() ? ",\n" : "\n");
        }
        indent(out, depth);
        out.append('}');
    }

    private static void writeArray(StringBuilder out, List<?> list, int depth) {
        if (list.isEmpty()) {
            out.append("[]");
            return;
        }
        out.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            indent(out, depth + 1);
            writeValue(out, list.get(i), depth + 1);
            out.append(i < list.size() - 1 ? ",\n" : "\n");
        }
        indent(out, depth);
        out.append(']');
    }

    private static void indent(StringBuilder out, int depth) {
        out.append("  ".repeat(depth));
    }

    private static void writeString(StringBuilder out, String text) {
        out.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }

    // ---- parser ---------------------------------------------------------------------------

    private static final class Parser {
        private final String text;
        private int position;

        Parser(String text) {
            this.text = text;
        }

        boolean atEnd() {
            return position >= text.length();
        }

        void skipWhitespace() {
            while (!atEnd() && Character.isWhitespace(text.charAt(position))) {
                position++;
            }
        }

        IllegalArgumentException error(String message) {
            return new IllegalArgumentException("JSON non valido alla posizione " + position + ": " + message);
        }

        Object readValue() {
            skipWhitespace();
            if (atEnd()) {
                throw error("fine inattesa");
            }
            char c = text.charAt(position);
            return switch (c) {
                case '{' -> readObject();
                case '[' -> readArray();
                case '"' -> readString();
                case 't' -> readLiteral("true", Boolean.TRUE);
                case 'f' -> readLiteral("false", Boolean.FALSE);
                case 'n' -> readLiteral("null", null);
                default -> readNumber();
            };
        }

        private Object readLiteral(String literal, Object value) {
            if (!text.startsWith(literal, position)) {
                throw error("atteso " + literal);
            }
            position += literal.length();
            return value;
        }

        private Map<String, Object> readObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            position++;
            skipWhitespace();
            if (peek() == '}') {
                position++;
                return map;
            }
            while (true) {
                skipWhitespace();
                if (peek() != '"') {
                    throw error("atteso il nome di un campo");
                }
                String key = readString();
                skipWhitespace();
                expect(':');
                map.put(key, readValue());
                skipWhitespace();
                if (peek() == ',') {
                    position++;
                } else {
                    expect('}');
                    return map;
                }
            }
        }

        private List<Object> readArray() {
            List<Object> list = new ArrayList<>();
            position++;
            skipWhitespace();
            if (peek() == ']') {
                position++;
                return list;
            }
            while (true) {
                list.add(readValue());
                skipWhitespace();
                if (peek() == ',') {
                    position++;
                } else {
                    expect(']');
                    return list;
                }
            }
        }

        private String readString() {
            StringBuilder out = new StringBuilder();
            position++;
            while (true) {
                if (atEnd()) {
                    throw error("stringa non terminata");
                }
                char c = text.charAt(position++);
                if (c == '"') {
                    return out.toString();
                }
                if (c != '\\') {
                    out.append(c);
                    continue;
                }
                if (atEnd()) {
                    throw error("sequenza di escape incompleta");
                }
                char escaped = text.charAt(position++);
                switch (escaped) {
                    case '"', '\\', '/' -> out.append(escaped);
                    case 'b' -> out.append('\b');
                    case 'f' -> out.append('\f');
                    case 'n' -> out.append('\n');
                    case 'r' -> out.append('\r');
                    case 't' -> out.append('\t');
                    case 'u' -> out.append(readUnicode());
                    default -> throw error("escape sconosciuto: \\" + escaped);
                }
            }
        }

        private char readUnicode() {
            if (position + 4 > text.length()) {
                throw error("sequenza \\u incompleta");
            }
            try {
                char c = (char) Integer.parseInt(text.substring(position, position + 4), 16);
                position += 4;
                return c;
            } catch (NumberFormatException e) {
                throw error("sequenza \\u non valida");
            }
        }

        private Object readNumber() {
            int start = position;
            while (!atEnd() && "+-0123456789.eE".indexOf(text.charAt(position)) >= 0) {
                position++;
            }
            String token = text.substring(start, position);
            if (token.isEmpty()) {
                throw error("valore inatteso");
            }
            try {
                boolean decimal = token.contains(".") || token.contains("e") || token.contains("E");
                return decimal ? (Object) Double.valueOf(token) : (Object) Long.valueOf(token);
            } catch (NumberFormatException e) {
                throw error("numero non valido: " + token);
            }
        }

        private char peek() {
            if (atEnd()) {
                throw error("fine inattesa");
            }
            return text.charAt(position);
        }

        private void expect(char expected) {
            if (peek() != expected) {
                throw error("atteso '" + expected + "'");
            }
            position++;
        }
    }
}
