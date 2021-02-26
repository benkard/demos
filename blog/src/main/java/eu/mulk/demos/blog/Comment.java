package eu.mulk.demos.blog;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends PanacheEntity {

  public String authorName;

  public Instant publicationDate;

  @Column(columnDefinition = "TEXT")
  public String text;

  @JsonbTransient
  @ManyToOne
  public Post post;

  public static Comment create(Post post, String authorName, String text) {
    var c = new Comment();
    c.authorName = authorName;
    c.publicationDate = Instant.now();
    c.text = text;
    c.post = post;
    return c;
  }
}
