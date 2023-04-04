package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyObjectTest {

    @Test
    public void testMyObjectCreation() {
        MyObject myObject = new MyObject("group1", "type1", 1L, 10L);
        assertEquals("group1", myObject.getGroup());
        assertEquals("type1", myObject.getType());
        assertEquals(1L, myObject.getNumber());
        assertEquals(10L, myObject.getWeight());
    }

    @Test
    public void testMyObjectToString() {
        MyObject myObject = new MyObject("group1", "type1", 1L, 10L);
        String expectedString = "MyObject{group='group1', type='type1', number=1, weight=10}";
        assertEquals(expectedString, myObject.toString());
    }
}
