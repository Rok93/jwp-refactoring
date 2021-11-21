package kitchenpos.order.ui.dto;

import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SaveOrderResponse {

    private Long id;
    private OrderTableResponse orderTable;
    private List<OrderLineItemResponse> orderLineItems;
    private OrderStatus orderStatus;
    private LocalDateTime orderedTime;

    public SaveOrderResponse(Order order) {
        id = order.getId();
        orderTable = new OrderTableResponse(order.getOrderTable());
        orderLineItems = order.getOrderLineItems().stream()
                .map(OrderLineItemResponse::new)
                .collect(Collectors.toList());
        orderStatus = order.getOrderStatus();
        orderedTime = order.getOrderedTime();
    }

    public Long getId() {
        return id;
    }

    public OrderTableResponse getOrderTable() {
        return orderTable;
    }

    public List<OrderLineItemResponse> getOrderLineItems() {
        return orderLineItems;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }
}
