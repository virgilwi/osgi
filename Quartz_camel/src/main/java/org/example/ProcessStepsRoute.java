package org.example;

import org.apache.camel.builder.RouteBuilder;

public class ProcessStepsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:nextStep")
                .choice()
                .when(header("step").isNull())
                .log("Starting step 1")
                .setHeader("step", constant(1))
                .to("direct:insertData")
                .when(header("step").isEqualTo(1))
                .log("Proceeding to step 2")
                .setHeader("step", constant(2))
                .to("direct:aggregateData")
                .otherwise()
                .log("Unknown step. Aborting.")
                .stop();
    }
}
