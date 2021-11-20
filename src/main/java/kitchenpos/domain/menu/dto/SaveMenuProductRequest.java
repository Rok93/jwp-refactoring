package kitchenpos.domain.menu.dto;

import kitchenpos.domain.MenuProduct;

public class SaveMenuProductRequest {

    private Long productId;
    private Long quantity;

    public SaveMenuProductRequest(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
