package sqlquery;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class PersonMigration {
    @Inject
    io.vertx.mutiny.pgclient.PgPool personClient;

    @Inject
    @ConfigProperty(name = "db.schema.create", defaultValue = "true")
    boolean schemaCreate;

    void config(@Observes StartupEvent ev) {
        if (schemaCreate) {
            initDb();
        }
    }

    private void initDb() {
        personClient.query(PersonSQL.DROP_PERSON_DATABASE)
                .execute()
                .flatMap(m -> personClient.query(PersonSQL.CREATE_PERSON_DATABASE)
                        .execute())
                .flatMap(m -> personClient.query(PersonSQL.INSERT_PERSON_DATA)
                        .execute())
                .subscribe()
                .with(
                        success -> System.out.println("Database initialized successfully."),
                        failure -> {
                            System.err.println("Failed to initialize the database.");
                            failure.printStackTrace();
                        });
    }
}
