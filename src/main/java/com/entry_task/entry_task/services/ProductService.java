package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.*;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.model.Product;
import com.entry_task.entry_task.repository.ProductRepository;
import com.entry_task.entry_task.sql.ProductSpecifications;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.AuthenticationException;
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

    public ProductInfoDto getProductInfo(long productId) {
        return toProductInfoDto(getProductById(productId));
    }

    public ProductDetailDto getSellerProductDetail(long productId) {
        validateAccessToProductId(productId);
        return getProductDetail(productId);
    }

    public ProductListResponse<ProductListingDto> getUserProductListingList(ProductsListRequest request, Long sellerId) {
        if (sellerId != null && sellerId > 0) {
            userService.validateSellerId(sellerId);
        }
        Page<ProductListingDto> page = getProductListingList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new MetadataDto(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    public ProductListResponse<ProductInfoDto> getSellerProductInfoList(ProductsListRequest request) {
        User currentUser = authService.getCurrentUser();
        Long sellerId = currentUser.getId();
        Page<ProductInfoDto> page = getProductInfoList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new MetadataDto(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    public ProductListResponse<ProductInfoDto> getAdminProductInfoList(ProductsListRequest request, Long sellerId) {
        validateAdmin();
        if (sellerId != null && sellerId > 0) {
            userService.validateSellerId(sellerId);
        }
        Page<ProductInfoDto> page = getProductInfoList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new MetadataDto(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    @Transactional
    public Long createProduct(ProductRequest request) {
        validateAccessToCreate();
        return createProduct(request, authService.getCurrentUser());
    }

    @Transactional
    public Long createProductAdmin(ProductRequest request, Long sellerId) {
        validateAdmin();
        User user = userService.findUserBySellerId(sellerId);
        return createProduct(request, user);
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.INACTIVE);
    }

    @Transactional
    public void activateProduct(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.ACTIVE);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        validateAccessToProductId(productId);
        setProductStatus(productId, ProductStatus.DELETED);
    }

    @Transactional
    public void updateProductById(Long productId, ProductRequest request) {
        validateAccessToUpdateProduct(productId);
        updateProduct(productId, request);
    }

    // Private Functions
    private Product getProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private ProductDetailDto getProductDetail(long productId) {
        return toProductDetailDto(getProductById(productId));
    }

    private Page<ProductInfoDto> getProductInfoList(ProductsListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = Specification.unrestricted();
        if (sellerId != null && sellerId > 0) {
            specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().statuses() != null &&
                !request.filter().statuses().isEmpty()) {
            specification = specification.and(ProductSpecifications.statusIn(request.filter().statuses()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification = specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductInfoDto);
    }

    private Page<ProductListingDto> getProductListingList(ProductsListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = ProductSpecifications.statusIsActive();
        if (sellerId != null && sellerId > 0) {
            specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification = specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductListingDto);
    }

    private Long createProduct(ProductRequest request, User seller) {
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
        return productRepository.save(newProduct).getId();
    }

    private void updateProduct(Long productId, ProductRequest request) {
        long now = Instant.now().getEpochSecond();
        Product existingProduct = getProductById(productId);
        existingProduct.setName(request.name());
        existingProduct.setPrice(request.price());
        existingProduct.setStock(request.stock());
        existingProduct.setDescription(request.description());
        existingProduct.setmTime(now);
        existingProduct.setCategories(categoryService.loadCategories(request.categoryIds()));
        productRepository.save(existingProduct);
    }

    private void setProductStatus(Long productId, ProductStatus status) {
        Product product = getProductById(productId);
        validateStatusUpdateOperation(product.getProductStatus(), status);
        product.setProductStatus(status);
        productRepository.save(product);
    }

    // Helper Functions
    private void validateAccessToCreate() {
        User currentUser = authService.getCurrentUser();
        if (!currentUser.getRole().equals(Role.SELLER) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("You are not allowed to access this product");
        }
    }

    private void validateAdmin() {
        if (!authService.getCurrentUser().getRole().equals(Role.ADMIN)) {
            throw new AuthenticationException("Access denied: insufficient permissions");
        }
    }

    private void validateAccessToUpdateProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        User currentUser = authService.getCurrentUser();

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not allowed to access this product");
        }
    }

    private void validateAccessToProductId(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        User currentUser = authService.getCurrentUser();

        if (!product.getSeller().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException("You are not allowed to access this product");
        }
    }

    private void validateStatusUpdateOperation(ProductStatus before, ProductStatus after) {
        switch (before) {
            case ACTIVE, INACTIVE -> {
                if (before.equals(after)) {
                    throw new IllegalStateException("Product Status is already " + before.name());
                }
            }
            default -> throw new IllegalStateException("Invalid Product Status Change");
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
