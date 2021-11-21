package kitchenpos.order.ui.dto;

import java.util.List;

public class SaveOrderTableGroupRequest {

    private List<Long> orderTables;

    public SaveOrderTableGroupRequest(List<Long> orderTables) {
        this.orderTables = orderTables;
    }

    public List<Long> getOrderTables() {
        return orderTables;
    }
}
