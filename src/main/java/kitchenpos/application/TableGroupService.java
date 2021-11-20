package kitchenpos.application;

import kitchenpos.dao.OrderRepository;
import kitchenpos.dao.OrderTableRepository;
import kitchenpos.dao.TableGroupRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.table_group.dto.SaveTableGroupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
public class TableGroupService {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository,
                             final TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    @Transactional
    public TableGroup create(final SaveTableGroupRequest request) {
        final List<Long> orderTableIds = request.getOrderTables();

        if (CollectionUtils.isEmpty(orderTableIds) || orderTableIds.size() < 2) { //todo: 일급컬렉션으로 변경이 가능하지 않을까...?
            throw new IllegalArgumentException();
        }

        final List<OrderTable> savedOrderTables = orderTableRepository.findAllByIdIn(orderTableIds);
        if (orderTableIds.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException(); //todo: 등록되지 않은 OrderTable이 존재
        }

        for (final OrderTable savedOrderTable : savedOrderTables) { // todo: 이것도 일급 컬렉션이 가능할지도...
            if (!savedOrderTable.isEmpty() || Objects.nonNull(savedOrderTable.getTableGroup())) {
                throw new IllegalArgumentException();
            }
        }

        final TableGroup savedTableGroup = tableGroupRepository.save(new TableGroup(savedOrderTables));
        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.assignTableGroup(savedTableGroup);
        }

        return savedTableGroup;
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        TableGroup tableGroup = tableGroupRepository.findById(tableGroupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단체 지정 입니다."));
        final List<OrderTable> orderTables = orderTableRepository.findAllByTableGroup(tableGroup);

        if (orderRepository.existsByOrderTableInAndOrderStatusIn(orderTables, Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL))) {
            throw new IllegalArgumentException();
        }

        orderTables.stream()
                .forEach(tableGroup::ungroupOrderTable);
    }
}
