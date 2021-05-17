package bfst21;

import bfst21.search.RadixTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RadixTreeTest {
    @Test
    public void newRadixTree() {
        RadixTree tree = new RadixTree();
        assertEquals(1, tree.getSize());
        assertEquals(0, tree.getPlaces());
    }

    @Test
    public void insertNodeCase1() {
        RadixTree tree = new RadixTree();
        tree.insert("test", 1);
        assertEquals(2, tree.getSize());
        assertEquals(1, tree.getPlaces());
        assertEquals("test", tree.lookupNode("test").getFullName());
    }

    @Test
    public void insertNodeCase2() {
        RadixTree tree = new RadixTree();
        tree.insert("test", 1);
        tree.insert("tester", 2);
        assertEquals(3, tree.getSize());
        assertEquals(2, tree.getPlaces());
        assertEquals("test", tree.lookupNode("test").getFullName());
        assertEquals("tester", tree.lookupNode("tester").getFullName());
        assertEquals("er", tree.lookupNode("tester").getValue());
    }

    @Test
    public void insertNodeCase3() {
        RadixTree tree = new RadixTree();
        tree.insert("roadkill", 1);
        tree.insert("road", 2);
        assertEquals(3, tree.getSize());
        assertEquals(2, tree.getPlaces());
        assertEquals("road", tree.lookupNode("road").getFullName());
        assertEquals("roadkill", tree.lookupNode("roadkill").getFullName());
        assertEquals("kill", tree.lookupNode("roadkill").getValue());
    }

    @Test
    public void insertNodeCase4() {
        RadixTree tree = new RadixTree();
        tree.insert("test", 1);
        tree.insert("team", 2);
        assertEquals(4, tree.getSize());
        assertEquals(2, tree.getPlaces());
        assertEquals("test", tree.lookupNode("test").getFullName());
        assertEquals("team", tree.lookupNode("team").getFullName());
        assertEquals("st", tree.lookupNode("test").getValue());
        assertEquals("am", tree.lookupNode("team").getValue());
    }

    @Test
    public void insertNodeCase5() {
        RadixTree tree = new RadixTree();
        tree.insert("test", 1);
        tree.insert("hammock", 2);
        tree.insert("road", 3);
        assertEquals(4, tree.getSize());
        assertEquals(3, tree.getPlaces());
        assertEquals("test", tree.lookupNode("test").getFullName());
        assertEquals("hammock", tree.lookupNode("hammock").getFullName());
        assertEquals("road", tree.lookupNode("road").getFullName());
        assertEquals("test", tree.lookupNode("test").getValue());
        assertEquals("hammock", tree.lookupNode("hammock").getValue());
        assertEquals("road", tree.lookupNode("road").getValue());
    }

    @Test
    public void suggestions() {
        RadixTree tree = new RadixTree();
        tree.insert("Test", 1);
        tree.insert("Team", 2);
        tree.insert("Tester", 3);
        tree.insert("Testing", 4);
        tree.insert("Tested", 5);
        assertEquals(8, tree.getSize());
        assertEquals(5, tree.getPlaces());

        assertEquals(4, tree.getSuggestions("tes").size());

    }
}
