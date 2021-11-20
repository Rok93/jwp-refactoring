package kitchenpos.domain.table_group.dto;

import java.util.List;

public class SaveTableGroupRequest {

    private List<Long> orderTables;

    public SaveTableGroupRequest(List<Long> orderTables) {
        this.orderTables = orderTables;
    }

    public List<Long> getOrderTables() {
        return orderTables;
    }
}
