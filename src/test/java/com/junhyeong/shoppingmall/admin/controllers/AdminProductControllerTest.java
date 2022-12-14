package com.junhyeong.shoppingmall.admin.controllers;

import com.junhyeong.shoppingmall.admin.services.CreateProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
@ActiveProfiles("test")
class AdminProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProductService createProductService;

    @Test
    void createProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"productName\":\"가디건\"," +
                                "\"categoryId\":\"1\"," +
                                "\"price\":\"10000\"," +
                                "\"description\":\"부드럽다\"" +
                                "}"))
                .andExpect(status().isCreated());

        verify(createProductService).createProduct(any(), any(), any(), any(), any(), any());
    }
}
