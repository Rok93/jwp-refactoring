package kitchenpos.order.ui.dto;

public class ChangeOrderTableRequest {

    private boolean empty;

    public ChangeOrderTableRequest(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }
}
