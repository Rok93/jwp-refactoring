package kitchenpos.order.application;

import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.dto.OrderTableResponse;
import kitchenpos.order.dto.SaveOrderTableRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 테이블 서비스 테스트")
@IntegrationTest
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @DisplayName("[주문 테이블 추가] 성공")
    @Test
    void testCreate() {
        //given
        SaveOrderTableRequest request = new SaveOrderTableRequest(0, true);

        //when
        OrderTable actual = tableService.create(request);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(0);
        assertThat(actual.getTableGroup()).isNull();
    }

    @DisplayName("[주문 테이블 조회] 성공")
    @Test
    void testList() {
        //when
        List<OrderTableResponse> actual = tableService.list();

        //then
        assertThat(actual).hasSize(8);
    }
}
