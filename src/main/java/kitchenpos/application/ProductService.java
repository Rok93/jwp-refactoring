package kitchenpos.application;

import kitchenpos.dao.ProductRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.product.dto.SaveProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product create(final SaveProductRequest request) {
        return productRepository.save(request.toEntity());
    }

    public List<Product> list() {
        return productRepository.findAll();
    }
}
