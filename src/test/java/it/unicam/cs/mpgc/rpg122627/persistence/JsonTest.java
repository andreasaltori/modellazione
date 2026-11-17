package it.unicam.cs.mpgc.rpg122627.persistence;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonTest {

    @Test
    void parsesNestedStructures() {
        Map<String, Object> object = Json.object(Json.parse(
                "{\"a\": 1, \"b\": [true, null, \"x\"], \"c\": {\"d\": 2.5}}"));
        assertEquals(1L, object.get("a"));
        assertEquals(2.5, Json.object(object.get("c")).get("d"));
        assertEquals(3, Json.array(object.get("b")).size());
    }

    @Test
    void roundTripPreservesSpecialCharacters() {
        Map<String, Object> original = new LinkedHashMap<>();
        original.put("text", "riga1\nriga2 \"citazione\" \\ tab\t «è» ù");
        original.put("list", List.of("a", "b"));
        original.put("number", 42);
        Map<String, Object> copy = Json.object(Json.parse(Json.write(original)));
        assertEquals(original.get("text"), copy.get("text"));
        assertEquals(List.of("a", "b"), copy.get("list"));
        assertEquals(42L, copy.get("number"));
    }

    @Test
    void parsesUnicodeEscapes() {
        assertEquals("è", Json.parse("\"\\u00e8\""));
    }

    @Test
    void emptyContainersAreSupported() {
        assertEquals(0, Json.object(Json.parse("{}")).size());
        assertEquals(0, Json.array(Json.parse("[ ]")).size());
    }

    @Test
    void malformedInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Json.parse("{\"a\": }"));
        assertThrows(IllegalArgumentException.class, () -> Json.parse("[1, 2"));
        assertThrows(IllegalArgumentException.class, () -> Json.parse("\"non chiusa"));
        assertThrows(IllegalArgumentException.class, () -> Json.parse("{} extra"));
    }

    @Test
    void missingRequiredFieldIsReported() {
        Map<String, Object> object = Json.object(Json.parse("{\"a\": 1}"));
        assertThrows(IllegalArgumentException.class, () -> Json.string(object, "a"));
        assertThrows(IllegalArgumentException.class, () -> Json.integer(object, "b"));
    }
}
