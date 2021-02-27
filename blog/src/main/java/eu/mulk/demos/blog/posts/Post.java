package eu.mulk.demos.blog.posts;

import eu.mulk.demos.blog.authors.Author;
import eu.mulk.demos.blog.comments.Comment;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Post extends PanacheEntity {

  public String title;

  public Instant publicationDate;

  @Column(columnDefinition = "TEXT")
  public String body;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonbTransient
  public Author author;

  @ManyToMany(fetch = FetchType.LAZY)
  @JsonbTransient
  public Set<Category> categories;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
  @JsonbTransient
  public Set<Comment> comments;

  public static Post create(Author author, String title) {
    var p = new Post();
    p.title = title;
    p.publicationDate = Instant.now();
    p.body = "";
    p.author = author;
    return p;
  }
}
