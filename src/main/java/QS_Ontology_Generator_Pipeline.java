import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.turtle.TurtleWriterFactory;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QS_Ontology_Generator_Pipeline {


    public static void main(String[] args) {
        // Create a new, empty Model object.
        Model model = new TreeModel();

        // This can change depending on the QS dataset at hand
        String wur = "src/main/resources/Filtered CSVs/QS 2024 World University Rankings Filtered.csv";
        String wur_subject = "src/main/resources/Filtered CSVs/QS 2024 WUR by Subject Filtered.csv";
        String qs_cities = "src/main/resources/Filtered CSVs/QS Best Student Cities 2024 Filtered.csv";


        String line = "";
        int current_row = 0;
        int ROW_LIMIT = 2000;

        // We want to reuse this namespace when creating several building blocks
        String qs = "http://www.semanticweb.org/georgematlis/ontologies/2024/3/QS_Ontology_Graph#";

        // We can add also the namespace to the model
        model.setNamespace("", qs);

        // Create IRIs for the resources we want to add

        // === Classes ===
        IRI university = Values.iri(qs, "University");
        IRI country = Values.iri(qs, "Country");
        IRI city = Values.iri(qs, "City");
        IRI subject = Values.iri(qs, "Subject");
        IRI uni_sub = Values.iri(qs, "Uni_Sub");

        // === Object Properties ===
        IRI isLocatedIn = Values.iri(qs,"isLocatedIn");
        IRI cityIsLocatedIn = Values.iri(qs,"cityIsLocatedIn");
        IRI sub = Values.iri(qs,"subject");
        IRI uni = Values.iri(qs,"university");
        IRI hasUniversity = Values.iri(qs,"hasUniversity");
        IRI hasCity = Values.iri(qs,"hasCity");

        // === Data Properties ===

        // === Properties for QS WUR ===
        IRI arRank = Values.iri(qs,"arRank");
        IRI arScore = Values.iri(qs, "arScore");
        IRI erRank = Values.iri(qs, "erRank");
        IRI erScore = Values.iri(qs, "erScore");
        IRI cpfRank = Values.iri(qs, "cpfRank");
        IRI cpfScore = Values.iri(qs, "cpfScore");
        IRI eoRank = Values.iri(qs, "eoRank");
        IRI eoScore = Values.iri(qs, "eoScore");
        IRI fsRank = Values.iri(qs, "fsRank");
        IRI fsScore = Values.iri(qs, "fsScore");
        IRI ifRank = Values.iri(qs, "ifRank");
        IRI ifScore = Values.iri(qs, "ifScore");
        IRI isRank = Values.iri(qs, "isRank");
        IRI isScore = Values.iri(qs, "isScore");
        IRI sRank = Values.iri(qs, "sRank");
        IRI sScore = Values.iri(qs, "sScore");
        IRI irnRank = Values.iri(qs, "irnRank");
        IRI irnScore = Values.iri(qs, "irnScore");
        IRI status = Values.iri(qs, "status");
        IRI focus = Values.iri(qs, "focus");
        IRI size = Values.iri(qs, "size");
        IRI res = Values.iri(qs, "res");
        IRI uRank = Values.iri(qs, "uRank");

        // === Properties for QS WUR by Subject ===
        IRI uniSubRank = Values.iri(qs, "uniSubRank");
        IRI uniSubScore = Values.iri(qs, "uniSubScore");

        // === Properties for QS Best student Cities ===
        IRI cRank = Values.iri(qs, "cRank");
        IRI cScore = Values.iri(qs, "cScore");


        try (BufferedReader br = new BufferedReader(new FileReader(wur))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (current_row == ROW_LIMIT) { // Set a limit for testing purposes
                    //break;
                } else if (current_row != 0) { // Row 0 is the column names (Skip row 0)
                    // === Add objects ===

                    // WUR
                    model.add(Values.iri(qs, data[3]), RDF.TYPE, university);
                    model.add(Values.iri(qs, data[5]), RDF.TYPE, country);

                    // === Add object relations ===

                    // WUR
                    model.add(Values.iri(qs, data[3]), isLocatedIn, Values.iri(qs, data[5]));
                    model.add(Values.iri(qs, data[5]), hasUniversity, Values.iri(qs, data[3]));

                    // Add data
                    model.add(Values.iri(qs, data[3]), uRank, Values.literal(data[1]));
                    model.add(Values.iri(qs, data[3]), arRank, Values.literal(data[11]));
                    model.add(Values.iri(qs, data[3]), arScore, !data[10].isEmpty() ? Values.literal(Float.parseFloat(data[10])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), erRank, Values.literal(data[13]));
                    model.add(Values.iri(qs, data[3]), erScore, !data[12].isEmpty() ? Values.literal(Float.parseFloat(data[12])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), cpfRank, Values.literal(data[17]));
                    model.add(Values.iri(qs, data[3]), cpfScore, !data[16].isEmpty() ? Values.literal(Float.parseFloat(data[16])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), isRank, Values.literal(data[21]));
                    model.add(Values.iri(qs, data[3]), isScore, !data[20].isEmpty() ? Values.literal(Float.parseFloat(data[20])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), ifRank, Values.literal(data[19]));
                    model.add(Values.iri(qs, data[3]), ifScore, !data[18].isEmpty() ? Values.literal(Float.parseFloat(data[18])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), sRank, Values.literal(data[27]));
                    model.add(Values.iri(qs, data[3]), sScore, !data[26].isEmpty() ? Values.literal(Float.parseFloat(data[26])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), irnRank, Values.literal(data[23]));
                    model.add(Values.iri(qs, data[3]), irnScore, !data[22].isEmpty() ? Values.literal(Float.parseFloat(data[22])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), eoRank, Values.literal(data[25]));
                    model.add(Values.iri(qs, data[3]), eoScore, !data[24].isEmpty() ? Values.literal(Float.parseFloat(data[24])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), fsRank, Values.literal(data[15]));
                    model.add(Values.iri(qs, data[3]), fsScore, !data[14].isEmpty() ? Values.literal(Float.parseFloat(data[14])) : Values.literal(Float.parseFloat("-1.0")));

                    model.add(Values.iri(qs, data[3]), size, Values.literal(data[6]));
                    model.add(Values.iri(qs, data[3]), focus, Values.literal(data[7]));
                    model.add(Values.iri(qs, data[3]), status, Values.literal(data[9]));
                    model.add(Values.iri(qs, data[3]), res, Values.literal(data[8]));
                }
                current_row += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        // ====== Best QS Student Cities ======

        current_row = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(qs_cities))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (current_row == ROW_LIMIT) { // Set a limit for testing purposes
                    //break;
                } else if (current_row != 0) { // Row 0 is the column names (Skip row 0)

                    // === Add objects ===

                    // Best Student Cities
                    model.add(Values.iri(qs, data[1]), RDF.TYPE, city);
                    model.add(Values.iri(qs, data[2]), RDF.TYPE, country);

                    // === Add object relations ===

                    // Best Student Cities
                    model.add(Values.iri(qs, data[1]), cityIsLocatedIn, Values.iri(qs, data[2]));
                    model.add(Values.iri(qs, data[2]), hasCity, Values.iri(qs, data[1]));

                    // Add data

                    // Best Student Cities
                    model.add(Values.iri(qs, data[1]), cRank, Values.literal(data[0]));
                    model.add(Values.iri(qs, data[1]), cScore, !data[3].isEmpty() ? Values.literal(Float.parseFloat(data[3])) : Values.literal(Float.parseFloat("-1.0")));

                }
                current_row += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        // ====== World University Rankings by subject ======

        current_row = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(wur_subject))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (current_row == ROW_LIMIT) { // Set a limit for testing purposes
                    //break;
                } else if (current_row != 0) { // Row 0 is the column names (Skip row 0)
                    // === Add objects ===

                    // WUR by Subject
                    model.add(Values.iri(qs, data[4]), RDF.TYPE, subject);
                    model.add(Values.iri(qs, data[1]), RDF.TYPE, university);
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), RDF.TYPE, uni_sub);

                    // === Add object relations ===

                    // WUR by Subject
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), sub, Values.iri(qs, data[4]));
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uni, Values.iri(qs, data[1]));

                    // Add data

                    // WUR by Subject
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uniSubRank, Values.literal(data[0]));
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uniSubScore, !data[3].isEmpty() ? Values.literal(Float.parseFloat(data[3])) : Values.literal(Float.parseFloat("-1.0")));
                }
                current_row += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        // ====== Write the results to the RDF Ontology ======

        /*
        System.out.println("-----------------------------------------------------------------------");
        try (OutputStream outputStream = new FileOutputStream("src/main/resources/QS_Ontology_Graph_Pipeline.ttl", true)) {
            Rio.write(model, outputStream, RDFFormat.TURTLE);
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
        System.out.println("-----------------------------------------------------------------------");

         */



        // ====== Execute SPARQL query ======

        HTTPRepository repository = new HTTPRepository("http://pop-os:7200/repositories/QS_Ontology_Pipeline");
        RepositoryConnection connection = repository.getConnection();

        // Clear the repository before we start
        connection.clear();

        // load a simple ontology from a file
        connection.begin();
        // Adding the family ontology
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/QS_Ontology_Graph_Pipeline.ttl");
            connection.add(inputStream, "urn:base",
                    RDFFormat.TURTLE);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Committing the transaction persists the data
        connection.commit();

        // add our data
        connection.begin();
        connection.add(model);
        connection.commit();

        // The SPARQL query
        String queryString = "PREFIX qs: <http://www.semanticweb.org/georgematlis/ontologies/2024/3/QS_Ontology_Graph#> \n";
        queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
        queryString += "SELECT ?uni ?country WHERE {\n";
        queryString += "    ?uni a qs:University . \n";
        queryString += "    ?uni qs:isLocatedIn ?country . \n";
        queryString += "    ?uni qs:arScore ?arScore . \n";
        queryString += "    ?uni qs:erScore ?erScore . \n";
        queryString += "    FILTER(?arScore > 90.0 && ?erScore > 90.0) \n";
        queryString += "} \n";
        queryString += "order by desc(?arScore) desc(?erScore)";

        TupleQuery query = connection.prepareTupleQuery(queryString);

        // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
        try (TupleQueryResult result = query.evaluate()) {
            // we just iterate over all solutions in the result...
            for (BindingSet solution : result) {
                // ... and print out the value of the variable binding for ?s and ?n
                System.out.println("?uni = " + solution.getValue("uni") + "| ?country = " + solution.getValue("country"));
            }
        }

        connection.close();
        repository.shutDown();



    }
}
