package eu.mulk.demos.blog;

import java.util.List;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/posts")
public class PostResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  public List<Post> getAll() {
    return Post.findAll().list();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q1")
  public List<Post> getAllWithComments() {
    return Post.find("""
        SELECT p FROM Post p
          LEFT JOIN FETCH p.comments 
        """).list();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Transactional
  @Path("/q2")
  public List<Post> getAllWithCommentsAndCategories() {
    return Post.find("""
        SELECT p FROM Post p
          LEFT JOIN FETCH p.comments
          LEFT JOIN FETCH p.categories
        """).list();
  }

}
