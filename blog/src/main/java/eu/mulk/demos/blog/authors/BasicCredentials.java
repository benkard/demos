package eu.mulk.demos.blog.authors;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class BasicCredentials extends PanacheEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JsonbTransient
  public Author author;

  public String username;

  public String password;

  public static BasicCredentials create(Author author, String username, String password) {
    var bc = new BasicCredentials();
    bc.author = author;
    bc.id = author.id;
    bc.username = username;
    bc.password = password;
    return bc;
  }
}
