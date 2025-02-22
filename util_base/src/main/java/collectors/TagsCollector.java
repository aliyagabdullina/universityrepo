package collectors;

import tag.Tag;

import java.util.stream.Stream;

public interface TagsCollector<K> {
    Stream<K> getKeys();
    void addKeyTags(K key, Stream<Tag> tagStream) throws IllegalStateException;
    void removeKey(K key);
    Stream<K> getKeysWithTag(Tag tag);
    Stream<Tag> getTagsForKey(K key);
}
