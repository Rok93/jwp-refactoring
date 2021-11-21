package kitchenpos.menu.application;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.menu.dto.SaveMenuProductRequest;
import kitchenpos.menu.dto.SaveMenuRequest;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴 서비스 테스트")
@IntegrationTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    private Product validProduct;
    private MenuGroup validMenuGroup;
    private MenuGroup invalidMenuGroup;
    private Product invalidProduct;

    @BeforeEach
    void setUp() {
        validMenuGroup = new MenuGroup(1L, "두마리메뉴");

        invalidMenuGroup = new MenuGroup(100L, "지옥행 열차 셋트");

        validProduct = testProduct(1L, "후라이드치킨", BigDecimal.valueOf(16_000));
        invalidProduct = testProduct(100L, "황천의 뒤틀린 치킨", BigDecimal.valueOf(100_000));
    }

    @Nested
    @IntegrationTest
    @DisplayName("[메뉴 추가]")
    class AddingMenu {

        @DisplayName("성공")
        @Test
        void create() {
            //given
            MenuProduct menuProduct = testMenuProduct(validProduct, 1);

            //when
            Menu actual = registerMenu(menuProduct);

            //then
            assertThat(actual.getMenuGroupId()).isNotNull();
            assertThat(actual.getPrice().longValue()).isEqualTo(validProduct.getPrice().longValue());
            assertThat(actual.getName()).isEqualTo("menu");
            assertThat(actual.getMenuProducts()).hasSize(1);
            assertThat(actual.getMenuProducts().get(0).getMenu().getId()).isEqualTo(actual.getId());
            assertThat(actual.getMenuProducts().get(0).getProduct().getId()).isEqualTo(menuProduct.getProduct().getId());
            assertThat(actual.getMenuProducts().get(0).getQuantity()).isEqualTo(menuProduct.getQuantity());
        }

        @DisplayName("실패 - 가격이 0원 이하인 경우")
        @Test
        void createWhenPriceIsNegative() {
            //given
            MenuProduct menuProduct = testMenuProduct(validProduct, 1);
            BigDecimal invalidPrice = BigDecimal.valueOf(-1);

            SaveMenuRequest saveMenuRequest = testMenuRequest(menuProduct, invalidPrice);

            //when //then
            assertThatThrownBy(() -> menuService.create(saveMenuRequest))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 메뉴 그룹이 존재하지 않은 경우")
        @Test
        void createWhenMenuGroupIsNotExist() {
            //given
            MenuProduct menuProduct = testMenuProduct(validProduct, 1);
            SaveMenuRequest saveMenuRequest = testMenuRequest(menuProduct, BigDecimal.valueOf(10_000), invalidMenuGroup.getId());

            //when //then
            assertThatThrownBy(() -> menuService.create(saveMenuRequest))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 메뉴 상품들 중에 존재하지 않는 상품이 있는 경우")
        @Test
        void createWhenHavingNotExistMenuProduct() {
            //given
            MenuProduct menuProduct = testMenuProduct(invalidProduct, 1);
            SaveMenuRequest saveMenuRequest = testMenuRequest(menuProduct, invalidProduct.getPrice());

            //when //then
            assertThatThrownBy(() -> menuService.create(saveMenuRequest))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("실패 - 메뉴의 가격이 개별로 주문한 금액을 더한 값보다 큰 경우")
        @Test
        void createWhenMenuPriceIsBiggerThanSumOfEachOrderProducts() {
            //given
            MenuProduct menuProduct = testMenuProduct(validProduct, 1);
            SaveMenuRequest saveMenuRequest = testMenuRequest(menuProduct, validProduct.getPrice().add(BigDecimal.ONE));

            //when //then
            assertThatThrownBy(() -> menuService.create(saveMenuRequest))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("[매뉴 조회] 성공")
    @Test
    void list() {
        //when
        List<MenuResponse> actual = menuService.list();

        //then
        assertThat(actual).hasSize(6);
    }

    private SaveMenuRequest testMenuRequest(MenuProduct menuProduct, BigDecimal price, Long menuGroupId) {
        return new SaveMenuRequest("menu", price, menuGroupId,
                Arrays.asList(new SaveMenuProductRequest(menuProduct.getProduct().getId(), menuProduct.getQuantity())));
    }

    private SaveMenuRequest testMenuRequest(MenuProduct menuProduct, BigDecimal price) {
        return testMenuRequest(menuProduct, price, validMenuGroup.getId());
    }

    private Menu registerMenu(MenuProduct menuProduct) {
        SaveMenuRequest saveMenuRequest = testMenuRequest(menuProduct, validProduct.getPrice());
        return menuService.create(saveMenuRequest);
    }

    private Product testProduct(Long id, String name, BigDecimal price) {
        Product product = new Product(id, name, price);
        return product;
    }

    private MenuProduct testMenuProduct(Product product, int quantity) {
        return new MenuProduct(product, quantity);
    }

    private Menu testMenu(MenuGroup menuGroup, MenuProduct menuProduct, BigDecimal price) {
        Menu menu = new Menu("menu", price, menuGroup);
        menu.addMenuProduct(menuProduct);
        return menu;
    }
}
