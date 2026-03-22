package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Exceptions.ApiException;
import com.ecomerce.sbecom.Exceptions.ResourceNotFoundException;
import com.ecomerce.sbecom.Model.Category;
import com.ecomerce.sbecom.Payload.CategoryDTO;
import com.ecomerce.sbecom.Payload.CategoryResponse;
import com.ecomerce.sbecom.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getallCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber,pageSize,sortByAndOrder);       //interface
        Page<Category> categoryPage = categoryRepository.findAll(pageDetail);

        List<Category> ls = categoryPage.getContent();

//        List<Category> ls = categoryRepository.findAll(); //Reciving Entity  earlier we use it

        if(ls.isEmpty()) throw new ApiException("No category exist currently!!!");

        List<CategoryDTO> categoryDTOS = ls.stream()      // Converting entity to Dto
                .map(category -> modelMapper.map(category,CategoryDTO.class))
                .collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages((long) categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDb!=null)
            throw new ApiException("Category with the name "+categoryDTO.getCategoryName()+" already exists  !!!!");

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ApiException("Resource Not Found with id: "+id));
        categoryRepository.delete(category);
        return modelMapper.map(category,CategoryDTO.class);
    }

    // new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found")) //build in Exception

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id) {
        Optional<Category> categories = categoryRepository.findById(id);
        Category savedCategory = categories.orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",id));

        Category category = modelMapper.map(categoryDTO,Category.class);
        category.setCategoryId(id);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

}
