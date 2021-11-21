package kitchenpos.order.ui.dto;

import kitchenpos.order.domain.OrderTable;

public class OrderTableResponse {

    private final Long id;
    private final int numberOfGuests;

    public OrderTableResponse(OrderTable orderTable) {
        id = orderTable.getId();
        numberOfGuests = orderTable.getNumberOfGuests();
    }

    public Long getId() {
        return id;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }
}
