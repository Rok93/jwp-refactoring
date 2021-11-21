package kitchenpos.product.application;

import kitchenpos.product.domain.Product;
import kitchenpos.product.dto.SaveProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 서비스 테스트")
@IntegrationTest
class ProductServiceTest {

    private static final String PRODUCT_NAME = "스타벅스 돌체라떼";
    private static final int PRODUCT_PRICE = 5_600;

    @Autowired
    private ProductService productService;

    @Nested
    @IntegrationTest
    @DisplayName("[상품 등록]")
    class CreateProduct {

        @DisplayName("성공")
        @Test
        void create() {
            //given
            Product savedProduct = registerProduct();

            //then
            assertThat(savedProduct).isNotNull();
            assertThat(savedProduct.getPrice().intValue()).isEqualTo(PRODUCT_PRICE);
            assertThat(savedProduct.getName()).isEqualTo(PRODUCT_NAME);
        }

        @DisplayName("실패 - 상품의 가격이 0원 미만인 경우")
        @Test
        void createWhenPriceIsNegative() {
            //given
            int invalidPrice = -1_000;

            // when //then
            assertThatThrownBy(() -> registerProduct(invalidPrice))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

        }
    }

    @DisplayName("[상품 조회] - 성공")
    @Test
    void list() {
        //when
        List<Product> actual = productService.list();

        //then
        assertThat(actual).hasSize(6);
    }

    private Product registerProduct(String name, int price) {
        SaveProductRequest request = new SaveProductRequest(name, BigDecimal.valueOf(price));
        return productService.create(request);
    }

    private Product registerProduct(int price) {
        return registerProduct(PRODUCT_NAME, price);
    }

    private Product registerProduct() {
        return registerProduct(PRODUCT_PRICE);
    }
}

