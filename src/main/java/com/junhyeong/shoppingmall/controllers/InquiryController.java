package com.junhyeong.shoppingmall.controllers;

import com.junhyeong.shoppingmall.dtos.InquiriesDto;
import com.junhyeong.shoppingmall.dtos.InquiryRequestDto;
import com.junhyeong.shoppingmall.dtos.InquiryResultDto;
import com.junhyeong.shoppingmall.dtos.UpdateInquiryDto;
import com.junhyeong.shoppingmall.models.vo.UserName;
import com.junhyeong.shoppingmall.services.CreateInquiryService;
import com.junhyeong.shoppingmall.services.DeleteInquiryService;
import com.junhyeong.shoppingmall.services.GetInquiresService;
import com.junhyeong.shoppingmall.services.UpdateInquiryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InquiryController {
    private final CreateInquiryService createInquiryService;
    private final GetInquiresService getInquiresService;
    private final DeleteInquiryService deleteInquiryService;
    private final UpdateInquiryService updateInquiryService;

    public InquiryController(CreateInquiryService createInquiryService,
                             GetInquiresService getInquiresService,
                             DeleteInquiryService deleteInquiryService,
                             UpdateInquiryService updateInquiryService) {
        this.createInquiryService = createInquiryService;
        this.getInquiresService = getInquiresService;
        this.deleteInquiryService = deleteInquiryService;
        this.updateInquiryService = updateInquiryService;
    }

    @GetMapping("products/{productId}/inquiries")
    public InquiriesDto inquiries(
            @PathVariable("productId") Long productId,
            @RequestAttribute("userName") UserName userName,
            @PageableDefault(size = 8, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return getInquiresService.inquiries(productId, userName, pageable);
    }

    @PostMapping("inquiry")
    @ResponseStatus(HttpStatus.CREATED)
    public InquiryResultDto write(
            @RequestAttribute("userName") UserName userName,
            @RequestBody InquiryRequestDto inquiryRequestDto
    ) {
        return createInquiryService.write(
                userName,
                inquiryRequestDto.getProductId(),
                inquiryRequestDto.getTitle(),
                inquiryRequestDto.getContent(),
                inquiryRequestDto.getIsSecret()
        );
    }

    @DeleteMapping("inquiries/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestAttribute("userName") UserName userName,
            @PathVariable("id") Long inquiryId
            ) {
            deleteInquiryService.delete(userName, inquiryId);
    }

    @PatchMapping("inquiries/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @RequestBody UpdateInquiryDto updateInquiryDto,
            @RequestAttribute("userName") UserName userName,
            @PathVariable("id") Long inquiryId
    ) {
        updateInquiryService.update(
                userName,
                inquiryId,
                updateInquiryDto.getTitle(),
                updateInquiryDto.getContent(),
                updateInquiryDto.getIsSecret());
    }
}
