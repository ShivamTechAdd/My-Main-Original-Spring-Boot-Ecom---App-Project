package com.ecomerce.sbecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemsDto {
    private Long cartItemId;
    private CategoryDTO cart;
    private ProductDTO product;
    private Integer quantity;
    private Double discout ;
    private Double productPrice;

}
