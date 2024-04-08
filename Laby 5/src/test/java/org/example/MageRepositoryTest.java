package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MageRepositoryTest {

    private MageRepository rep;

    @BeforeEach
    void setUp() {
        rep = new MageRepository();
    }

    @Test
    void testDelete_NotFound() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rep.delete("Mage"));
    }

    @Test
    void testFind_NotFound() {
        Optional<Mage> foundMage = rep.find("Mage");
        assertThat(foundMage).isEmpty();
    }

    @Test
    void testSave_AlreadyExists() {
        Mage mage1 = new Mage("Mage1", 10);
        Mage mage2 = new Mage("Mage1", 20);
        rep.save(mage1);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rep.save(mage2))
                .withMessage("Mage already exists");
    }
}