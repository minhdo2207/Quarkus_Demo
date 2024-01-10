package sqlquery;

public class PersonSQL {

    public static String DROP_PERSON_DATABASE = " DROP TABLE IF EXISTS persons ";

    public static String CREATE_PERSON_DATABASE = " CREATE TABLE persons (id SERIAL PRIMARY KEY, name VARCHAR(50), age INT, email VARCHAR(50)) ";

    public static String INSERT_PERSON_DATA =   "DO $$ \n" +
                                                "DECLARE\n" +
                                                "    i INTEGER;\n" +
                                                "BEGIN\n" +
                                                "    FOR i IN 1..1000000 LOOP\n" +
                                                "        INSERT INTO persons(name, age, email) \n" +
                                                "        VALUES \n" +
                                                "            ('Person' || i, \n" +
                                                "             (i % 80) + 20, -- Age between 20 and 99\n" +
                                                "             'person' || i || '@example.com');\n" +
                                                "    END LOOP;\n" +
                                                "END $$;";
    public static String CREATE_INDEX_PERSON_DATABASE = " CREATE INDEX idx_persons_name ON persons (name); ";
    public static String FIND_ALL_PERSON = " SELECT id, name, age, email FROM persons ORDER BY id, name DESC ";
    public static String FIND_PERSON_BY_ID = " SELECT id, name, age, email FROM persons WHERE id = $1 ";

    public static String CREATE_PERSON = " INSERT INTO persons (name, age, email) VALUES ($1, $2, $3) RETURNING id, name, age, email ";

    public static String UPDATE_PERSON = " UPDATE persons SET name = $1, age = $2, email = $3 WHERE id = $4 RETURNING id, name, age, email ";
    public static String DELETE_PERSON = " DELETE FROM persons WHERE id = $1 RETURNING id, name, age, email ";


}
