package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.model.Product;
import com.entry_task.entry_task.repository.ProductRepository;
import com.entry_task.entry_task.sql.ProductSpecifications;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.entry_task.entry_task.model.User;
import com.entry_task.entry_task.model.Category;

import java.time.Instant;

@Service
public class ProductService {

    private final AuthService authService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public ProductService(AuthService authService, UserService userService, CategoryService categoryService, ProductRepository productRepository) {
        this.authService = authService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    private Product getProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public ProductInfoDto getProductInfo(long productId) {
        return toProductInfoDto(getProductById(productId));
    }

    private ProductDetailDto getProductDetail(long productId) {
        return toProductDetailDto(getProductById(productId));
    }

    public ProductDetailDto getSellerProductDetail(long productId) {
        validateAccessToProductId(productId);
        return getProductDetail(productId);

    }

    private void createProduct(CreateProductRequest request, User seller) {
        long now = Instant.now().getEpochSecond();
        Product newProduct = new Product();
        newProduct.setName(request.name());
        newProduct.setSeller(seller);
        newProduct.setPrice(request.price());
        newProduct.setStock(request.stock());
        newProduct.setDescription(request.description());
        newProduct.setProductStatus(ProductStatus.ACTIVE);
        newProduct.setcTime(now);
        newProduct.setmTime(now);
        newProduct.setCategories(categoryService.loadCategories(request.categoryIds()));
        productRepository.save(newProduct);
    }

    public void createProduct(CreateProductRequest request) {
        validateAccessToCreate(request);
        createProduct(request, authService.getCurrentUser());
    }

    public void createProductAdmin(CreateProductRequest request) {
        User seller = validateAccessToCreate(request);
        createProduct(request, seller);
    }


    private void setProductStatus(Long productId, ProductStatus status) {
        Product product = getProductById(productId);
        product.setProductStatus(status);
        productRepository.save(product);
    }

    public void deactivateProduct(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.INACTIVE);
    }

    public void activateProduct(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.ACTIVE);
    }

    public void deleteProduct(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.DELETED);
    }

    public Page<ProductListingDto> getProductListing(ProductsListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = Specification.unrestricted();
        if (sellerId != null && sellerId > 0) {
            specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().statuses() != null &&
                !request.filter().statuses().isEmpty()) {
            specification.and(ProductSpecifications.statusIn(request.filter().statuses()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductListingDto);
    }

    public ProductListResponse getSellerProductInfoList(ProductsListRequest request) {
        User currentUser = authService.getCurrentUser();
        Long sellerId = currentUser.getId();
        Page<ProductInfoDto> page = getProductList(request, sellerId);
        return new ProductListResponse(
                page.toList(),
                new MetadataDto(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }


    // Can be called by the seller and admin. Seller will have the sellerId auto passed to this while admin will pass from request
    public Page<ProductInfoDto> getProductList(ProductsListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = Specification.unrestricted();
        if (sellerId != null && sellerId > 0) {
            specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().statuses() != null &&
                !request.filter().statuses().isEmpty()) {
            specification.and(ProductSpecifications.statusIn(request.filter().statuses()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductInfoDto);
    }

    // Helper Functions
    private User validateAccessToCreate(CreateProductRequest request) {
        User seller = userService.getUserExistsBy(request.sellerId()).orElseThrow(() -> new IllegalArgumentException("SellerId does not exist"));
        User currentUser = authService.getCurrentUser();
        if (!currentUser.getRole().equals(Role.SELLER) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("You are not allowed to access this product");
        }
        return seller;
    }

    private void validateAccessToProductId(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        User currentUser = authService.getCurrentUser();

        if (!product.getSeller().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("You are not allowed to access this product");
        }
    }

    private ProductListingDto toProductListingDto(Product product) {
        return new ProductListingDto(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice()
        );
    }

    private ProductInfoDto toProductInfoDto(Product product) {
        return new ProductInfoDto(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice(),
                product.getDescription(),
                product.getProductStatus()
        );
    }

    private ProductDetailDto toProductDetailDto(Product product) {
        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice(),
                product.getCategories()
                        .stream()
                        .map(Category::getId)
                        .toList(),
                product.getDescription(),
                product.getProductStatus(),
                product.getcTime(),
                product.getmTime()
        );
    }
}
