package eu.mulk.demos.blog;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.Instant;
import java.util.List;
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
  public Author author;

  @ManyToMany(fetch = FetchType.LAZY)
  public List<Category> categories;

  @OneToMany(fetch = FetchType.LAZY)
  public List<Comment> comments;
}
