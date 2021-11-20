package kitchenpos.dao;

import kitchenpos.domain.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {
//    List<OrderLineItem> findAllByOrderId(Long orderId); todo: 일단 안쓰니까 주석!!
}
