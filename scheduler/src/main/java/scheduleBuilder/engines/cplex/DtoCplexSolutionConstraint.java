package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DtoCplexSolutionConstraint {
    @JacksonXmlProperty
    public String name;
    @JacksonXmlProperty
    public String index;
    @JacksonXmlProperty
    public String slack;
}
