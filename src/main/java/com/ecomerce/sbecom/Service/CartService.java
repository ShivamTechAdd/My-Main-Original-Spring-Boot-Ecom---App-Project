package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Payload.CartDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    public CartDto addProductToCart(Long productId, Integer quantity);

    List<CartDto> getAllCarts();

    CartDto getCart(String emailId, Long cartId);

    @Transactional
    CartDto updateProdeuctQuantityInCart(Long productId, Integer quantity);
    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCart(Long cartId, Long productId);

}
