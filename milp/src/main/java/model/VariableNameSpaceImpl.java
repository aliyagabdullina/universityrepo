package model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VariableNameSpaceImpl implements VariableNameSpace {
    private final Map<String, Integer> _typeNameNumDimMap = new ConcurrentHashMap<>();
    private int _nextAuxId = 0;

    @Override
    public void registerVariableName(String key, int numDimensions) {
        _typeNameNumDimMap.put(key, numDimensions);
    }

    @Override
    public String getVariableName(String key, List<String> indices) {
        if(_typeNameNumDimMap.containsKey(key)) {
            if(_typeNameNumDimMap.get(key) != indices.size()) {
                throw new IllegalArgumentException("Incorrect size of indices list: " + indices.size() + " when expected " + _typeNameNumDimMap.get(key));
            }
            StringBuilder result = new StringBuilder(key + "_{");
            for (int i=0; i < indices.size(); i++) {
                if(i > 0) {
                    result.append(",");
                }
                result.append(indices.get(i));
            }
            result.append("}");
            return result.toString();
        }

        throw new IllegalStateException("Undefined variable key: " + key);
    }

    @Override
    public List<String> getVariableIndices(String varName) {
        int startTokens = varName.indexOf("_{") + 2;
        String subString = varName.substring(startTokens,varName.length()-1);
        return Arrays.stream(subString.split(","))
                .sequential()
                .toList();
    }

    @Override
    public synchronized String getNextAuxVarName() {
        return "AUX_{" + getNextAuxId() +"}";
    }

    @Override
    public boolean ifLabeledBy(String varName, String key) {
        int nameLength = varName.indexOf("_{");
        return varName.substring(0, nameLength).equals(key);
    }

    private synchronized int getNextAuxId() {
        int result = _nextAuxId;
        _nextAuxId++;
        return result;
    }
}
