package scheduleBuilder.engines.milp.orTools;



import model.MilpModel;
import scheduleBuilder.engines.SchedulingEngineInput;
import scheduleBuilder.engines.SchedulingEngineSettings;

public interface MilpOrToolsInput extends SchedulingEngineInput<MilpOrToolsApi> {
    MilpModel getModel();
    MilpOrToolsSettings getSettings();
}
