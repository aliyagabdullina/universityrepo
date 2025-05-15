package scheduleBuilder.engines.milp.orTools;

import com.google.ortools.linearsolver.*;
import model.MilpModel;
import model.ObjectiveType;
import model.constraint.LinearConstraint;
import model.expressions.LinearExpression;
import model.variables.Variable;
import pair.Pair;
import scheduleBuilder.engines.cplex.CplexSolutionReader;
import solution.MilpSolutionImpl;
import solution.SolutionStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.stream.Stream;

public class MilpOrToolsRunnerImpl implements  MilpOrToolsRunner {
    private final MPSolver _mpSolver;
    private OptionalLong _timeLimitInMs = OptionalLong.empty();


    public MilpOrToolsRunnerImpl(MilpOrToolsInput input) {
        _mpSolver = initializeMpSolverSettings(input.getSettings());
        setUpModel(input.getModel());
    }

    private MPSolver initializeMpSolverSettings(MilpOrToolsSettings settings) {
        MPSolver.OptimizationProblemType solverType = settings.getSolverType();
        _timeLimitInMs = settings.getTimeLimitMs();
        MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.GUROBI_MIXED_INTEGER_PROGRAMMING);
        //MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING);
        //MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.XPRESS_MIXED_INTEGER_PROGRAMMING);
        //MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.BOP_INTEGER_PROGRAMMING);
        //MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.GLPK_MIXED_INTEGER_PROGRAMMING);
        //MPSolver mpSolver = new MPSolver("solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        return mpSolver;
    }


    private void setUpModel(MilpModel model) {
        model.getVariablesStream().forEach(this::addVariable);
        model.getConstraintsStream().forEach(this::addConstraint);
        model.getObjectiveExpression().ifPresent(objExpression -> addObjective(objExpression,model.getObjectiveType()));
    }

    @Override
    public MilpOrToolsResult run() {
        _mpSolver.enableOutput();
        MPSolverParameters parameters = createMpSolverParameters();
        //_timeLimitInMs.ifPresent(_mpSolver::setTimeLimit);
        _mpSolver.setTimeLimit(60000);
        //_mpSolver.setNumThreads(2);

        try {
            saveModelInMps();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MPSolver.ResultStatus resultStatus  = _mpSolver.solve(parameters);

        //if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            saveSolutionToFile();
        //}


        SolutionStatus solutionStatus = decodeResultStatus(resultStatus);
        Stream<Pair<String, Double>> varLabelValueStream = Arrays.stream(_mpSolver.variables())
                .map(mpVariable -> new Pair<>(mpVariable.name(), mpVariable.solutionValue()));
        double objective = _mpSolver.objective().value();


        MilpOrToolsResult result = exportFromCplexXML();
        /*
        SolutionStatus solutionStatus = decodeResultStatus(null);
        Stream<Pair<String, Double>> varLabelValueStream = Arrays.stream(_mpSolver.variables())
                .map(mpVariable -> new Pair<>(mpVariable.name(), mpVariable.solutionValue()));
        double objective = _mpSolver.objective().value();
        var solution = new MilpSolutionImpl(solutionStatus, varLabelValueStream, OptionalDouble.of(objective));
        return new MilpOrToolsResultImpl(solution);
         */
        return result;
    }

    private void saveSolutionToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/aliya/Documents/Курсач/AI_Scheduling/buf/buf_solution.sol"))) {
            for (MPVariable var : _mpSolver.variables()) {
                double value = var.solutionValue();
                if (Math.abs(value) > 1e-6) { // только значимые значения
                    writer.write(var.name() + " " + value);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save solution to file: " + "/Users/aliya/Documents/Курсач/AI_Scheduling/buf/buf_solution.sol", e);
        }
    }


    private MilpOrToolsResult exportFromCplexXML() {
        File file = new File("/Users/aliya/Documents/Курсач/AI_Scheduling/buf/buf_solution.sol");
        Stream<Pair<String, Double>> varLabelValueStream = CplexSolutionReader.extractNonZeroVarValues(file);

        SolutionStatus solutionStatus = SolutionStatus.FEASIBLE;
        var solution = new MilpSolutionImpl(solutionStatus, varLabelValueStream, OptionalDouble.of(999999));
        return new MilpOrToolsResultImpl(solution);
    }

    private void saveModelInMps() throws IOException {
        FileWriter fWriter = new FileWriter("/Users/aliya/Documents/Курсач/AI_Scheduling/buf/buf_lp.lp");
        BufferedWriter writer = new BufferedWriter(fWriter);
            writer.write(_mpSolver.exportModelAsLpFormat());
        writer.close();
        fWriter.close();
    }

    private MPSolverParameters createMpSolverParameters() {
        var result = new MPSolverParameters();
        result.setDoubleParam(MPSolverParameters.DoubleParam.RELATIVE_MIP_GAP, 0.50);
        return result;
    }

    private SolutionStatus decodeResultStatus(MPSolver.ResultStatus resultStatus) {
        switch (resultStatus) {
            case OPTIMAL :
                return SolutionStatus.OPTIMAL;
            case FEASIBLE:
                return SolutionStatus.FEASIBLE;
            case INFEASIBLE:
                return SolutionStatus.INFEASIBILITY_PROVED;
            case UNBOUNDED:
                return SolutionStatus.UNBOUNDED;
            case NOT_SOLVED:
                return SolutionStatus.NOT_FOUND;
            default:
                return SolutionStatus.NOT_FOUND;
        }
    }

    private synchronized void addVariable(Variable variable) {
        String varName = variable.getLabel();
        switch (variable.getType()){
            case BINARY: {
                _mpSolver.makeBoolVar(varName);
                return;
            }
            case INTEGER: {
                _mpSolver.makeIntVar(variable.getLb().orElse(Integer.MIN_VALUE), variable.getUb().orElse(Integer.MAX_VALUE), varName);
                return;
            }
            default: throw new IllegalArgumentException("Unsupported variable type + " + variable.getType());
        }
    }

    private synchronized void addConstraint(LinearConstraint constraint) {
        String name = "" + constraint.getLabel();
        MPConstraint mpConstraint = _mpSolver.makeConstraint(name);
        // set bound
        double constant = constraint.getConstant();
        switch (constraint.getConstraintType()) {
            case EQUALS -> mpConstraint.setBounds(constant, constant);
            case LESS_OR_EQUAL -> mpConstraint.setUb(constant);
            case MORE_OR_EQUAL -> mpConstraint.setLb(constant);
        }
        constraint.getExpression()
                .getVarLabelCoefficientStream()
                .forEach(varCoeff -> mpConstraint.setCoefficient(_mpSolver.lookupVariableOrNull(varCoeff.getKey()), varCoeff.getValue()));
    }

    private synchronized void addObjective(LinearExpression expression, ObjectiveType objectiveType) {
        MPObjective objective = _mpSolver.objective();
        switch (objectiveType) {
            case MINIMIZATION -> objective.setMinimization();
            case MAXIMIZATION -> objective.setMaximization();
        }
        expression.getVarLabelCoefficientStream()
                .sequential()
                .forEach(varLabelCoefficientPair -> {
                    MPVariable mpVar = _mpSolver.lookupVariableOrNull(varLabelCoefficientPair.getKey());
                    double coefficient = varLabelCoefficientPair.getValue();
                    objective
                        .setCoefficient(mpVar, coefficient);
        });
    }
}
