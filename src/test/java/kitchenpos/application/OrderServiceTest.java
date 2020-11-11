package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.MenuGroupRequest;
import kitchenpos.dto.MenuGroupResponse;
import kitchenpos.dto.MenuProductRequest;
import kitchenpos.dto.MenuRequest;
import kitchenpos.dto.MenuResponse;
import kitchenpos.dto.OrderCreateRequest;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderResponse;
import kitchenpos.dto.OrderStatusChangeRequest;
import kitchenpos.dto.ProductRequest;
import kitchenpos.dto.ProductResponse;
import kitchenpos.dto.TableChangeRequest;
import kitchenpos.dto.TableCreateRequest;
import kitchenpos.dto.TableGroupCreateRequest;
import kitchenpos.dto.TableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql("/truncate.sql")
class OrderServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private TableService tableService;

    @Autowired
    private TableGroupService tableGroupService;

    @Autowired
    private OrderService orderService;

    private MenuResponse menu;
    private TableResponse table;

    @BeforeEach
    void setUp() {
        menu = createMenu_후라이드세트();
        table = createTable();
    }

    @Test
    void create() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // when
        OrderLineItemRequest request = new OrderLineItemRequest(menu.getId(), 2);

        OrderCreateRequest order = new OrderCreateRequest(table.getId(), Collections.singletonList(request));

        OrderResponse result = orderService.create(order);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
        assertThat(result.getOrderTableId()).isEqualTo(table.getId());
    }

    @Test
    @DisplayName("create - 그룹에 속한 테이블에서 주문")
    void createWithGroupedTable() {
        // given
        TableResponse anotherTable = createTable();

        TableGroupCreateRequest request = new TableGroupCreateRequest(
            Arrays.asList(anotherTable.getId(), table.getId()));
        tableGroupService.create(request);

        // when
        OrderResponse resultOfTable = orderWithEqualAmountOfAllMenus(table,
            Collections.singletonList(menu), 2);
        OrderResponse resultOfAnotherTable = orderWithEqualAmountOfAllMenus(anotherTable,
            Collections.singletonList(menu), 2);

        // then
        assertThat(resultOfTable.getId()).isNotNull();
        assertThat(resultOfTable.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
        assertThat(resultOfTable.getOrderTableId()).isEqualTo(table.getId());

        assertThat(resultOfAnotherTable.getId()).isNotNull();
        assertThat(resultOfAnotherTable.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
        assertThat(resultOfAnotherTable.getOrderTableId()).isEqualTo(anotherTable.getId());
    }

    @Test
    @DisplayName("create - 아무 메뉴도 포함하지 않는 주문시 예외처리")
    void create_IfOrderContainsNoMenu_ThrowException() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // when
        OrderCreateRequest order = new OrderCreateRequest(table.getId(), new ArrayList<>());

        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("create - 존재하지 않는 메뉴 id로 주문시 예외처리")
    void create_IfOrderContainsWrongMenu_ThrowException() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // when
        OrderLineItemRequest orderLineItem = new OrderLineItemRequest(1_000L, 2);

        OrderCreateRequest order = new OrderCreateRequest(table.getId(), Collections.singletonList(orderLineItem));

        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("create - 빈 테이블에서 주문 시도시 예외처리")
    void create_IfTableIsEmpty_ThrowException() {
        OrderLineItemRequest request = new OrderLineItemRequest(menu.getId(), 2);

        OrderCreateRequest order = new OrderCreateRequest(table.getId(), Collections.singletonList(request));

        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void list() {
        assertThat(orderService.list()).hasSize(0);

        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // given (create order)
        orderWithEqualAmountOfAllMenus(table, Collections.singletonList(menu), 2);

        // when & then
        assertThat(orderService.list()).hasSize(1);
    }

    @Test
    @DisplayName("changeOrderStatus")
    void changeOrderStatus() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // given (create order)
        OrderResponse order = orderWithEqualAmountOfAllMenus(table, Collections.singletonList(menu), 2);

        // when & then
        OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest(OrderStatus.MEAL.name());
        assertThat(orderService.changeOrderStatus(order.getId(), orderStatusChangeRequest).getOrderStatus())
            .isEqualTo(OrderStatus.MEAL.toString());

        orderStatusChangeRequest = new OrderStatusChangeRequest(OrderStatus.COMPLETION.name());
        assertThat(orderService.changeOrderStatus(order.getId(), orderStatusChangeRequest).getOrderStatus())
            .isEqualTo(OrderStatus.COMPLETION.toString());
    }

    @Test
    @DisplayName("changeOrderStatus - 요리중인 상태에서 식사를 거치지 않고 식사완료 상태로 바꾸려는 경우")
    void changeOrderStatus_IfAfterCookingIsCompletion() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // given (create order)
        OrderResponse order = orderWithEqualAmountOfAllMenus(table, Collections.singletonList(menu), 2);

        // when & then
        OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest(OrderStatus.COMPLETION.name());
        order = orderService.changeOrderStatus(order.getId(), orderStatusChangeRequest);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETION.toString());
    }

    @Test
    @DisplayName("changeOrderStatus - 존재하지 않는 상태 문자열 사용시 예외처리")
    void changeOrderStatus_IfTryWithUndefinedStatus_ThrowException() {
        // given (change table to not empty)
        TableChangeRequest changeRequest = new TableChangeRequest(false);
        table = tableService.changeEmpty(table.getId(), changeRequest);

        changeRequest = new TableChangeRequest(4);
        table = tableService.changeNumberOfGuests(table.getId(), changeRequest);

        // given (create order)
        OrderResponse order = orderWithEqualAmountOfAllMenus(table, Collections.singletonList(menu), 2);

        // when & then
        OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest("식사중");
        assertThatThrownBy(() -> orderService.changeOrderStatus(order.getId(), orderStatusChangeRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private TableResponse createTable() {
        // create table
        TableCreateRequest createRequest = new TableCreateRequest(true, 0);

        return tableService.create(createRequest);
    }

    private MenuResponse createMenu_후라이드세트() {
        // create products
        ProductRequest 후라이드치킨_request = new ProductRequest("후라이드치킨", BigDecimal.valueOf(10_000));
        ProductResponse 후라이드치킨 = productService.create(후라이드치킨_request);

        ProductRequest 프랜치프라이_request = new ProductRequest("프랜치프라이", BigDecimal.valueOf(5_000));
        ProductResponse 프랜치프라이 = productService.create(프랜치프라이_request);

        // create a menu group
        MenuGroupRequest 세트메뉴_request = new MenuGroupRequest("세트메뉴");
        MenuGroupResponse 세트메뉴 = menuGroupService.create(세트메뉴_request);

        // create menu
        List<MenuProductRequest> menuProducts = createMenuProductsWithAllQuantityAsOne(
            Arrays.asList(후라이드치킨, 프랜치프라이));

        MenuRequest menuRequest = new MenuRequest(
            "후라이드 세트", BigDecimal.valueOf(13_000), 세트메뉴.getId(), menuProducts);

        return menuService.create(menuRequest);
    }

    private List<MenuProductRequest> createMenuProductsWithAllQuantityAsOne(List<ProductResponse> products) {
        List<MenuProductRequest> menuProducts = products.stream()
            .map(product -> new MenuProductRequest(product.getId(), 1))
            .collect(Collectors.toList());

        return Collections.unmodifiableList(menuProducts);
    }

    private OrderResponse orderWithEqualAmountOfAllMenus(TableResponse table, List<MenuResponse> menus, int quantity) {
        List<OrderLineItemRequest> orderLineItemRequests = new ArrayList<>();

        for (MenuResponse menu : menus) {
            OrderLineItemRequest request = new OrderLineItemRequest(menu.getId(), quantity);
            orderLineItemRequests.add(request);
        }
        OrderCreateRequest order = new OrderCreateRequest(table.getId(), orderLineItemRequests);

        return orderService.create(order);
    }
}
