package kitchenpos.dao;

import kitchenpos.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    long countByIdIn(List<Long> ids); // todo: 이 쿼리메서드 기능은 동작하는지 확실치 않음! 🤔
}
