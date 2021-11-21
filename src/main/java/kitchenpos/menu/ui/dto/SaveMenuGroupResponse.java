package kitchenpos.menu.ui.dto;

import kitchenpos.menu.domain.MenuGroup;

public class SaveMenuGroupResponse {

    private Long id;
    private String name;

    public SaveMenuGroupResponse(MenuGroup menuGroup) {
        id = menuGroup.getId();
        name = menuGroup.getName();
    }

    public SaveMenuGroupResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
