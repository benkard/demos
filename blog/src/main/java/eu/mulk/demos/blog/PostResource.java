package eu.mulk.demos.blog;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.jboss.logging.Logger;

@Path("/posts")
public class PostResource {

  static final Logger log = Logger.getLogger(PostResource.class);

  /**
   * Fetches all posts with no extra information.
   *
   * Simple.  No surprises.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  public List<Post> getAll() {
    clearLog();

    return Post.findAll().list();
  }

  /**
   * Fetches all posts with comments included.
   *
   * Lazy fetching.  Simple.  No surprises.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q1")
  public List<Post> getAllWithComments() {
    clearLog();

    return Post.find(
        """
            SELECT p FROM Post p
              LEFT JOIN FETCH p.comments 
            """)
        .list();
  }

  /**
   * Fetches all posts with author info included.
   *
   * <strong>Oops!</strong>
   *
   * {@link LazyToOne} with {@link LazyToOneOption#NO_PROXY} is needed to make this efficient.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q2")
  public List<Post> getAllWithAuthors() {
    clearLog();

    return Post.find(
        """
            SELECT p FROM Post p
              LEFT JOIN FETCH p.author 
            """)
        .list();
  }

  /**
   * Fetches all posts with comments and category info included.
   *
   * <strong>Oops!</strong>  Crashes.
   *
   * Either use {@link Set} and get bad performance or do it as in {@link
   * #getAllWithCommentsAndCategories2()}.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q3")
  public List<Post> getAllWithCommentsAndCategories() {
    clearLog();

    return Post.find(
        """
            SELECT p FROM Post p
              LEFT JOIN FETCH p.comments
              LEFT JOIN FETCH p.categories
            """)
        .list();
  }

  /**
   * Fetches all posts with comments and category info included.
   *
   * 2 queries, but hey, no cartesian explosion!  Works really well.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q4")
  public List<Post> getAllWithCommentsAndCategories2() {
    clearLog();

    List<Post> posts = Post.find(
        """
            SELECT p FROM Post p
              LEFT JOIN FETCH p.comments
            """)
        .list();

    posts = Post.find(
        """
            SELECT DISTINCT p FROM Post p
              LEFT JOIN FETCH p.categories
             WHERE p IN (?1)
            """,
        posts)
        .list();

    return posts;
  }

  private static void clearLog() {
    log.infof("""
                
        -----------------------------------------------------
        -------------------- NEW REQUEST --------------------
        -----------------------------------------------------
        """);
  }
}
