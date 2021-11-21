package kitchenpos.order.dto;

import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.domain.TableGroup;

public class OrderTableResponse {

    private final Long id;
    private final TableGroup tableGroup; //todo: orderTables DTO로 감싸기
    private final int numberOfGuests;

    public OrderTableResponse(OrderTable orderTable) {
        id = orderTable.getId();
        tableGroup = orderTable.getTableGroup();
        numberOfGuests = orderTable.getNumberOfGuests();
    }
}
