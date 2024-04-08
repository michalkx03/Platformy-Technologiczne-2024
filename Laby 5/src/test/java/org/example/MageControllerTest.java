package org.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MageControllerTest {

    private MageController controller;

    @Mock
    private MageRepository mock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MageController(mock);
    }

    @Test
    void testDelete_Delete() {
        when(mock.find("Mage")).thenReturn(Optional.of(new Mage("Mage", 10)));
        assertThat(controller.delete("Mage")).isEqualTo("done");
        verify(mock, times(1)).delete("Mage");
    }

    @Test
    void testDelete_NotFound() {
        doThrow(new IllegalArgumentException()).when(mock).delete("Mage");
        assertThat(controller.delete("Mage")).isEqualTo("not found");
    }

    @Test
    void testFind_NotFound() {
        when(mock.find("Mage")).thenReturn(Optional.empty());
        assertThat(controller.find("Mage")).isEqualTo("not found");
    }

    @Test
    void testFind_Found() {
        Mage mage = new Mage("Mage1", 10);
        when(mock.find("Mage1")).thenReturn(Optional.of(mage));
        assertThat(controller.find("Mage1")).isEqualTo(mage.toString());
    }

    @Test
    void testSave_ValidInput() {
        assertThat(controller.save("Mage", 10)).isEqualTo("done");
        verify(mock, times(1)).save(new Mage("Mage", 10));
    }

    @Test
    void testSave_Duplicate() {
        doThrow(new IllegalArgumentException()).when(mock).save(new Mage("Mage", 10));
        assertThat(controller.save("Mage", 10)).isEqualTo("bad request");
    }
}
