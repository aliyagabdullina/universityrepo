package scheduleBuilder.engines.milp.orTools;

import model.MilpModel;
import scheduleBuilder.engines.SchedulingEngineSettings;

public class MilpOrToolsInputImpl implements MilpOrToolsInput {
    private final MilpModel _model;
    private final MilpOrToolsSettings _settings;
    public MilpOrToolsInputImpl(MilpModel model, MilpOrToolsSettings settings) {
        _model = model;
        _settings = settings;
    }

    @Override
    public MilpOrToolsSettings getSettings() {
        return _settings;
    }

    @Override
    public MilpModel getModel() {
        return _model;
    }
}
