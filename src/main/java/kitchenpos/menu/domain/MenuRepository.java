package kitchenpos.menu.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @EntityGraph(attributePaths = "menuProducts")
    List<Menu> findAll();

    long countByIdIn(List<Long> ids); // todo: 이 쿼리메서드 기능은 동작하는지 확실치 않음! 🤔
}
