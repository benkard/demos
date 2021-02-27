package eu.mulk.demos.blog.posts;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class Category extends PanacheEntity {

  public String name;

  public static Category create(String name) {
    var c = new Category();
    c.name = name;
    return c;
  }
}
