package kitchenpos.menu.ui.dto;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class SavedMenuResponse {

    private Long id;
    private MenuGroup menuGroup;
    private List<MenuProductResponse> menuProducts;
    private String name;
    private BigDecimal price;

    public SavedMenuResponse(Long id, MenuGroup menuGroup, List<MenuProductResponse> menuProducts, String name, BigDecimal price) {
        this.id = id;
        this.menuGroup = menuGroup;
        this.menuProducts = menuProducts;
        this.name = name;
        this.price = price;
    }

    public SavedMenuResponse(Menu menu) {
        id = menu.getId();
        menuGroup = menu.getMenuGroup();
        menuProducts = menu.getMenuProducts().stream()
                .map(MenuProductResponse::new)
                .collect(Collectors.toList());
        price = menu.getPrice();
        name = menu.getName();
    }

    public Long getId() {
        return id;
    }

    public MenuGroup getMenuGroup() {
        return menuGroup;
    }

    public List<MenuProductResponse> getMenuProducts() {
        return menuProducts;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
