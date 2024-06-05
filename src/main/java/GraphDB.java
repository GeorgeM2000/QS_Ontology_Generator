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

public class GraphDB {
    /**
     * How to connect to a graphdb repository, load a file to a repository, add some data through the API and perform a
     * sparql query
     */
    public static void main(String[] args) {

        HTTPRepository repository = new HTTPRepository("http://pop-os:7200/repositories/QS_Ontology");
        RepositoryConnection connection = repository.getConnection();

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
