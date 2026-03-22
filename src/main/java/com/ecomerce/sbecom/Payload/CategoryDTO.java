package com.ecomerce.sbecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {   //Request;
    private Long categoryId;
    private String categoryName;

}
