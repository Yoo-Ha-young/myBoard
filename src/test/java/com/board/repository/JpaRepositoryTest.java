package com.board.repository;

import com.board.config.JpaConfig;
import com.board.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

//@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaRepositoryTest {


    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    JpaRepositoryTest(ArticleRepository articleRepository,
        ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }


    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given - 주어진 상황 (테스트 데이터가 없는 경우)

        // When - 실행했을 때
        List<Article> articles = articleRepository.findAll();

        // Then - 결과가 이러해야 함
        assertThat(articles).isNotNull().hasSize(2);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInsert_thenWorksFine() {
        // Given - 주어진 상황 (기존 데이터 개수)
        long previousCount = articleRepository.count();

        // When - 실행했을 때
        Article savedArticle = articleRepository.save(
            Article.of("new articles", "new content", "#spring"));

        // Then - 결과가 이러해야 함 (기존 데이터 개수 + 1)
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdate_thenWorksFine() {
        // Given - 주어진 상황 (기존 데이터 개수)
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);
        // When - 실행했을 때

        Article savedArticle = articleRepository.saveAndFlush(article);

        // Then - 결과가 이러해야 함 (기존 데이터 개수 + 1)
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDelete_thenWorksFine() {
        // Given - 주어진 상황 (기존 데이터 개수)
        Article article = articleRepository.findById(1L).orElseThrow();

        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // When - 실행했을 때
        articleRepository.delete(article);

        // Then - 결과가 이러해야 함
        assertThat(articleRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }
}
