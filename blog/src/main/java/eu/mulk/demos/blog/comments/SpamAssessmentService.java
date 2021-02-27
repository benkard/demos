package eu.mulk.demos.blog.comments;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

/**
 * Simulates a remote service that classifies {@link Comment}s as either {@link SpamStatus#SPAM} or
 * {@link SpamStatus#HAM}.
 */
@ApplicationScoped
public class SpamAssessmentService {

  /**
   * Classifies a list of {@link Comment}s as either {@link SpamStatus#SPAM} or * {@link
   * SpamStatus#HAM}.
   *
   * @return a map mapping {@link Comment#id}s to {@link SpamStatus}es.
   */
  public Map<Long, SpamStatus> assess(Collection<Comment> comments) {
    return comments.stream().collect(toMap(x -> x.id, this::assessOne));
  }

  private SpamStatus assessOne(Comment c) {
    if (c.authorName.startsWith("Anonymous")) {
      return SpamStatus.SPAM;
    }

    return SpamStatus.HAM;
  }
}
