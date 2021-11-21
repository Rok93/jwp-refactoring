package kitchenpos.order.application;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.order.domain.*;
import kitchenpos.order.ui.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 서비스 테스트")
@IntegrationTest
class OrderServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private OrderTable orderTable;
    private OrderLineItem validOrderLineItem;
    private Menu menu;

    @BeforeEach
    void setUp() {
        orderTable = registerOrderTable(false);
        menu = testMenu(1L, "후라이드치킨", BigDecimal.valueOf(16_000));
        validOrderLineItem = new OrderLineItem(this.menu, 1);
    }

    @Nested
    @IntegrationTest
    @DisplayName("[주문 추가]")
    class CreateOrder {

        @DisplayName("성공")
        @Test
        void create() {
            //when
            SaveOrderResponse actual = registerOrder();

            //then
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING);
            assertThat(actual.getOrderTable().getId()).isEqualTo(orderTable.getId());
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING);
            assertThat(actual.getOrderLineItems()).hasSize(1);
        }

        @DisplayName("실패 - 주문 항목이 비어있는 경우")
        @Test
        void createWhenOrderLineItemsIsEmpty() { // todo: Order의 생성자로 OrderLineItem을 집어넣는 것이 아니라면... 😅 예외처리가 안 될 것이다.
            //given
            List<OrderLineItem> emptyOrderLineItems = Collections.emptyList();

            //when //then
            assertThatThrownBy(() -> registerOrder(orderTable, emptyOrderLineItems))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 주문 항목들 중에 존재하지 않는 주문 항목이 있는 경우")
        @Test
        void createWhenContainsNotExistOrderLineItem() {
            //given
            OrderLineItem invalidOrderLineItem = new OrderLineItem(testMenu(100L, "황천의 뒤틀린 닭튀김", BigDecimal.valueOf(100_000)), 1);
            List<OrderLineItem> invalidOrderLineItems = Collections.singletonList(invalidOrderLineItem);

            //when //then
            assertThatThrownBy(() -> registerOrder(orderTable, invalidOrderLineItems))
                    .isExactlyInstanceOf(IllegalArgumentException.class); //todo: cascade 속성으로 PERSIST 시킬 때, 에러가 발생하면 UnsupportedOperationException이 발생!
        }

        @DisplayName("실패 - 존재하지 않는 주문 테이블인 경우")
        @Test
        void createWhenNotExistOrderTable() {
            //given
            OrderTable notRegisteredOrderTable = OrderTable.of();
            notRegisteredOrderTable.setId(100L);

            //when //then
            assertThatThrownBy(() -> registerOrder(notRegisteredOrderTable))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 주문 테이블이 empty인 경우")
        @Test
        void createWhenOrderTableIsEmpty() {
            //given
            OrderTable emptyOrderTable = registerOrderTable(true);

            //when //then
            assertThatThrownBy(() -> registerOrder(emptyOrderTable))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @IntegrationTest
    @DisplayName("[주문 상태 변경]")
    class ChangeOrderStatus {

        @DisplayName("성공")
        @Test
        void changeOrderStatus() {
            //given
            SaveOrderResponse savedOrder = registerOrder();
            OrderStatus changedOrderStatus = OrderStatus.MEAL;

            //when
            ChangeOrderResponse actual = orderService.changeOrderStatus(savedOrder.getId(), changedOrderStatus);

            //then
            assertThat(actual.getId()).isEqualTo(savedOrder.getId());
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.MEAL);
            assertThat(actual.getOrderTable().getId()).isEqualTo(orderTable.getId());
        }

        @DisplayName("실패 - 이미 '계산 완료'된 주문인 경우")
        @Test
        void changeOrderStatusWhenAlreadyOrderStatusIsCOMPLETION() {
            //given
            SaveOrderResponse completionOrder = registerOrder();
            orderService.changeOrderStatus(completionOrder.getId(), OrderStatus.COMPLETION);

            OrderStatus changedOrderStatus = OrderStatus.MEAL;

            //when //then
            assertThatThrownBy(() -> orderService.changeOrderStatus(completionOrder.getId(), changedOrderStatus))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 등록되어있지 않은 주문인 경우")
        @Test
        void changeOrderStatusWhenNotRegisteredOrderStatusIsCOMPLETION() {
            //given
            Order notRegisteredOrder = new Order(100L, orderTable, OrderStatus.COMPLETION,
                    Collections.singletonList(new OrderLineItem(menu, 1L)));

            OrderStatus changedOrderStatus = OrderStatus.MEAL;

            //when //then
            assertThatThrownBy(() -> orderService.changeOrderStatus(notRegisteredOrder.getId(), changedOrderStatus))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("[주문 조회] 성공")
    @Test
    void list() {
        //given
        registerOrder();

        //when
        List<OrderResponse> actual = orderService.list();

        //then
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getId()).isNotNull();
        assertThat(actual.get(0).getOrderTable().getId()).isEqualTo(orderTable.getId());
        assertThat(actual.get(0).getOrderTable().getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        assertThat(actual.get(0).getOrderStatus()).isEqualTo(OrderStatus.COOKING);
    }

    private Menu testMenu(Long id, String name, BigDecimal price) {
        return new Menu(id, name, price, new MenuGroup(2L, "한마리메뉴"));
    }

    private SaveOrderResponse registerOrder(OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        return orderService.create(new SaveOrderRequest(orderTable.getId(), orderLineItems));
    }

    private SaveOrderResponse registerOrder(OrderTable orderTable) {
        return registerOrder(orderTable, Arrays.asList(validOrderLineItem));
    }

    private SaveOrderResponse registerOrder() {
        return registerOrder(orderTable);
    }

    private OrderTable registerOrderTable(boolean empty) {
        SaveOrderTableRequest request = new SaveOrderTableRequest(0, empty);
        return tableService.create(request);
    }
}
