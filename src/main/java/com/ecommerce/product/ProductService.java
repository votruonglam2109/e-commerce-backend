package com.ecommerce.product;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.product.model.FilterDTO;
import com.ecommerce.product.model.PaginationDTO;
import com.ecommerce.product.model.Product;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product){
        String slug = Slugify.builder().transliterator(true).lowerCase(true).build()
                .slugify(product.getTitle());
        product.setSlug(slug);
        return productRepository.save(product);
    }
    private Product findProductById(Long id){
        Product p = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The product with id [%s] not exists"
                        .formatted(id)));
        return p;
    }
    public Product getProduct(Long id){
        Product p = findProductById(id);
        if(!p.isDeleted())
            return p;
        throw new ResourceNotFoundException("The product with id [%s] not exists"
                .formatted(id));
    }
    public String deleteProductById(Long id){
        Product p = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The product with id [%s] not exists"
                        .formatted(id)));
        p.setDeleted(true);
        productRepository.save(p);
        return "Product have been deleted";
    }

    public List<Product> findAllProducts(){
        return productRepository.findAll();
    }

    public Product updateProductById(Long id, Product p){
        Product oldProduct = findProductById(id);
        String slug = Slugify.builder().transliterator(true).lowerCase(true).build()
                .slugify(p.getTitle());
        p.setSlug(slug);
        p.setId(id);
        p.setCreatedBy(oldProduct.getCreatedBy());
        p.setCreatedAt(oldProduct.getCreatedAt());
        return productRepository.save(p);
    }

    public List<Product> filterProducts(FilterDTO filter, PaginationDTO pagination){
        Pageable pageable = getPageable(pagination);
        return productRepository.findWithFilter(filter, pageable);
    }

    private Pageable getPageable(PaginationDTO paginationDTO){
        Sort sort = Sort.by(
                Sort.Direction.fromString(paginationDTO.sortDirection()),
                paginationDTO.sortBy()
        );
        return PageRequest.of(paginationDTO.page(), paginationDTO.limit(), sort);
    }
}
