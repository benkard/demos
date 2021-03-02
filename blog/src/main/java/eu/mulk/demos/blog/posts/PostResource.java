package eu.mulk.demos.blog.posts;

import eu.mulk.demos.blog.authors.Author;
import eu.mulk.demos.blog.comments.Comment;
import eu.mulk.demos.blog.comments.SpamAssessmentService;
import eu.mulk.demos.blog.comments.SpamStatus;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.jboss.logging.Logger;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

  static final Logger log = Logger.getLogger(PostResource.class);

  @Inject
  SpamAssessmentService spamAssessmentService;

  @PersistenceContext
  EntityManager entityManager;

  /**
   * Fetches all posts with no extra information.
   *
   * Simple.  No surprises.
   */
  @GET
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
   * {@link LazyToOne} with {@link LazyToOneOption#NO_PROXY} on {@link Author#basicCredentials} is
   * needed to make this efficient.
   */
  @GET
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

  /**
   * Fetches all posts with comments and category info included.
   *
   * 2 queries, but hey, no cartesian explosion!  Works really well.
   */
  @POST
  @Transactional
  @Path("/q5")
  public void updateCommentStatus() {
    clearLog();

    List<Comment> comments = Comment.find(
        """
            SELECT c FROM Comment c
             WHERE c.spamStatus = 'UNKNOWN'
            """)
        .list();

    var assessments = spamAssessmentService.assess(comments);

    for (var assessment : assessments.entrySet()) {
      Comment comment = Comment.findById(assessment.getKey());
      comment.spamStatus = assessment.getValue();
    }
  }

  /**
   * Resets the {@link Comment#spamStatus} to {@link SpamStatus#UNKNOWN} on all comments.
   *
   * This issues a lot of UPDATE statements, but semantically speaking it works.
   */
  @POST
  @Transactional
  @Path("/q6")
  public void resetCommentStatus() {
    clearLog();

    List<Comment> comments = Comment.find(
        """
            SELECT c FROM Comment c
             WHERE c.spamStatus <> 'UNKNOWN'
            """)
        .list();
    comments.forEach(c -> c.spamStatus = SpamStatus.UNKNOWN);
  }

  /**
   * Resets the {@link Comment#spamStatus} to {@link SpamStatus#UNKNOWN} on all comments.
   *
   * This is exactly equivalent to {@link #resetCommentStatus()} and just as efficient or
   * inefficient.
   */
  @POST
  @Transactional
  @Path("/q6")
  public void resetCommentStatus2() {
    clearLog();

    Comment.update("UPDATE Comment c SET c.spamStatus = 'UNKNOWN' WHERE c.spamStatus <> 'UNKNOWN'");
  }

  /**
   * Resets the {@link Comment#spamStatus} to {@link SpamStatus#UNKNOWN} on all comments.
   *
   * This is exactly equivalent to {@link #resetCommentStatus()} and just as efficient or
   * inefficient.
   */
  @POST
  @Transactional
  @Path("/q7")
  public void resetCommentStatus3() {
    clearLog();

    entityManager.createNativeQuery(
        "UPDATE comment SET spam_status = 'UNKNOWN' WHERE spam_status <> 'UNKNOWN'")
        .executeUpdate();
  }

  /**
   * Fetches all posts with all the relevant info for an overview included.
   *
   * Bad version.
   */
  @GET
  @Transactional
  @Path("/q8")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PostSummary> overview1() {
    clearLog();

    return Post.find(
        """
            SELECT p FROM Post p
              LEFT JOIN FETCH p.author
              LEFT JOIN FETCH p.comments
            """)
        .<Post>stream()
        .map((Post p) ->
            new PostSummary(p.author.name, p.title, p.publicationDate, p.comments.size()))
        .collect(Collectors.toList());
  }

  /**
   * Fetches all posts with all the relevant info for an overview included.
   *
   * Good version.
   */
  @GET
  @Transactional
  @Path("/q9")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PostSummary> overview2() {
    clearLog();

    return entityManager.createQuery(
        """
            SELECT NEW eu.mulk.demos.blog.posts.PostSummary(
                p.author.name, p.title, p.publicationDate, size(p.comments))
              FROM Post p
            """,
        PostSummary.class)
        .getResultList();
  }

  private static void clearLog() {
    log.infof("""
                
        -----------------------------------------------------
        -------------------- NEW REQUEST --------------------
        -----------------------------------------------------
        """);
  }

}
