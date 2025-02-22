package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pair.Pair;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class CplexSolutionReader {
    public static Stream<Pair<String, Double>> extractNonZeroVarValues(File file) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            DtoCplexSolution solution = xmlMapper.readValue(file, DtoCplexSolution.class);
            return solution.variables.stream()
                    .map(dtoVariable -> new Pair<>(dtoVariable.name, dtoVariable.value))
                    .filter(pair -> !pair.getValue().equals(0.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
