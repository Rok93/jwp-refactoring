package kitchenpos.application;

import kitchenpos.domain.OrderTable;
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
        //when
        OrderTable actual = tableService.create(new OrderTable());

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getNumberOfGuests()).isEqualTo(0);
        assertThat(actual.getTableGroupId()).isNull();
    }

    @DisplayName("[주문 테이블 조회] 성공")
    @Test
    void testList() {
        //when
        List<OrderTable> actual = tableService.list();

        //then
        assertThat(actual).hasSize(8);
    }
}
