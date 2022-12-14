package com.junhyeong.shoppingmall.services;

import com.junhyeong.shoppingmall.exceptions.ReviewWriteFailed;
import com.junhyeong.shoppingmall.models.Order;
import com.junhyeong.shoppingmall.models.vo.OrderProduct;
import com.junhyeong.shoppingmall.models.Review;
import com.junhyeong.shoppingmall.models.User;
import com.junhyeong.shoppingmall.models.vo.UserName;
import com.junhyeong.shoppingmall.repositories.OrderRepository;
import com.junhyeong.shoppingmall.repositories.ReviewRepository;
import com.junhyeong.shoppingmall.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateReviewServiceTest {
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private ReviewRepository reviewRepository;
    private CreateReviewService createReviewService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        orderRepository = mock(OrderRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        createReviewService = new CreateReviewService(
                userRepository,
                orderRepository, reviewRepository);
    }

    @Test
    void write() {
        UserName userName = new UserName("test123");
        given(userRepository.findByUserName(userName))
                .willReturn(Optional.of(User.fake(userName)));

        Long productId = 1L;

        Long orderId = 1L;
        Order order = Order.fake(orderId);
        order.toDelivered();

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        Long reviewId = 1L;
        given(reviewRepository.save(Review.fake(reviewId)))
                .willReturn(Review.fake(reviewId));

        Long optionId = 1L;

        OrderProduct orderProduct = OrderProduct.fake(productId, optionId);

        Review review = createReviewService.write(
                userName, 5.0D, "???????????? ????????????", orderId, "", orderProduct);

        assertThat(review).isNotNull();

        verify(userRepository).findByUserName(userName);
        verify(orderRepository).findById(orderId);
        verify(reviewRepository).save(review);
    }

    @Test
    void writeFailWithShippedOrder() {
        UserName userName = new UserName("test123");
        given(userRepository.findByUserName(userName))
                .willReturn(Optional.of(User.fake(userName)));

        Long productId = 1L;

        Long orderId = 1L;
        Order order = Order.fake(orderId);

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        Long optionId = 1L;

        OrderProduct orderProduct = OrderProduct.fake(productId, optionId);

        assertThrows(ReviewWriteFailed.class, () -> {
            createReviewService.write(userName, 5.0D, "???????????? ????????????",  orderId, "", orderProduct);
        });
    }

    @Test
    void writeFailWithExistReview() {
        UserName userName = new UserName("test123");
        given(userRepository.findByUserName(userName))
                .willReturn(Optional.of(User.fake(userName)));

        Long productId = 1L;

        Long orderId = 1L;
        Order order = Order.fake(orderId);
        order.toDelivered();

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        Long optionId = 1L;

        OrderProduct orderProduct = OrderProduct.fake(productId, optionId);

        given(reviewRepository.exists(any(Specification.class)))
                .willThrow(new ReviewWriteFailed("?????? ????????? ???????????????"));

        assertThrows(ReviewWriteFailed.class, () -> {
            createReviewService.write(userName, 5.0D, "???????????? ????????????",  orderId, "", orderProduct);
        });
    }
}
