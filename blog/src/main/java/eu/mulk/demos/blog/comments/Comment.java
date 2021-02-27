package eu.mulk.demos.blog.comments;

import eu.mulk.demos.blog.posts.Post;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends PanacheEntity {

  public String authorName;

  public Instant publicationDate;

  @Enumerated(EnumType.STRING)
  public SpamStatus spamStatus;

  @Column(columnDefinition = "TEXT")
  public String text;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonbTransient
  public Post post;

  public static Comment create(Post post, String authorName, String text) {
    var c = new Comment();
    c.authorName = authorName;
    c.publicationDate = Instant.now();
    c.text = text;
    c.post = post;
    c.spamStatus = SpamStatus.UNKNOWN;
    return c;
  }

}
