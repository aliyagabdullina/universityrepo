package model;

import java.util.List;

public interface VariableNameSpace {
    void registerVariableName(String key, int numDimensions);
    String getVariableName(String key, List<String> indices);
    List<String> getVariableIndices(String varName);
    String getNextAuxVarName();
    boolean ifLabeledBy(String varName, String key);
}
