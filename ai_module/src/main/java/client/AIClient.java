package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AIClient {
    public static String runPythonScript(String input) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python", "ai.py", input);
        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}