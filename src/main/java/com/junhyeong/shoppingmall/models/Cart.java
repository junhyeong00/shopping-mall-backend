package com.junhyeong.shoppingmall.models;

import com.junhyeong.shoppingmall.dtos.CartDto;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Cart {
    private String items;

    public Cart() {
    }

    public Cart(String items) {
        this.items = items;
    }

    public String items() {
        return items;
    }

    public CartDto toDto() {
        return new CartDto(items);
    }

    @Override
    public boolean equals(Object other) {
        return other != null &&
                other.getClass() == Cart.class &&
                Objects.equals(this.items, ((Cart) other).items());
    }
}
