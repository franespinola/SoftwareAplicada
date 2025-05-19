package com.jhipster.demo.store.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

import com.jhipster.demo.store.domain.Product;
import com.jhipster.demo.store.domain.ProductCategory;
import com.jhipster.demo.store.domain.enumeration.Size;
import com.jhipster.demo.store.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);

        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic devices");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setProductSize(Size.M);
        product.setProductCategory(category);
    }

    @Test
    void testSaveProduct() {
        // given
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product savedProduct = productService.save(product);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("99.99"));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testFindAllProducts() {
        // given
        List<Product> products = new ArrayList<>();
        products.add(product);
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setPrice(new BigDecimal("49.99"));
        product2.setProductSize(Size.L);
        products.add(product2);
        
        Page<Product> page = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(productRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<Product> result = productService.findAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Another Product");
        verify(productRepository, times(1)).findAll(pageable);
    }
}
