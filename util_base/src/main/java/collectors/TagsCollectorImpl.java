package collectors;

import tag.Tag;

import java.security.Key;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagsCollectorImpl<K> implements TagsCollector<K> {
    private Map<Tag, Set<K>> _tagKeyMap = new ConcurrentHashMap<>();
    private Map<K, Set<Tag>> _keyTagMap = new ConcurrentHashMap<>();

    @Override
    public Stream<K> getKeys() {
        return _keyTagMap.keySet().stream();
    }

    @Override
    public void addKeyTags(K key, Stream<Tag> tagStream) {
        if(_keyTagMap.containsKey(key)) {
            throw new IllegalStateException("Key: " + key + " already defined in the scope!");
        }
        Set<Tag> tagSet  = tagStream.collect(Collectors.toUnmodifiableSet());
        _keyTagMap.put(key, tagSet);
        tagSet.forEach(tag -> addTagAndKeyToTagMap(tag, key));
    }

    private void addTagAndKeyToTagMap(Tag tag, final K key) {
        _tagKeyMap.putIfAbsent(tag, new HashSet<>());
        _tagKeyMap.get(tag).add(key);
    }

    @Override
    public void removeKey(K key) {
        if(!_keyTagMap.containsKey(key)) {
            return;
        }
        Set<Tag> tagsForKey = _keyTagMap.get(key);
        _keyTagMap.remove(key);
        tagsForKey.forEach(tag -> _tagKeyMap.get(tag).remove(key));
    }

    @Override
    public Stream<K> getKeysWithTag(Tag tag) {
        Set<K>  keys = _tagKeyMap.get(tag);
        return keys != null ? keys.stream() : Stream.empty();
    }

    @Override
    public Stream<Tag> getTagsForKey(K key) {
        Set<Tag> tags = _keyTagMap.get(key);
        return tags != null ? tags.stream() : Stream.empty();
    }
}
