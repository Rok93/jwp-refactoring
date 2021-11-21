package kitchenpos.product.ui.dto;

import kitchenpos.product.domain.Product;

import java.math.BigDecimal;

public class SaveProductRequest {

    private String name;
    private BigDecimal price;

    public SaveProductRequest(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public Product toEntity() {
        return new Product(name, price);
    }
}
