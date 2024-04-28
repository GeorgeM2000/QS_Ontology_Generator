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

/*
Indexes of indicators of the 2024 World University Rankings dataset:

                1 // Rank
                3 // University
                5 // Country
                6 // size
                7 // focus
                8 // research
                9 // status
                10 // ar score
                11 // ar rank
                12 // er score
                13 // er rank
                14 // fsr score
                15 // fsr rank
                16 // cpf score
                17 // cpf rank
                18 // ifr score
                19 // ifr rank
                20 // isr score
                21 // isr rank
                22 // irn score
                23 // irn rank
                24 // eo score
                25 // eo rank
                26 // sus score
                27 // sus rank
 */


/*
Indexes of indicators of the 2024 best student cities dataset:

0 - Rank
1 - City
2 - Country
3 - Score

 */

/*
Indexes of indicators of the 2024 World University Rankings by Subject dataset:

0 - 2024 Rank
1 - Institution
2 - Location
3 - Score
4 - Subject

 */


public class QS_Ontology_Generator {
    public static void main(String[] args) throws IOException {

        // Create a new, empty Model object.
        Model model = new TreeModel();

        // This can change depending on the QS dataset at hand
        //String csvFile = "src/main/resources/Filtered CSVs/QS 2024 World University Rankings Filtered.csv";
        String csvFile = "src/main/resources/Filtered CSVs/QS 2024 WUR by Subject Filtered.csv";
        //String csvFile = "src/main/resources/Filtered CSVs/QS Best Student Cities 2024 Filtered.csv";


        String line = "";
        int current_row = 0;
        int ROW_LIMIT = 10;

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




        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (current_row == ROW_LIMIT) { // Set a limit for testing purposes
                    break;
                } else if (current_row != 0) { // Row 0 is the column names (Skip row 0)

                    // === Add objects ===

                    // WUR
                    //model.add(Values.iri(qs, data[3]), RDF.TYPE, university);
                    //model.add(Values.iri(qs, data[5]), RDF.TYPE, country);

                    // Best Student Cities
                    //model.add(Values.iri(qs, data[1]), RDF.TYPE, city);

                    // WUR by Subject
                    model.add(Values.iri(qs, data[4]), RDF.TYPE, subject);
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), RDF.TYPE, uni_sub);



                    // === Add object relations ===

                    // WUR
                    //model.add(Values.iri(qs, data[3]), isLocatedIn, Values.iri(qs, data[5]));

                    // Best Student Cities
                    //model.add(Values.iri(qs, data[1]), cityIsLocatedIn, Values.iri(qs, data[2]));

                    // WUR by Subject
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), sub, Values.iri(qs, data[4]));
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uni, Values.iri(qs, data[1]));


                    // Add data

                    /*
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

                     */

                    // Best Student Cities
                    //model.add(Values.iri(qs, data[1]), cRank, Values.literal(data[0]));
                    //model.add(Values.iri(qs, data[1]), cScore, !data[3].isEmpty() ? Values.literal(Float.parseFloat(data[3])) : Values.literal(Float.parseFloat("-1.0")));

                    // WUR by Subject
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uniSubRank, Values.literal(data[0]));
                    model.add(Values.iri(qs, data[1] + "-" + data[4]), uniSubScore, !data[3].isEmpty() ? Values.literal(Float.parseFloat(data[3])) : Values.literal(Float.parseFloat("-1.0")));



                }

                current_row += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("-----------------------------------------------------------------------");

        // You can also use write to print the model
        System.out.println("You can also use write to print the model");

        try (OutputStream outputStream = new FileOutputStream("src/main/resources/Results/Results.ttl")) {
            Rio.write(model, outputStream, RDFFormat.TURTLE);
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }

        //Rio.write(model, System.out, RDFFormat.TURTLE); // Print the output to default output

        System.out.println("-----------------------------------------------------------------------");
    }
}
