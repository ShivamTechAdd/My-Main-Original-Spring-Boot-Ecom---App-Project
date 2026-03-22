package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Payload.CategoryDTO;
import com.ecomerce.sbecom.Payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getallCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long id);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long id);

}
