package eu.mulk.demos.blog.posts;

import java.time.Instant;

public final class PostSummary {

  public final String authorName;
  public final String title;
  public final Instant publicationDate;
  public final int commentCount;

  public PostSummary(
      String authorName,
      String title,
      Instant publicationDate,
      int commentCount) {
    this.authorName = authorName;
    this.title = title;
    this.publicationDate = publicationDate;
    this.commentCount = commentCount;
  }
}
