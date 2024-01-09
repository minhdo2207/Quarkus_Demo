package entity;

import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.inject.Model;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Person {
    public String name;
    public Long age;
    public String email;
    private Long id;

    public static Person from(Row row) {
        return new Person(row.getString("name"), row.getLong("age"), row.getString("email"), row.getLong("id"));
    }
}
