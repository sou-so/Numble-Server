package kr.co.numble.numble.domain.feed.domain.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.numble.numble.domain.category.domain.repository.vo.QCategoryVO;
import kr.co.numble.numble.domain.feed.domain.repository.vo.FeedDetailsVO;
import kr.co.numble.numble.domain.feed.domain.repository.vo.QFeedDetailsVO;
import kr.co.numble.numble.domain.user.domain.repository.vo.QAuthorVO;
import kr.co.numble.numble.global.enums.SortPageType;
import kr.co.numble.numble.global.utils.code.PagingSupportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import static kr.co.numble.numble.domain.bookmark.domain.QFeedBookmark.feedBookmark;
import static kr.co.numble.numble.domain.category.domain.QFeedCategory.feedCategory;
import static kr.co.numble.numble.domain.feed.domain.QFeed.feed;
import static kr.co.numble.numble.domain.like.domain.QFeedLike.feedLike;
import static kr.co.numble.numble.global.enums.SortPageType.LATEST;
import static kr.co.numble.numble.global.enums.SortPageType.POPULAR;


@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public FeedDetailsVO queryFeedDetails(Long feedId, Long userId) {
        return selectFromFeed(userId)
                .where(feed.id.eq(feedId))
                .fetchOne();
    }

    @Override
    public Slice<FeedDetailsVO> queryFeedPages(Long userId, Long cursorId, SortPageType sortType, Pageable pageable) {

        JPAQuery<FeedDetailsVO> jpaQuery = selectFromFeed(userId)
                .distinct()
                .where(eqPage(cursorId))
                .orderBy(feedSort(sortType));

        return PagingSupportUtil.fetchSlice(jpaQuery, pageable);
    }

    private BooleanExpression eqPage(Long cursorId) {
        return cursorId != null ? feed.id.gt(cursorId) : null;
    }

    private BooleanExpression eqFeedCategoryId(NumberPath<Long> id) {
        return id != null ? feedCategory.feed.id.eq(id) : null;
    }

    private BooleanExpression eqFeedBookmarkId(NumberPath<Long> id) {
        return id != null ? feedBookmark.feed.id.eq(id) : null;
    }

    private BooleanExpression eqFeedLikeId(NumberPath<Long> id) {
        return id != null ? feedLike.feed.id.eq(id) : null;
    }

    private BooleanExpression eqFeedBookmarkUserId(Long id) {
        return id != null ? feedBookmark.user.id.eq(id) : null;
    }

    private BooleanExpression eqFeedLikeUserId(Long id) {
        return id != null ? feedLike.user.id.eq(id) : null;
    }

    private OrderSpecifier<?> feedSort(SortPageType sortType) {
        if (sortType.getCode().equals(LATEST.getCode())) {
            return new OrderSpecifier<>(Order.ASC, feed.createdAt);
        }
        else if (sortType.getCode().equals(POPULAR.getCode())) {
            return new OrderSpecifier<>(Order.DESC, feed.likeCount);
        }
        return null;
    }

    private JPAQuery<FeedDetailsVO> selectFromFeed(Long userId) {
        return query
                .select(new QFeedDetailsVO(
                        new QCategoryVO(
                                feedCategory.category.id,
                                feedCategory.category.categoryName
                        ),
                        new QAuthorVO(
                                feed.user.id,
                                feed.user.nickname,
                                feed.user.birth,
                                feed.user.profileImageUrl
                        ),
                        feed.content,
                        feed.id,
                        feed.likeCount,
                        feed.bookmarkCount,
                        feed.commentCount,
                        feedLike.isNotNull(),
                        feedBookmark.isNotNull(),
                        feed.createdAt
                ))
                .from(feed)
                .leftJoin(feedCategory)
                .on(eqFeedCategoryId(feed.id))
                .leftJoin(feedBookmark)
                .on(eqFeedBookmarkId(feed.id).and(eqFeedBookmarkUserId(userId)))
                .leftJoin(feedLike)
                .on(eqFeedLikeId(feed.id).and(eqFeedLikeUserId(userId)));
    }

}
