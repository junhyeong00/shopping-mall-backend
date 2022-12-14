package com.junhyeong.shoppingmall.services;

import com.junhyeong.shoppingmall.exceptions.UserNotFound;
import com.junhyeong.shoppingmall.models.vo.Cart;
import com.junhyeong.shoppingmall.models.User;
import com.junhyeong.shoppingmall.models.vo.UserName;
import com.junhyeong.shoppingmall.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UpdateCartService {
    private final UserRepository userRepository;

    public UpdateCartService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateCart(UserName userName, Cart cart) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(UserNotFound::new);

        user.updateCart(cart);
    }
}
