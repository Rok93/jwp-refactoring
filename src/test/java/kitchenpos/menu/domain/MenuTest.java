package kitchenpos.menu.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuTest {

    @DisplayName("실패 - 가격이 null인 경우")
    @ParameterizedTest
    @MethodSource
    @NullSource
    void createWhenPriceIsNull(BigDecimal price) {
        //when //then
        assertThatThrownBy(() -> new Menu("menuName", price, new MenuGroup("menuGroup")))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> createWhenPriceIsNull() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1)),
                Arguments.of(BigDecimal.valueOf(-1_000)),
                Arguments.of(BigDecimal.valueOf(-10_000))
        );
    }
}
