package kitchenpos.order.ui.dto;

import kitchenpos.order.domain.OrderLineItem;

public class SaveOrderLineItemRequest {

    private Long menuId;
    private long quantity;

    public SaveOrderLineItemRequest(Long menuId, long quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public SaveOrderLineItemRequest(OrderLineItem orderLineItem) {
        this(orderLineItem.getMenuId(), orderLineItem.getQuantity());
    }

    public Long getMenuId() {
        return menuId;
    }

    public long getQuantity() {
        return quantity;
    }
}
