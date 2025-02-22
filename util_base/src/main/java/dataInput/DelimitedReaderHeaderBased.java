package dataInput;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DelimitedReaderHeaderBased<T> implements DelimitedReader<T> {
    private final String _delimiter;
    private final List<String> _etalonHeaderSequence;
    private final Function<List<String>, T> _toObjectMapper;

    public DelimitedReaderHeaderBased(String delimiter, List<String> etalonHeaderSequence, Function<List<String>, T> toObjectMapper) {
        _delimiter = delimiter;
        _etalonHeaderSequence = etalonHeaderSequence;
        _toObjectMapper = toObjectMapper;
    }

    @Override
    public List<T> read(File file) {
        String header = readHeader(file);
        if(header == null) {
            return new ArrayList<>();
        }
        List<String> headerArray = Arrays.stream(header.split(_delimiter)).sequential().toList();

        Function<String[], T> mapToObj = createArrayToObject(headerArray);
        try (Stream<String> linesStream = Files.lines(file.toPath())) {
            return linesStream
                    .sequential()
                    .skip(1)
                    .map(str -> str.split(_delimiter))
                    .map(mapToObj)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readHeader(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String result = reader.readLine();
            reader.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Function<String[], T> createArrayToObject(List<String> headerSequence) {
        if(headerSequence.size() != _etalonHeaderSequence.size()) {
            throw new IllegalArgumentException("Incorrect header size! Expected: " + _etalonHeaderSequence.size() + " Actual: " + headerSequence.size());
        }

        final List<Integer> currentHeadersIndicesOrder = _etalonHeaderSequence
                .stream()
                .map(o -> {
                    var result = headerSequence.indexOf(o);
                    if(result == -1) {
                        boolean test = headerSequence.get(0).equals("name");
                        throw new IllegalArgumentException("Header " + o + " not found");
                    }
                    return headerSequence.indexOf(o);
                })
                .toList();
        return tokens -> {
            List<String> reorderedTokens = currentHeadersIndicesOrder
                    .stream()
                    .map(i -> tokens[i])
                    .map(String::trim)
                    .toList();
            return _toObjectMapper.apply(reorderedTokens);
        };
    }



}
