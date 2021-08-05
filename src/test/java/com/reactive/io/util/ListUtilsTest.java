package com.reactive.io.util;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.reactive.io.util.ListUtils.toSingleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ListUtilsTest {

    @Test
    public void shouldCollectSingleItem() {
        List<String> list = Collections.singletonList("Test");
        String result = list.stream().collect(toSingleton());
        assertEquals(result, list.get(0));
    }

    @Test
    public void shouldCollectSingleItemFail() {
        List<String> list = Arrays.asList("Test", "Test1");
        assertThrows(IllegalStateException.class, ()-> list.stream().collect(toSingleton()));

        assertThrows(IllegalStateException.class, ()-> Collections.emptyList().stream().collect(toSingleton()));
    }
}