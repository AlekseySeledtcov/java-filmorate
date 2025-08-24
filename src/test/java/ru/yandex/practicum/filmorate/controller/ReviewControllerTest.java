package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для контроллера отзывов (ReviewController)
 * Проверяют работу всех эндпоинтов API отзывов
 */
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerTest {

    @Autowired
    private ReviewController reviewController;

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
    void testCreateReview() {
        Review review = Review.builder()
                .content("Great film!")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        assertNotNull(createdReview.getReviewId());
        assertEquals("Great film!", createdReview.getContent());
        assertTrue(createdReview.getIsPositive());
        assertEquals(user1.getId(), createdReview.getUserId());
        assertEquals(film1.getId(), createdReview.getFilmId());
        assertEquals(0, createdReview.getUseful()); // Полезность должна быть 0 при создании
    }

    @Test
    void testGetReviewById() {
        Review review = Review.builder()
                .content("Good film")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        Review retrievedReview = reviewController.getReviewById(createdReview.getReviewId());

        assertEquals(createdReview.getReviewId(), retrievedReview.getReviewId());
        assertEquals("Good film", retrievedReview.getContent());
    }

    @Test
    void testUpdateReview() {
        Review review = Review.builder()
                .content("Original content")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        createdReview.setContent("Updated content");
        createdReview.setIsPositive(false);

        Review updatedReview = reviewController.updateReview(createdReview);

        assertEquals("Updated content", updatedReview.getContent());
        assertFalse(updatedReview.getIsPositive());
    }

    @Test
    void testDeleteReview() {
        Review review = Review.builder()
                .content("To be deleted")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        reviewController.deleteReview(createdReview.getReviewId());

        assertThrows(Exception.class, () -> reviewController.getReviewById(createdReview.getReviewId()));
    }

    @Test
    void testGetReviewsByFilmId() {
        // Создаем отзывы для film1
        Review review1 = Review.builder()
                .content("Review 1 for film1")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review review2 = Review.builder()
                .content("Review 2 for film1")
                .isPositive(false)
                .userId(user2.getId())
                .filmId(film1.getId())
                .build();

        reviewController.addReview(review1);
        reviewController.addReview(review2);

        List<Review> reviews = reviewController.getReviews(film1.getId(), 10);

        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().anyMatch(r -> r.getContent().contains("Review 1")));
        assertTrue(reviews.stream().anyMatch(r -> r.getContent().contains("Review 2")));
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

        reviewController.addReview(review1);
        reviewController.addReview(review2);

        List<Review> reviews = reviewController.getReviews(null, 10);

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

        Review createdReview = reviewController.addReview(review);

        reviewController.addLike(createdReview.getReviewId(), user2.getId());

        Review updatedReview = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(1, updatedReview.getUseful());
    }

    @Test
    void testAddDislikeToReview() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        reviewController.addDislike(createdReview.getReviewId(), user2.getId());

        Review updatedReview = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(-1, updatedReview.getUseful());
    }

    @Test
    void testRemoveLikeFromReview() {
        Review review = Review.builder()
                .content("Test review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        reviewController.addLike(createdReview.getReviewId(), user2.getId());

        reviewController.removeLike(createdReview.getReviewId(), user2.getId());

        Review updatedReview = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(0, updatedReview.getUseful());
    }

    @Test
    void testReviewUsefulRatingCalculation() {
        // Создаем отзыв
        Review review = Review.builder()
                .content("Test review for rating")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build();

        Review createdReview = reviewController.addReview(review);

        reviewController.addLike(createdReview.getReviewId(), user2.getId());
        Review afterLike = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(1, afterLike.getUseful()); // Должно быть +1

        reviewController.addLike(createdReview.getReviewId(), user3.getId());
        Review afterSecondLike = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(2, afterSecondLike.getUseful()); // Должно быть +2

        reviewController.addDislike(createdReview.getReviewId(), user2.getId());
        Review afterDislike = reviewController.getReviewById(createdReview.getReviewId());
        assertEquals(0, afterDislike.getUseful());
    }

    @Test
    void testGetReviewsSortedByUseful() {
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

        Review createdReview1 = reviewController.addReview(review1);
        Review createdReview2 = reviewController.addReview(review2);

        reviewController.addLike(createdReview2.getReviewId(), user1.getId());
        reviewController.addLike(createdReview2.getReviewId(), user3.getId());
        Review afterLikes = reviewController.getReviewById(createdReview2.getReviewId());
        assertEquals(2, afterLikes.getUseful());

        reviewController.addDislike(createdReview1.getReviewId(), user2.getId());
        Review afterDislike = reviewController.getReviewById(createdReview1.getReviewId());
        assertEquals(-1, afterDislike.getUseful());

        List<Review> reviews = reviewController.getReviews(film1.getId(), 10);

        assertEquals(2, reviews.size());
        assertEquals(createdReview2.getReviewId(), reviews.get(0).getReviewId());
        assertEquals(2, reviews.get(0).getUseful());
        assertEquals(-1, reviews.get(1).getUseful());
    }
}