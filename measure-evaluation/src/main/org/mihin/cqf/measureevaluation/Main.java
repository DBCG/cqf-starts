package org.mihin.cqf.measureevaluation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import org.hl7.fhir.dstu3.model.*;

import java.io.Console;


public class Main {

    public static void main(String[] args) {
        setup();
        retrievePatient();
        evaluateIndividualMeasure();
    }

    private static FhirContext ctx;
    private static IParser parser;
    private static IGenericClient client;

    private static void setup() {
        // Build a DSTU3 context
        ctx = FhirContext.forDstu3();
        parser = ctx.newXmlParser().setPrettyPrint(true);

        // Create a new client using the desired endpoint
        client = ctx.newRestfulGenericClient("http://connectathon22.alphora.com/cqf-ruler/baseDstu3");
    }

    private static void retrievePatient() {
        // Retrieve a Patient
        Patient patient = client.read().resource(Patient.class).withId("Patient-411").execute();

        // Write the patient out as an XML string
        System.out.println(parser.encodeResourceToString(patient));
    }

    private static void evaluateIndividualMeasure() {

        // Execute a measure evaluation for the patient
        Parameters response = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
                .named("evaluate-measure")
                .withParameter(Parameters.class, "patient", new StringType("Patient-411"))
                .andParameter("periodStart", new DateTimeType("2018-01-01"))
                .andParameter("periodEnd", new DateTimeType("2018-12-31"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));
    }

    private static void collectData() {

        // Collect data for the patient
        Parameters response = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
                .named("collect-data")
                .withParameter(Parameters.class, "patient", new StringType("Patient-411"))
                .andParameter("periodStart", new DateTimeType("2018-01-01"))
                .andParameter("periodEnd", new DateTimeType("2018-12-31"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));

        // Submit the results (POST $submit-data)
        //Parameters submitResponse = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
        //      .named("submit-data")
        //        .withParameters((Object)response)
    }

    private static void submitData() {

    }

    private static void evaluateSummaryMeasure() {
        // Execute a measure evaluation for the population
        Parameters response = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
                .named("evaluate-measure")
                .withParameter(Parameters.class, "periodStart", new DateTimeType("2018-01"))
                .andParameter("periodEnd", new DateTimeType("2018-12"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));
    }

    private static void careGaps() {
        // Execute a care gaps report for the patient
        Parameters response = client.operation().onType(Measure.class)
                .named("care-gaps")
                .withParameter(Parameters.class, "patient", new StringType("Patient-411"))
                .andParameter("periodStart", new DateTimeType("2018-01-01"))
                .andParameter("periodEnd", new DateTimeType("2018-12-31"))
                .andParameter("topic", new StringType("Preventive Care and Screening"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));
    }
}
