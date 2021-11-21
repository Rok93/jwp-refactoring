package kitchenpos.application;

import kitchenpos.dao.OrderRepository;
import kitchenpos.dao.TableGroupRepository;
import kitchenpos.domain.*;
import kitchenpos.domain.order_table.dto.SaveOrderTableRequest;
import kitchenpos.domain.table_group.dto.SaveTableGroupRequest;
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

@DisplayName("단체 지정 서비스 테스트")
@IntegrationTest
class TableGroupServiceTest {

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @Autowired
    private TableGroupService tableGroupService;

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderRepository orderRepository;

    private MenuGroup menuGroup;
    private Menu menu;
    private OrderTable firstOrderTable;
    private OrderTable secondOrderTable;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup(2L, "한마리메뉴");
        menu = new Menu(1L, "후라이드치킨", BigDecimal.valueOf(16_000), menuGroup);
        firstOrderTable = registerOrderTable();
        secondOrderTable = registerOrderTable();
    }

    @Nested
    @IntegrationTest
    @DisplayName("[단체 지정 - 등록]")
    class CreateTableGroup {

        @DisplayName("성공")
        @Test
        void create() {
            //given //when
            TableGroup actual = registerTableGroup(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId()));

            //then
            assertThat(actual).isNotNull();
            assertThat(actual.getOrderTables()).hasSize(2);
            assertThat(actual.getOrderTables().get(0).getTableGroup()).isNotNull();
            assertThat(actual.getOrderTables().get(0).getNumberOfGuests()).isEqualTo(0);
            assertThat(actual.getOrderTables().get(1).getTableGroup()).isNotNull();
            assertThat(actual.getOrderTables().get(1).getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("실패 - 주문 테이블의 갯수가 2개 미만인 경우")
        @Test
        void createWhenOrderTablesSizeSmallerThanTwo() {
            //when //then
            assertThatThrownBy(() -> registerTableGroup(Collections.singletonList(firstOrderTable.getId())))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 존재하지 않는 주문 테이블이 있는 경우")
        @Test
        void createWhenOrderTablesContainNotExistOrderTable() {
            //given
            OrderTable notExistOrderTable = new OrderTable(0, true);

            //when //then
            assertThatThrownBy(() -> registerTableGroup(Arrays.asList(firstOrderTable.getId(), notExistOrderTable.getId())))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 주문 테이블이 비어있지 않은 경우")
        @Test
        void createWhenOrderTablesContainNotEmptyOrderTable() {
            //given
            OrderTable notEmptyOrderTable = registerOrderTable(false);

            //when //then
            assertThatThrownBy(() -> registerTableGroup(Arrays.asList(firstOrderTable.getId(), notEmptyOrderTable.getId())))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 이미 단체 지정이 되어있으면 예외가 발생한다")
        @Test
        void createWhenAlreadyRegistered() {
            //given
            OrderTable registeredOrderTable = registerOrderTable(true);
            registerTableGroup(Arrays.asList(registerOrderTable().getId(), registeredOrderTable.getId()));

            //when //then
            assertThatThrownBy(() -> registerTableGroup(Arrays.asList(firstOrderTable.getId(), registeredOrderTable.getId())))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @IntegrationTest
    @DisplayName("[단체 지정 취소]")
    class UngroupTableGroup {

        @DisplayName("성공")
        @Test
        void ungroup() {
            //given
            List<Long> orderTableIds = Arrays.asList(registerOrderTable().getId(), registerOrderTable().getId());
            TableGroup tableGroup = registerTableGroup(orderTableIds);

            //when
            tableGroupService.ungroup(tableGroup.getId());

            //then
            TableGroup actual = tableGroupRepository.findById(tableGroup.getId()).get();
            assertThat(actual.getOrderTables()).isEmpty();
        }

        @DisplayName("실패 - '계산 완료' 상태가 아닌 주문 테이블이 있는 경우")
        @Test
        void ungroupWhenOrderStatusNotCOMPLETION() {
            //given
            OrderTable cookingOrderTable = registerOrderTable();

            List<Long> orderTableIds = Arrays.asList(cookingOrderTable.getId(), registerOrderTable().getId());
            TableGroup tableGroup = registerTableGroup(orderTableIds);

            registerOrder(cookingOrderTable); // todo: Order_id 가 널이면 안된다.

            //when //then
            assertThatThrownBy(() -> tableGroupService.ungroup(tableGroup.getId()))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    private TableGroup registerTableGroup(List<Long> orderTableIds) {
        SaveTableGroupRequest request = new SaveTableGroupRequest(orderTableIds);
        return tableGroupService.create(request);
    }

    private OrderTable registerOrderTable(boolean empty) {
        SaveOrderTableRequest request = new SaveOrderTableRequest(0, empty);
        return tableService.create(request);
    }

    private OrderTable registerOrderTable() {
        return registerOrderTable(true);
    }

    private void registerOrder(OrderTable cookingOrderTable) { // todo: OrderLineItem 쪽에 문제...  (오늘은 여기까지!)
        Order order = Order.of(cookingOrderTable);
        OrderLineItem orderLineItem = new OrderLineItem(menu, 1L);
        order.addOrderLineItem(orderLineItem);
        orderRepository.save(order);
    }
}
