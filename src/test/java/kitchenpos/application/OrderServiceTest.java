package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.domain.order_table.dto.SaveOrderTableRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.math.BigDecimal;
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
            Order actual = registerOrder();

            //then
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
            assertThat(actual.getOrderTableId()).isEqualTo(orderTable.getId());
            assertThat(actual.getOrderLineItems()).hasSize(1);
        }

        @DisplayName("실패 - 주문 항목이 비어있는 경우")
        @Test
        void createWhenOrderLineItemsIsEmpty() {
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
                    .isExactlyInstanceOf(IllegalArgumentException.class);
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
            Order savedOrder = registerOrder();
            String changedOrderStatus = OrderStatus.MEAL.name();

            //when
            Order actual = orderService.changeOrderStatus(savedOrder.getId(), changedOrderStatus);

            //then
            assertThat(actual.getId()).isEqualTo(savedOrder.getId());
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
            assertThat(actual.getOrderTableId()).isEqualTo(orderTable.getId());
            assertThat(actual.getOrderLineItems()).hasSize(1);
        }

        @DisplayName("실패 - 이미 '계산 완료'된 주문인 경우")
        @Test
        void changeOrderStatusWhenAlreadyOrderStatusIsCOMPLETION() {
            //given
            Order completionOrder = registerOrder();
            completionOrder.changeStatus(OrderStatus.COMPLETION);
            orderService.create(completionOrder);

            String changedOrderStatus = OrderStatus.MEAL.name();

            //when //then
            assertThatThrownBy(() -> orderService.changeOrderStatus(completionOrder.getId(), changedOrderStatus))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 등록되어있지 않은 주문인 경우")
        @Test
        void changeOrderStatusWhenNotRegisteredOrderStatusIsCOMPLETION() {
            //given
            Order notRegisteredOrder = new Order(100L, OrderTable.of(), OrderStatus.COMPLETION,
                    Collections.singletonList(new OrderLineItem(menu, 1L)));

            String changedOrderStatus = OrderStatus.MEAL.name();

            //when //then
            assertThatThrownBy(() -> orderService.changeOrderStatus(notRegisteredOrder.getId(), changedOrderStatus))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("[주문 조회] 성공")
    @Test
    void list() {
        //given
        Order order = registerOrder();

        //when
        List<Order> actual = orderService.list();

        //then
        assertThat(actual).hasSize(1);
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(order);
    }

    private Menu testMenu(Long id, String name, BigDecimal price) {
        return new Menu(id, name, price, new MenuGroup(2L, "한마리메뉴"));
    }

    private Order registerOrder(OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order order = Order.of(orderTable, orderLineItems);

        return orderService.create(order);
    }

    private Order registerOrder(OrderTable orderTable) {
        return registerOrder(orderTable, Collections.singletonList(validOrderLineItem));
    }

    private Order registerOrder() {
        return registerOrder(orderTable);
    }

    private OrderTable registerOrderTable(boolean empty) {
        SaveOrderTableRequest request = new SaveOrderTableRequest(0, empty);
        return tableService.create(request);
    }
}
