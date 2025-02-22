package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DtoCplexSolutionQuality {
    @JacksonXmlProperty
    public String epInt;
    @JacksonXmlProperty
    public String epRHS;
    @JacksonXmlProperty
    public String maxIntInfeas;
    @JacksonXmlProperty
    public String maxPrimalInfeas;
    @JacksonXmlProperty
    public String maxX;
    @JacksonXmlProperty
    public String maxSlack;
}
