package com.sportradar.unifiedodds.sdk.junit;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

@SuppressWarnings({ "MagicNumber" })
public class AssumingRabbitRunningRule implements TestRule {

    private final RabbitMqConnectionChecker checker;

    public AssumingRabbitRunningRule(RabbitMqConnectionChecker checker) {
        this.checker = checker;
    }

    public AssumingRabbitRunningRule() {
        this(new RabbitMqConnectionChecker(15672));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (!checker.connect()) {
                    throw new AssumptionViolatedException("Could not connect to RabbitMQ. Skipping test!");
                } else {
                    base.evaluate();
                }
            }
        };
    }
}
