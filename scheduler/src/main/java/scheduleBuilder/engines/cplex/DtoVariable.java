package scheduleBuilder.engines.cplex;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "variables")
public class DtoVariable {
    public String name;
    public int index;
    public double value;
}
