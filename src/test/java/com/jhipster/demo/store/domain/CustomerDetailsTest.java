package com.jhipster.demo.store.domain;

import static com.jhipster.demo.store.domain.CustomerDetailsTestSamples.*;
import static com.jhipster.demo.store.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jhipster.demo.store.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerDetails.class);
        CustomerDetails customerDetails1 = getCustomerDetailsSample1();
        CustomerDetails customerDetails2 = new CustomerDetails();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);

        customerDetails2.setId(customerDetails1.getId());
        assertThat(customerDetails1).isEqualTo(customerDetails2);

        customerDetails2 = getCustomerDetailsSample2();
        assertThat(customerDetails1).isNotEqualTo(customerDetails2);
    }

    @Test
    void cartTest() {
        CustomerDetails customerDetails = getCustomerDetailsRandomSampleGenerator();
        ShoppingCart shoppingCartBack = getShoppingCartRandomSampleGenerator();

        customerDetails.addCart(shoppingCartBack);
        assertThat(customerDetails.getCarts()).containsOnly(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomerDetails()).isEqualTo(customerDetails);

        customerDetails.removeCart(shoppingCartBack);
        assertThat(customerDetails.getCarts()).doesNotContain(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomerDetails()).isNull();

        customerDetails.carts(new HashSet<>(Set.of(shoppingCartBack)));
        assertThat(customerDetails.getCarts()).containsOnly(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomerDetails()).isEqualTo(customerDetails);

        customerDetails.setCarts(new HashSet<>());
        assertThat(customerDetails.getCarts()).doesNotContain(shoppingCartBack);
        assertThat(shoppingCartBack.getCustomerDetails()).isNull();
    }
}
