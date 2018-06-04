package org.mihin.cqf.measureevaluation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import org.hl7.fhir.dstu3.model.*;

import java.io.Console;


public class Main {

    public static void main(String[] args) {
        // Build a DSTU3 context
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser = ctx.newXmlParser().setPrettyPrint(true);

        // Create a new client using the desired endpoint
        IGenericClient client = ctx.newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");


        // Retrieve a Patient
        Patient patient = client.read().resource(Patient.class).withId("Patient-1153").execute();

        // Write the patient out as an XML string
        System.out.println(parser.encodeResourceToString(patient));

        // Execute a measure evaluation for the patient
        Parameters response = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
                .named("evaluate-measure")
                .withParameter(Parameters.class, "patient", new StringType("Patient-1153"))
                .andParameter("periodStart", new DateTimeType("2017-01"))
                .andParameter("periodEnd", new DateTimeType("2017-12"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));

//        // Execute a measure evaluation for the population
//        response = client.operation().onInstance(new IdType("Measure", "measure-bcs"))
//                .named("evaluate-measure")
//                .withParameter(Parameters.class, "periodStart", new DateTimeType("2017-01"))
//                .andParameter("periodEnd", new DateTimeType("2017-12"))
//                .useHttpGet()
//                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));

        // Execute a care gaps report for the patient
        response = client.operation().onType(Measure.class)
                .named("care-gaps")
                .withParameter(Parameters.class, "patient", new StringType("Patient-1153"))
                .andParameter("periodStart", new DateTimeType("2017-01"))
                .andParameter("periodEnd", new DateTimeType("2017-12"))
                .andParameter("topic", new StringType("Preventive Care and Screening"))
                .useHttpGet()
                .execute();

        // Write out the result as an XML string
        System.out.println(parser.encodeResourceToString(response));
    }
}
