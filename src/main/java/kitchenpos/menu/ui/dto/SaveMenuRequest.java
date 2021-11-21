package kitchenpos.menu.ui.dto;

import java.math.BigDecimal;
import java.util.List;

public class SaveMenuRequest {

    private String name;
    private BigDecimal price;
    private Long menuGroupId;
    private List<SaveMenuProductRequest> menuProducts;

    public SaveMenuRequest(String name, BigDecimal price, Long menuGroupId, List<SaveMenuProductRequest> menuProducts) {
        this.name = name;
        this.price = price;
        this.menuGroupId = menuGroupId;
        this.menuProducts = menuProducts;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public List<SaveMenuProductRequest> getMenuProducts() {
        return menuProducts;
    }
}
