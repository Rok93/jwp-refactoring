package kitchenpos.menu.application;

import kitchenpos.menu.domain.*;
import kitchenpos.menu.ui.dto.MenuResponse;
import kitchenpos.menu.ui.dto.SaveMenuProductRequest;
import kitchenpos.menu.ui.dto.SaveMenuRequest;
import kitchenpos.menu.ui.dto.SavedMenuResponse;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public SavedMenuResponse create(final SaveMenuRequest request) { //todo: 리팩토링 대상!
        if (!menuGroupRepository.existsById(request.getMenuGroupId())) {
            throw new IllegalArgumentException();
        }

        final List<SaveMenuProductRequest> menuProducts = request.getMenuProducts(); // 새로운거 Menu, 기존에 있던거 Product, 이번에 받아온 값 quantity

        Map<Long, Long> menuProductIdAndQuantities = menuProducts.stream()
                .collect(Collectors.toMap(SaveMenuProductRequest::getProductId, SaveMenuProductRequest::getQuantity));

        List<Product> products = productRepository.findByIdIn(new ArrayList<>(menuProductIdAndQuantities.keySet()));
        BigDecimal sum = products.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(menuProductIdAndQuantities.get(product.getId()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal price = request.getPrice();
        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException("메뉴의 가격이 메뉴에 포함된 개별 메뉴 상품의 기격 합보다 비쌀 수 없습니다.");
        }

        MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 그룹입니다."));

        final Menu savedMenu = menuRepository.save(new Menu(request.getName(), price, menuGroup)); // menu 생성
        products.stream()
                .collect(Collectors.toMap(Function.identity(), product -> menuProductIdAndQuantities.get(product.getId())))
                .entrySet().stream()
                .map(entry -> new MenuProduct(entry.getKey(), entry.getValue()))
                .forEach(menuProduct -> savedMenu.addMenuProduct(menuProduct));

        return new SavedMenuResponse(savedMenu);
    }

    public List<MenuResponse> list() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::new)
                .collect(Collectors.toList());
    }
}
