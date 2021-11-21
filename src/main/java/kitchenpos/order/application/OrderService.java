package kitchenpos.order.application;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.order.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderTableRepository orderTableRepository;

    public OrderService(
            final MenuRepository menuRepository,
            final OrderRepository orderRepository,
            final OrderLineItemRepository orderLineItemRepository,
            final OrderTableRepository orderTableRepository
    ) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public Order create(final Order order) { //todo: 리팩토링 대상!
        final List<OrderLineItem> orderLineItems = order.getOrderLineItems();

        final List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        if (orderLineItems.size() != menuRepository.countByIdIn(menuIds)) {
            throw new IllegalArgumentException();
        }

        final OrderTable orderTable = orderTableRepository.findById(order.getOrderTableId())
                .orElseThrow(IllegalArgumentException::new);

        Order savedOrder = new Order(orderTable, OrderStatus.COOKING);
        for (final OrderLineItem orderLineItem : orderLineItems) { //todo: Order에 casecade 속성을 잘 걸면!!! 따로 저장할 필요 없이 잘 들어가지 않을까...?
            Menu menu = menuRepository.findById(orderLineItem.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));
            savedOrder.addOrderLineItem(new OrderLineItem(menu, orderLineItem.getQuantity()));
        }
        return orderRepository.save(savedOrder); //todo: DTO로 변경
    }

    public List<Order> list() {
        final List<Order> orders = orderRepository.findAll();
        for (final Order order : orders) {
            order.getOrderLineItems(); //todo: N+1 문제 발생
        }

        return orders;
    }

    @Transactional
    public Order changeOrderStatus(final Long orderId, final OrderStatus orderStatus) {
        final Order savedOrder = orderRepository.findById(orderId)
                .orElseThrow(IllegalArgumentException::new);

        savedOrder.changeStatus(orderStatus);
        savedOrder.getOrderLineItems(); // todo: fetch Join 시키기!

        return savedOrder;
    }
}
