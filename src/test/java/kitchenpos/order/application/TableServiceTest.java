package kitchenpos.order.application;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.dto.OrderTableResponse;
import kitchenpos.order.dto.SaveOrderTableRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 테이블 서비스 테스트")
@IntegrationTest
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderService orderService;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = testMenu(1L, "후라이드치킨", BigDecimal.valueOf(16_000));
    }

    @DisplayName("[주문 테이블 추가] 성공")
    @Test
    void testCreate() {
        //given //when
        OrderTable actual = registerOrderTable();

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(0);
        assertThat(actual.getTableGroup()).isNull();
    }

    @DisplayName("[주문 테이블 empty로 변경] - 성공")
    @Test
    void testChangeEmpty() {
        //given
        OrderTable orderTable = registerOrderTable();

        //when
        OrderTable actual = tableService.changeEmpty(orderTable.getId());

        //then
        assertThat(actual.getId()).isEqualTo(orderTable.getId());
        assertThat(actual.getTableGroup()).isNull();
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("[주문 테이블 empty로 변경] - 실패 - '계산 완료'되지 않은 주문이 포함된 경우")
    @Test
    void testChangeEmptyWhenOrderTableContainNotCompletionOrder() {
        //given
        OrderTable orderTable = registerOrderTable(0, false);
        orderService.create(Order.of(orderTable, Arrays.asList(new OrderLineItem(menu, 1))));

        //when //then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[주문 테이블 '방문한 손님 수' 변경] - 성공")
    @Test
    void changeNumberOfGuests() {
        //given
        int changedNumberOfGuests = 2;
        OrderTable orderTable = registerOrderTable(0, false);

        //when
        OrderTable actual = tableService.changeNumberOfGuests(orderTable.getId(), changedNumberOfGuests);

        //then
        assertThat(actual.getId()).isEqualTo(orderTable.getId());
        assertThat(actual.getTableGroup()).isNull();
        assertThat(actual.isEmpty()).isFalse();
        assertThat(actual.getNumberOfGuests()).isEqualTo(changedNumberOfGuests);
    }

    @DisplayName("[주문 테이블 '방문한 손님 수' 변경] - 실패 - 0미만의 숫자를 입력하는 경우")
    @Test
    void changeNumberOfGuestsWhenNegativeNumber() {
        //given
        OrderTable orderTable = registerOrderTable(0, false);

        //when //then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(orderTable.getId(), -1))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    private OrderTable registerOrderTable(int numberOfGuests, boolean empty) {
        SaveOrderTableRequest request = new SaveOrderTableRequest(numberOfGuests, empty);
        return tableService.create(request);
    }

    private OrderTable registerOrderTable() {
        return registerOrderTable(0, true);
    }

    @DisplayName("[주문 테이블 조회] 성공")
    @Test
    void testList() {
        //when
        List<OrderTableResponse> actual = tableService.list();

        //then
        assertThat(actual).hasSize(8);
    }

    private Menu testMenu(Long id, String name, BigDecimal price) {
        return new Menu(id, name, price, new MenuGroup(2L, "한마리메뉴"));
    }
}
