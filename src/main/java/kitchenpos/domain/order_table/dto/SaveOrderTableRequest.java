package kitchenpos.domain.order_table.dto;

import kitchenpos.domain.OrderTable;

public class SaveOrderTableRequest {

    private int numberOfGuests;
    private boolean empty;

    public SaveOrderTableRequest(int numberOfGuests, boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public OrderTable toEntity() {
        return new OrderTable(numberOfGuests, empty);
    }
}
