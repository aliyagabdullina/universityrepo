package tag;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Tag {
    private final static Predicate<String> _predicate = Pattern.compile("\\w+").asPredicate();
    private final String _tagContent;

    public Tag(String tag) {
        if(_predicate.test(tag)) {
            _tagContent = tag;
        } else throw new IllegalArgumentException("Incorrect tag: " + tag);
    }

    @Override
    public String toString() {
        return "#" + _tagContent;
    }

    public static Stream<Tag> extractTags(String string) {
        String[] tags = string.split("#");
        return Arrays.stream(tags)
                .filter(str -> !str.isBlank())
                .map(String::trim)
                .map(Tag::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(_tagContent, tag._tagContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_tagContent);
    }
}
