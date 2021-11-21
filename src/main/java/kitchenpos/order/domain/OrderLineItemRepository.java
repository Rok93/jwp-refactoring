package kitchenpos.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {
//    List<OrderLineItem> findAllByOrderId(Long orderId); todo: 일단 안쓰니까 주석!!
}
