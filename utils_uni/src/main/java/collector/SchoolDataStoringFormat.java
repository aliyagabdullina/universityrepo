package collector;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public enum SchoolDataStoringFormat {
    JSON {
        @Override
        public boolean ifPathIsOk(Path path) {
            PathMatcher matcher =
                    FileSystems.getDefault().getPathMatcher("glob:**.{json, json5}");
            boolean result = matcher.matches(path);
            return result;
        };
    };

    public abstract boolean ifPathIsOk(Path path);
}
