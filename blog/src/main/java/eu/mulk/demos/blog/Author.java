package eu.mulk.demos.blog;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class Author extends PanacheEntity {

  public String name;

  public static Author create(String name) {
    var a = new Author();
    a.name = name;
    return a;
  }
}
