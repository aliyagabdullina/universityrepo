package model.expressions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestLinearExpressionImpl {
    LinearExpression zeroExpression;
    LinearExpression expression;
    @BeforeEach
    void setUp() {
        zeroExpression = new LinearExpressionImpl(Stream.empty(), 0.0);
        var pair1 = new Pair<>("var1", 4.0);
        var pair2 = new Pair<>("var2", 3.1);
        expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
    }

    @Test
    void testGetConstant0() {
        assertEquals(0.0, zeroExpression.getConstant());
    }

    @Test
    void testGetConstant5() {
        assertEquals(5.0, expression.getConstant());
    }

    @Test
    void testGetCoefficientZero() {
        assertEquals(0.0, expression.getCoefficient("var10"));
    }

    @Test
    void testGetCoefficient4() {
        assertEquals(4.0, expression.getCoefficient("var1"));
    }

    @Test
    void testCalculate0() {
        assertEquals(0.0, zeroExpression.calculate(i -> 10.0));
    }

    @Test
    void testCalculate() {
        assertEquals(19.2, expression.calculate(i -> 2.0));
    }
}