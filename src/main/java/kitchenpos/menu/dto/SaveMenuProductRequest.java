package kitchenpos.menu.dto;

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
