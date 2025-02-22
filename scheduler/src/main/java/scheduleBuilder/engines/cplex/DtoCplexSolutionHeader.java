package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DtoCplexSolutionHeader {
    @JacksonXmlProperty
   public String problemName;
    @JacksonXmlProperty
    public String solutionName;
    @JacksonXmlProperty
    public String solutionIndex;
    @JacksonXmlProperty
    public String objectiveValue;
    @JacksonXmlProperty
    public String solutionTypeValue;
    @JacksonXmlProperty
    public String solutionTypeString;
    @JacksonXmlProperty
    public String solutionStatusValue;
    @JacksonXmlProperty
    public String solutionStatusString;
    @JacksonXmlProperty
    public String solutionMethodString;
    @JacksonXmlProperty
    public String primalFeasible;
    @JacksonXmlProperty
    public String dualFeasible;
    @JacksonXmlProperty
    public String MIPNodes;
    @JacksonXmlProperty
    public String MIPIterations;
    @JacksonXmlProperty
    public String writeLevel;
}
