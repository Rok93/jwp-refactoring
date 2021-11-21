package kitchenpos.menu.ui.dto;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.product.domain.Product;

public class MenuProductResponse {

    private Long seq;
    private Long menuId;
    private long quantity;
    private Product product;

    public MenuProductResponse(Long seq, Menu menu, long quantity, Product product) {
        this.seq = seq;
        this.menuId = menu.getId();
        this.quantity = quantity;
        this.product = product;
    }

    public MenuProductResponse(MenuProduct menuProduct) {
        this(menuProduct.getSeq(), menuProduct.getMenu(), menuProduct.getQuantity(), menuProduct.getProduct());
    }

    public Long getSeq() {
        return seq;
    }

    public Long getMenuId() {
        return menuId;
    }

    public long getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
