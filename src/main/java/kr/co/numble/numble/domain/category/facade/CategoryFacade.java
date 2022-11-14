package kr.co.numble.numble.domain.category.facade;

import kr.co.numble.numble.domain.category.entity.Category;
import kr.co.numble.numble.domain.category.exception.FeedCategoryNotFoundException;
import kr.co.numble.numble.domain.category.repository.CategoryRepository;
import kr.co.numble.numble.global.error.exception.NumbleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryFacade {
    private final CategoryRepository categoryRepository;

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> FeedCategoryNotFoundException.EXCEPTION);
    }
}