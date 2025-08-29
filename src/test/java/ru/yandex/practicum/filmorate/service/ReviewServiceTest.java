package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundReactionException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundReviewException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для сервиса отзывов (ReviewService)
 * Проверяют бизнес-логику работы с отзывами
 */
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        user1 = new User("user1@mail.ru", "user1", "User One", LocalDate.of(1990, 1, 1));
        user2 = new User("user2@mail.ru", "user2", "User Two", LocalDate.of(1990, 1, 1));
        user3 = new User("user3@mail.ru", "user3", "User Three", LocalDate.of(1990, 1, 1));

        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(120);
        film2.setMpa(new Mpa(1, "G"));

        film1 = filmService.addFilm(film1);
        film2 = filmService.addFilm(film2);
    }

    @Test
    void testAddValidReview() {
        Review review = Review.builder()
                .content("Excellent film with great acting")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review result = reviewService.addReview(review);

        assertNotNull(result.getReviewId());
        assertEquals("Excellent film with great acting", result.getContent());
        assertTrue(result.getIsPositive());
        assertEquals(0, result.getUseful());
    }

    @Test
    void testAddReviewWithEmptyContent() {
        Review review = Review.builder()
                .content("")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        assertThrows(ValidationException.class, () -> reviewService.addReview(review));
    }

    @Test
    void testAddReviewWithNullContent() {
        Review review = Review.builder()
                .content(null)
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        assertThrows(ValidationException.class, () -> reviewService.addReview(review));
    }

    @Test
    void testAddReviewWithNullIsPositive() {
        Review review = Review.builder()
                .content("Good film")
                .isPositive(null)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        assertThrows(ValidationException.class, () -> reviewService.addReview(review));
    }

    @Test
    void testAddReviewWithNonExistentUser() {
        Review review = Review.builder()
                .content("Good film")
                .isPositive(true)
                .userId(999L)
                .filmId(film1.getId())
                .build();

        assertThrows(NotFoundUserByIdException.class, () -> reviewService.addReview(review));
    }

    @Test
    void testAddReviewWithNonExistentFilm() {
        Review review = Review.builder()
                .content("Good film")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(999L)
                .build();

        assertThrows(NotFoundFilmException.class, () -> reviewService.addReview(review));
    }

    @Test
    void testUpdateReview() {
        // Создаем отзыв
        Review review = Review.builder()
                .content("Original review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        created.setContent("Updated review content");
        created.setIsPositive(false);

        Review updated = reviewService.updateReview(created);

        assertEquals("Updated review content", updated.getContent());
        assertFalse(updated.getIsPositive());
        assertEquals(created.getReviewId(), updated.getReviewId());
    }

    @Test
    void testUpdateReviewWithDifferentUser() {
        Review review = Review.builder()
                .content("Original review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        created.setUserId(user2.getId());

    }

    @Test
    void testDeleteReview() {
        Review review = Review.builder()
                .content("Review to delete")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.deleteReview(created.getReviewId());

        assertThrows(NotFoundReviewException.class, () -> reviewService.getReviewById(created.getReviewId()));
    }

    @Test
    void testGetReviewsByFilmId() {
        Review review1 = Review.builder()
                .content("Review 1")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review review2 = Review.builder()
                .content("Review 2")
                .isPositive(false)
                .userId(user2.getId())
                .filmId(film1.getId())
                .build();

        reviewService.addReview(review1);
        reviewService.addReview(review2);

        List<Review> reviews = reviewService.getReviews(film1.getId(), 10);

        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().anyMatch(r -> r.getUserId().equals(user1.getId())));
        assertTrue(reviews.stream().anyMatch(r -> r.getUserId().equals(user2.getId())));
    }

    @Test
    void testGetAllReviews() {
        Review review1 = Review.builder()
                .content("Review for film1")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review review2 = Review.builder()
                .content("Review for film2")
                .isPositive(false)
                .userId(user2.getId())
                .filmId(film2.getId())
                .build();

        reviewService.addReview(review1);
        reviewService.addReview(review2);

        List<Review> reviews = reviewService.getReviews(null, 10);

        assertEquals(2, reviews.size());
    }

    @Test
    void testAddLikeToReview() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.addLike(created.getReviewId(), user2.getId());

        Review updated = reviewService.getReviewById(created.getReviewId());
        assertEquals(1, updated.getUseful());
    }

    @Test
    void testAddDislikeToReview() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.addDislike(created.getReviewId(), user2.getId());

        Review updated = reviewService.getReviewById(created.getReviewId());
        assertEquals(-1, updated.getUseful());
    }

    @Test
    void testReplaceReaction() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.addLike(created.getReviewId(), user2.getId());
        Review afterLike = reviewService.getReviewById(created.getReviewId());
        assertEquals(1, afterLike.getUseful());

        // Заменяем лайк на дизлайк - система должна разрешить это
        reviewService.addDislike(created.getReviewId(), user2.getId());
        Review afterDislike = reviewService.getReviewById(created.getReviewId());
        assertEquals(-1, afterDislike.getUseful());
    }

    @Test
    void testRemoveReaction() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.addLike(created.getReviewId(), user2.getId());

        reviewService.removeReaction(created.getReviewId(), user2.getId());

        Review updated = reviewService.getReviewById(created.getReviewId());
        assertEquals(0, updated.getUseful());
    }

    @Test
    void testRemoveNonExistentReaction() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        assertThrows(NotFoundReactionException.class,
                () -> reviewService.removeReaction(created.getReviewId(), user2.getId()));
    }

    @Test
    void testReviewSortingByUseful() {
        Review review1 = Review.builder()
                .content("Review with low useful")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review review2 = Review.builder()
                .content("Review with high useful")
                .isPositive(true)
                .userId(user2.getId())
                .filmId(film1.getId())
                .build();

        Review created1 = reviewService.addReview(review1);
        Review created2 = reviewService.addReview(review2);

        reviewService.addLike(created2.getReviewId(), user1.getId());
        reviewService.addLike(created2.getReviewId(), user3.getId());

        reviewService.addDislike(created1.getReviewId(), user2.getId());

        List<Review> reviews = reviewService.getReviews(film1.getId(), 10);

        assertEquals(2, reviews.size());
        assertEquals(created2.getReviewId(), reviews.get(0).getReviewId());
        assertTrue(reviews.get(0).getUseful() > reviews.get(1).getUseful());
    }

    @Test
    void testReactionReplacementLogic() {
        // Создаем отзыв
        Review review = Review.builder()
                .content("Test review for reaction replacement")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review created = reviewService.addReview(review);

        reviewService.addLike(created.getReviewId(), user2.getId());
        Review afterFirstLike = reviewService.getReviewById(created.getReviewId());
        assertEquals(1, afterFirstLike.getUseful());

        reviewService.addLike(created.getReviewId(), user3.getId());
        Review afterSecondLike = reviewService.getReviewById(created.getReviewId());
        assertEquals(2, afterSecondLike.getUseful());

        reviewService.addDislike(created.getReviewId(), user2.getId());
        Review afterDislike = reviewService.getReviewById(created.getReviewId());
        assertEquals(0, afterDislike.getUseful()); // 2 - 1 (убрали лайк) - 1 (добавили дизлайк) = 0

        reviewService.removeReaction(created.getReviewId(), user3.getId());
        Review afterRemove = reviewService.getReviewById(created.getReviewId());
        assertEquals(-1, afterRemove.getUseful()); // 0 - 1 (убрали лайк) = -1
    }
}