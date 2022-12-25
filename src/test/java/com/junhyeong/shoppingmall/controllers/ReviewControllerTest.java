package com.junhyeong.shoppingmall.controllers;

import com.junhyeong.shoppingmall.exceptions.ReviewWriteFailed;
import com.junhyeong.shoppingmall.models.Order;
import com.junhyeong.shoppingmall.models.OrderProduct;
import com.junhyeong.shoppingmall.models.Review;
import com.junhyeong.shoppingmall.models.UserName;
import com.junhyeong.shoppingmall.services.CreateReviewService;
import com.junhyeong.shoppingmall.services.GetReviewsService;
import com.junhyeong.shoppingmall.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateReviewService createReviewService;

    @MockBean
    private GetReviewsService getReviewsService;

    @SpyBean
    private JwtUtil jwtUtil;

    private String token;
    private UserName userName;

    @BeforeEach
    void setup() {
        userName = new UserName("test123");
        token = jwtUtil.encode(userName);
    }

    @Test
    void write() throws Exception {
        Long productId = 1L;
        Long orderId = 1L;
        Long reviewId = 1L;

        Order order = Order.fake(orderId);
        order.toDelivered();

        given(createReviewService.write(any(), any(), any(), any(), any(), any()))
                .willReturn(Review.fake(reviewId));

        mockMvc.perform(MockMvcRequestBuilders.post("/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"productId\":\"1\", " +
                                "\"orderId\":\"1\", " +
                                "\"rating\":\"5\", " +
                                "\"content\":\"부드럽고 따뜻해요\"" +
                                "}")
                )
                .andExpect(status().isCreated());

        verify(createReviewService).write(any(), any(), any(), any(), any(), any());
    }

    @Test
    void writeFailWithShippedOrder() throws Exception {
        given(createReviewService.write(any(), any(), any(), any(), any(), any()))
                .willThrow(new ReviewWriteFailed("배송완료된 상품만 리뷰를 작성할 수 있습니다"));

        mockMvc.perform(MockMvcRequestBuilders.post("/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"productId\":\"1\", " +
                                "\"orderId\":\"1\", " +
                                "\"rating\":\"5\", " +
                                "\"content\":\"부드럽고 따뜻해요\"" +
                                "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void writeFailWithExistReview() throws Exception {
        given(createReviewService.write(any(), any(), any(), any(), any(), any()))
                .willThrow(new ReviewWriteFailed("이미 작성한 리뷰입니다"));

        mockMvc.perform(MockMvcRequestBuilders.post("/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"productId\":\"1\", " +
                                "\"orderId\":\"1\", " +
                                "\"rating\":\"5\", " +
                                "\"content\":\"부드럽고 따뜻해요\"" +
                                "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void reviews() throws Exception {
        OrderProduct orderProduct = OrderProduct.fake(1L, 1L);

        List<Review> reviews = List.of(
                new Review(1L, 1L, 1L, orderProduct, 5D, "부드럽고 따뜻해요"),
                new Review(2L, 1L, 1L, orderProduct, 5D, "부드럽고 따뜻해요"),
                new Review(3L, 1L, 1L, orderProduct, 5D, "부드럽고 따뜻해요")
        );

        int page = 1;

        Pageable pageable = PageRequest.of(page - 1, 8);

        Page<Review> pageableReview = new PageImpl<>(reviews, pageable, reviews.size());

        Long productId = 1L;

        given(getReviewsService.reviews(productId, pageable))
                .willReturn(pageableReview);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/1/reviews"))
                .andExpect(status().isOk());
    }
}