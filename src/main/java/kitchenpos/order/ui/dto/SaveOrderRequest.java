package kitchenpos.order.ui.dto;

import kitchenpos.order.domain.OrderLineItem;

import java.util.List;
import java.util.stream.Collectors;

public class SaveOrderRequest {

    private Long orderTableId;
    private List<SaveOrderLineItemRequest> orderLineItems;

    public SaveOrderRequest(Long orderTableId, List<OrderLineItem> orderLineItems) {
        this.orderTableId = orderTableId;
        this.orderLineItems = orderLineItems.stream()
                .map(SaveOrderLineItemRequest::new)
                .collect(Collectors.toList());
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public List<SaveOrderLineItemRequest> getOrderLineItems() {
        return orderLineItems;
    }
}
