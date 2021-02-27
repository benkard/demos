package eu.mulk.demos.blog.authors;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

@Entity
public class Author extends PanacheEntity {

  public String name;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "author")
  @LazyToOne(LazyToOneOption.NO_PROXY)
  @JsonbTransient
  public BasicCredentials basicCredentials;

  public static Author create(String name) {
    var a = new Author();
    a.name = name;
    return a;
  }
}
