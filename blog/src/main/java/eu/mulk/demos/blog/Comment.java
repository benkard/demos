package eu.mulk.demos.blog;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import javax.persistence.Entity;

@Entity
public class Comment extends PanacheEntity {

  public String authorName;
  public Instant publicationDate;
  public String text;
}
