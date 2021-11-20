package kitchenpos.domain.order_table.dto;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;

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
