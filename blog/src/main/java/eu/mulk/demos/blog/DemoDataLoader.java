package eu.mulk.demos.blog;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import eu.mulk.demos.blog.authors.Author;
import eu.mulk.demos.blog.comments.Comment;
import eu.mulk.demos.blog.posts.Category;
import eu.mulk.demos.blog.posts.Post;
import io.quarkus.runtime.StartupEvent;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class DemoDataLoader {

  static final int AUTHOR_COUNT = 3;
  static final int POST_COUNT = 10;
  static final int COMMENT_COUNT = 3;
  static final int CATEGORY_COUNT = 2;

  @Inject
  EntityManager em;

  @Transactional
  void onStart(@Observes StartupEvent ev) {
    if (Author.findAll().stream().findAny().isPresent()) {
      // Already initialized.
      return;
    }

    // Authors
    var authors =
        nat(AUTHOR_COUNT)
            .map(x -> Author.create("Author #%d".formatted(x)))
            .collect(toList());
    authors.forEach(em::persist);

    // Posts
    var posts =
        nat(POST_COUNT)
            .map(x -> Post.create(authors.get(x % AUTHOR_COUNT), "Post #%d".formatted(x)))
            .collect(toList());
    posts.forEach(em::persist);

    // Comments
    for (var post : posts) {
      post.comments =
          nat(COMMENT_COUNT)
              .map(x -> Comment.create(post, "Anonymous Coward", "First post"))
              .collect(toSet());
      post.comments.forEach(em::persist);
    }

    // Categories
    var categories =
        nat(CATEGORY_COUNT)
            .map(x -> Category.create("Category #%d".formatted(x)))
            .collect(toSet());
    categories.forEach(em::persist);
    for (var post : posts) {
      post.categories = categories;
    }
  }

  private static Stream<Integer> nat(int postCount) {
    return nat().limit(postCount);
  }

  private static Stream<Integer> nat() {
    return Stream.iterate(0, x -> x + 1);
  }
}
