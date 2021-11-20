package kitchenpos.application;

import kitchenpos.dao.MenuGroupRepository;
import kitchenpos.dao.MenuProductRepository;
import kitchenpos.dao.MenuRepository;
import kitchenpos.dao.ProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.domain.menu.dto.SaveMenuProductRequest;
import kitchenpos.domain.menu.dto.SaveMenuRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuProductRepository menuProductRepository;
    private final ProductRepository productRepository;

    public MenuService(
            final MenuRepository menuRepository,
            final MenuGroupRepository menuGroupRepository,
            final MenuProductRepository menuProductRepository,
            final ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuProductRepository = menuProductRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Menu create(final SaveMenuRequest request) {
        final BigDecimal price = request.getPrice();
        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

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

        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException("메뉴의 가격이 메뉴에 포함된 개별 메뉴 상품의 기격 합보다 비쌀 수 없습니다.");
        }

        MenuGroup menuGroup = menuGroupRepository.findById(request.getMenuGroupId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴 그룹입니다."));

        final Menu savedMenu = menuRepository.save(new Menu(request.getName(), price, menuGroup));
        products.stream()
                .collect(Collectors.toMap(Function.identity(), product -> menuProductIdAndQuantities.get(product.getId())))
                .entrySet().stream()
//                .map(entry -> new MenuProduct(savedMenu, entry.getKey(), entry.getValue())) // 안되면 이걸로 돌려놓기....
                .map(entry -> new MenuProduct(entry.getKey(), entry.getValue())) //todo: casecade 옵션 설정 중!
                .forEach(menuProduct -> savedMenu.addMenuProduct(menuProduct));

        return savedMenu;
    }

    public List<Menu> list() {
        final List<Menu> menus = menuRepository.findAll();
        for (final Menu menu : menus) {
            menu.getMenuProducts(); //todo: fetch Join으로 쿼리 최적화하기!
        }

        return menus;
    }
}
