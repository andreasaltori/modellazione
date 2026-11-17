package it.unicam.cs.mpgc.rpg122627.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvestigatorTest {

    @Test
    void newInvestigatorHasBaseSkillsAndSpecialtyBonus() {
        Investigator hero = Investigator.create("Anna", Skill.EMPATHY);
        assertEquals(Investigator.BASE_SKILL + Investigator.SPECIALTY_BONUS, hero.skill(Skill.EMPATHY));
        assertEquals(Investigator.BASE_SKILL, hero.skill(Skill.LOGIC));
        assertEquals(1, hero.level());
    }

    @Test
    void experienceBelowThresholdDoesNotLevelUp() {
        Investigator hero = Investigator.create("Anna", Skill.LOGIC);
        assertFalse(hero.gainExperience(Skill.LOGIC, 4));
        assertEquals(1, hero.level());
        assertEquals(4, hero.experience());
    }

    @Test
    void levelUpImprovesTheUsedSkillAndKeepsLeftoverExperience() {
        Investigator hero = Investigator.create("Anna", Skill.LOGIC);
        int before = hero.skill(Skill.PERCEPTION);
        assertTrue(hero.gainExperience(Skill.PERCEPTION, 7));
        assertEquals(2, hero.level());
        assertEquals(2, hero.experience());
        assertEquals(before + 1, hero.skill(Skill.PERCEPTION));
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Investigator.create("  ", Skill.LOGIC));
    }

    @Test
    void negativeExperienceIsRejected() {
        Investigator hero = Investigator.create("Anna", Skill.LOGIC);
        assertThrows(IllegalArgumentException.class, () -> hero.gainExperience(Skill.LOGIC, -1));
    }
}
