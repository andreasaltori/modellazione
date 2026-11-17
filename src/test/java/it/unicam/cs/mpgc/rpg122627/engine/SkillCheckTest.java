package it.unicam.cs.mpgc.rpg122627.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillCheckTest {

    @Test
    void totalIsRollPlusBonus() {
        SkillCheck check = SkillCheck.attempt(sides -> 10, 3, 12);
        assertEquals(13, check.total());
        assertTrue(check.success());
    }

    @Test
    void meetingTheDifficultyExactlyIsASuccess() {
        assertTrue(new SkillCheck(9, 3, 12).success());
    }

    @Test
    void belowTheDifficultyIsAFailure() {
        assertFalse(new SkillCheck(8, 3, 12).success());
    }

    @Test
    void randomDiceStaysInRange() {
        RandomDice dice = new RandomDice();
        for (int i = 0; i < 500; i++) {
            int value = dice.roll(20);
            assertTrue(value >= 1 && value <= 20);
        }
    }
}
