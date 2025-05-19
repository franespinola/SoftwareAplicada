package com.jhipster.demo.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jhipster.demo.store.domain.ProductCategory;
import com.jhipster.demo.store.repository.ProductCategoryRepository;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    private ProductCategoryService productCategoryService;

    private ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategoryService = new ProductCategoryService(productCategoryRepository);

        productCategory = new ProductCategory();
        productCategory.setId(1L);
        productCategory.setName("Electronics");
        productCategory.setDescription("Electronic devices and gadgets");
    }

    @Test
    void testSaveProductCategory() {
        // given
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

        // when
        ProductCategory savedCategory = productCategoryService.save(productCategory);

        // then
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Electronics");
        assertThat(savedCategory.getDescription()).isEqualTo("Electronic devices and gadgets");
        verify(productCategoryRepository, times(1)).save(productCategory);
    }

    @Test
    void testFindOne() {
        // given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // when
        Optional<ProductCategory> result = productCategoryService.findOne(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Electronics");
        verify(productCategoryRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllProductCategories() {
        // given
        List<ProductCategory> categories = new ArrayList<>();
        categories.add(productCategory);
        
        ProductCategory category2 = new ProductCategory();
        category2.setId(2L);
        category2.setName("Clothing");
        category2.setDescription("Apparel and accessories");
        categories.add(category2);
        
        Page<ProductCategory> page = new PageImpl<>(categories);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(productCategoryRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<ProductCategory> result = productCategoryService.findAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Electronics");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Clothing");
        verify(productCategoryRepository, times(1)).findAll(pageable);
    }
}
