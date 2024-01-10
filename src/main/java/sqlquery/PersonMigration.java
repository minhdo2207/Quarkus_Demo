package sqlquery;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import utils.ErrrorMessage;

@Slf4j
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
                .flatMap(m -> personClient.query(PersonSQL.CREATE_INDEX_PERSON_DATABASE)
                        .execute())
                .subscribe()
                .with(
                        success -> log.info(ErrrorMessage.DATABASE_INTITAL_SUCCESS),
                        failure -> {
                            log.info(ErrrorMessage.DATABASE_INTITAL_FAIL);
                            failure.printStackTrace();
                        });
    }
}
