package kr.co.numble.numble.domain.category.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@EqualsAndHashCode
public class FeedCategoryId implements Serializable {

    private Long feedId;

    private Long categoryId;

    @Builder
    public FeedCategoryId(Long feedId, Long categoryId) {
        this.feedId = feedId;
        this.categoryId = categoryId;
    }
}