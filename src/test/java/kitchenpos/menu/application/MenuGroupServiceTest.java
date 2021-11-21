package kitchenpos.menu.application;

import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.ui.dto.MenuGroupResponse;
import kitchenpos.menu.ui.dto.SaveMenuGroupRequest;
import kitchenpos.menu.ui.dto.SaveMenuGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 그룹 서비스 테스트")
@IntegrationTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("[메뉴 그룹 추가] - 성공")
    @Test
    void create() {
        //given //when
        String menuName = "menu1";
        SaveMenuGroupResponse actual = registerMenu(menuName);

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(menuName);
    }

    @DisplayName("[메뉴 그룹 조회] - 성공")
    @Test
    void list() {
        //when
        List<MenuGroupResponse> actual = menuGroupService.list();

        //then
        assertThat(actual).hasSize(4);
    }

    private SaveMenuGroupResponse registerMenu(String menuName) {
        SaveMenuGroupRequest menuGroup = new SaveMenuGroupRequest(menuName);
        return menuGroupService.create(menuGroup);
    }
}
