package collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tag.Tag;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TagsCollectorImplTest {

    TagsCollector<Integer> collector;
    @BeforeEach
    void setUp() {
        collector = new TagsCollectorImpl<>();
    }

    @Test
    void testGetElements() {
        collector.addKeyTags(1,  Stream.empty());
        collector.addKeyTags(10,  Stream.empty());
        collector.addKeyTags(3,  Stream.empty());
        assertEquals(3, collector.getKeys().count());
    }

    @Test
    void testAddElement() {
        assertDoesNotThrow(() -> collector.addKeyTags(1,  Stream.empty()));
    }

    @Test
    void testRemoveElement() {
        collector.addKeyTags(1,  Stream.of(new Tag("A")));
        collector.addKeyTags(10,  Stream.empty());
        collector.addKeyTags(3,Stream.of(new Tag("A")));
        assertEquals(3, collector.getKeys().count());
        assertEquals(2, collector.getKeysWithTag(new Tag("A")).count());
        collector.removeKey(10);
        collector.removeKey(3);
        assertEquals(1, collector.getKeys().count());
        assertEquals(1, collector.getKeysWithTag(new Tag("A")).count());
    }

    @Test
    void testGetElementsByTagSuccess() {
        collector.addKeyTags(1,  Stream.of(new Tag("A")));
        collector.addKeyTags(10,  Stream.empty());
        collector.addKeyTags(3,  Stream.of(new Tag("A")));
        assertEquals(2, collector.getKeysWithTag(new Tag("A")).count());
    }

    @Test
    void testGetElementsByTagEmptySuccess() {
        collector.addKeyTags(1,  Stream.of(new Tag("A")));
        collector.addKeyTags(10,  Stream.empty());
        collector.addKeyTags(3,  Stream.of(new Tag("A")));
        assertEquals(0, collector.getKeysWithTag(new Tag("B")).count());
    }


    @Test
    void testAddIllegalState() {
        collector.addKeyTags(1,  Stream.of(new Tag("A")));
        assertThrows(IllegalStateException.class, () -> collector.addKeyTags(1,  Stream.of(new Tag("A"))));
    }

}