package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;
@JacksonXmlRootElement(localName = "CPLEXSolution")
public class DtoCplexSolution {
    @JacksonXmlProperty
    String version;
    @JacksonXmlProperty
    DtoCplexSolutionHeader header;
    @JacksonXmlProperty
    DtoCplexSolutionQuality quality;
    @JacksonXmlProperty
    List<DtoCplexSolutionConstraint> linearConstraints;
    @JacksonXmlProperty
    List<DtoVariable> variables;
}
