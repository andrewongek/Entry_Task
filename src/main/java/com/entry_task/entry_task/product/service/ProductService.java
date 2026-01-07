package com.entry_task.entry_task.product.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.service.CategoryService;
import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.exceptions.InsufficientStockException;
import com.entry_task.entry_task.exceptions.ProductNotActiveException;
import com.entry_task.entry_task.exceptions.ProductNotFoundException;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.dto.cache.ProductDynamic;
import com.entry_task.entry_task.product.dto.cache.ProductStatic;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.product.repository.projections.ProductDetailProjection;
import com.entry_task.entry_task.product.specifications.ProductSpecifications;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.service.UserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private static final Logger log = LoggerFactory.getLogger(ProductService.class);

  private final AuthService authService;
  private final UserService userService;
  private final CategoryService categoryService;
  private final ProductCacheService productCacheService;
  private final ProductRepository productRepository;

  public ProductService(
      AuthService authService,
      UserService userService,
      CategoryService categoryService,
      ProductCacheService productCacheService,
      ProductRepository productRepository) {
    this.authService = authService;
    this.userService = userService;
    this.categoryService = categoryService;
    this.productCacheService = productCacheService;
    this.productRepository = productRepository;
  }

  public ProductInfo getProductInfo(long productId) {
    ProductStatic productStatic = productCacheService.getProductStatic(productId);

    if (productStatic.status() == ProductStatus.INACTIVE) throw new ProductNotActiveException();
    if (productStatic.status() == ProductStatus.DELETED) throw new ProductNotFoundException();

    ProductDynamic productDynamic = productCacheService.getProductDynamic(productId);
    return new ProductInfo(
        productStatic.id(),
        productStatic.name(),
        productStatic.sellerId(),
        productDynamic.stock(),
        productDynamic.price(),
        productStatic.description(),
        productStatic.status());
  }

  public Product getActiveProductById(long productId) {
    Product product = getProductById(productId);
    if (product.getProductStatus() != ProductStatus.ACTIVE) {
      throw new ProductNotActiveException();
    }
    return product;
  }

  @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
  public ProductDetailResponse getSellerProductDetail(long productId) {
    validateProductOwnership(productId);
    ProductDetailProjection p =
        productRepository.findProductDetail(productId).orElseThrow(ProductNotFoundException::new);
    return new ProductDetailResponse(
        p.getId(),
        p.getName(),
        p.getSellerId(),
        p.getStock(),
        p.getPrice(),
        p.getCategoryIds(),
        p.getDescription(),
        p.getProductStatus(),
        p.getCTime(),
        p.getMTime());
  }

  public ProductListResponse<ProductListing> getUserProductListingList(
      ProductListRequest request, Long sellerId) {
    if (sellerId != null && sellerId > 0) {
      userService.validateSellerId(sellerId);
    }
    Page<ProductListing> page = getProductListingList(request, sellerId);
    return new ProductListResponse<>(
        page.toList(),
        new Metadata(page.getTotalElements(), page.getNumber(), page.getSize(), page.hasNext()));
  }

  public ProductListResponse<ProductInfo> getSellerProductInfoList(ProductListRequest request) {
    User currentUser = authService.getCurrentUser();
    Long sellerId = currentUser.getId();
    Page<ProductInfo> page = getProductInfoList(request, sellerId);
    return new ProductListResponse<>(
        page.toList(),
        new Metadata(page.getTotalElements(), page.getNumber(), page.getSize(), page.hasNext()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  public ProductListResponse<ProductInfo> getAdminProductInfoList(
      ProductListRequest request, Long sellerId) {
    if (sellerId != null && sellerId > 0) {
      userService.validateSellerId(sellerId);
    }
    Page<ProductInfo> page = getProductInfoList(request, sellerId);
    return new ProductListResponse<>(
        page.toList(),
        new Metadata(page.getTotalElements(), page.getNumber(), page.getSize(), page.hasNext()));
  }

  public ProductListResponse<ProductListing> getUserFavouriteProductListingList(
      ProductListRequest request) {
    Page<ProductListing> page =
        getUserFavouriteProductListingList(request, authService.getCurrentUser().getId());
    return new ProductListResponse<>(
        page.toList(),
        new Metadata(page.getTotalElements(), page.getNumber(), page.getSize(), page.hasNext()));
  }

  @Transactional
  @PreAuthorize("hasRole('SELLER')")
  public Long createProduct(CreateProductRequest request) {
    User user = authService.getCurrentUser();
    Long productId = createProduct(request, user);
    log.info("Seller: {} successfully created product: {}", user.getId(), productId);
    return productId;
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public Long createProductAdmin(CreateProductRequest request, Long sellerId) {
    User user = userService.findUserBySellerId(sellerId);
    Long productId = createProduct(request, user);
    log.info("Admin successfully created product: {}", productId);
    return productId;
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "product:static", key = "#productId"),
        @CacheEvict(value = "product:dynamic", key = "#productId")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @Retryable(retryFor = {OptimisticLockException.class})
  public void deactivateProduct(Long productId) {
    validateProductOwnership(productId);
    setProductStatus(productId, ProductStatus.INACTIVE);
    log.info("Admin deactivated product: {}", productId);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "product:static", key = "#productId"),
        @CacheEvict(value = "product:dynamic", key = "#productId")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @Retryable(retryFor = {OptimisticLockException.class})
  public void activateProduct(Long productId) {
    validateProductOwnership(productId);
    setProductStatus(productId, ProductStatus.ACTIVE);
    log.info("Admin activated product: {}", productId);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "product:static", key = "#productId"),
        @CacheEvict(value = "product:dynamic", key = "#productId")
      })
  @PreAuthorize("hasRole('SELLER')")
  @Retryable(retryFor = {OptimisticLockException.class})
  public void deleteProductById(Long productId) {
    validateProductOwnership(productId);
    setProductStatus(productId, ProductStatus.DELETED);
    log.info("Seller deleted product: {}", productId);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "product:static", key = "#productId"),
        @CacheEvict(value = "product:dynamic", key = "#productId")
      })
  @PreAuthorize("hasRole('SELLER')")
  @Retryable(retryFor = {OptimisticLockException.class})
  public void updateProductById(Long productId, UpdateProductRequest request) {
    validateProductOwnership(productId);
    updateProduct(productId, request);
    log.info("Seller updated product: {}", productId);
  }

  @Transactional
  @Caching(evict = {@CacheEvict(value = "product:dynamic", key = "#productId")})
  public void reserveStock(long productId, int qty) {
    int updated = productRepository.reserveStock(productId, qty);
    if (updated == 0) {
      throw new InsufficientStockException("Insufficient stock or product inactive: " + productId);
    }
  }

  // Private Functions
  private Product getProductById(long productId) {
    return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
  }

  private Page<ProductInfo> getProductInfoList(ProductListRequest request, Long sellerId) {
    Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
    Pageable pageable =
        PageRequest.of(
            request.pagination().page(),
            request.pagination().size(),
            Sort.by(sortDirection, request.sort().field()));
    Specification<Product> specification = Specification.unrestricted();
    if (sellerId != null && sellerId > 0) {
      specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
    }
    if (request.keyword() != null && !request.keyword().isBlank()) {
      specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
    }
    if (request.filter() != null
        && request.filter().statuses() != null
        && !request.filter().statuses().isEmpty()) {
      specification =
          specification.and(ProductSpecifications.statusIn(request.filter().statuses()));
    }
    if (request.filter() != null
        && request.filter().categoryIds() != null
        && !request.filter().categoryIds().isEmpty()) {
      specification =
          specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
    }
    return productRepository.findAll(specification, pageable).map(this::toProductInfo);
  }

  private Page<ProductListing> getProductListingList(ProductListRequest request, Long sellerId) {
    Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
    Pageable pageable =
        PageRequest.of(
            request.pagination().page(),
            request.pagination().size(),
            Sort.by(sortDirection, request.sort().field()));
    Specification<Product> specification = ProductSpecifications.statusIsActive();
    if (sellerId != null && sellerId > 0) {
      specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
    }
    if (request.keyword() != null && !request.keyword().isBlank()) {
      specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
    }
    if (request.filter() != null
        && request.filter().categoryIds() != null
        && !request.filter().categoryIds().isEmpty()) {
      specification =
          specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
    }
    return productRepository.findAll(specification, pageable).map(this::toProductListing);
  }

  private Page<ProductListing> getUserFavouriteProductListingList(
      ProductListRequest request, Long userId) {
    Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
    Pageable pageable =
        PageRequest.of(
            request.pagination().page(),
            request.pagination().size(),
            Sort.by(sortDirection, request.sort().field()));
    Specification<Product> specification =
        ProductSpecifications.statusIsActive().and(ProductSpecifications.isFavouritedBy(userId));
    if (request.keyword() != null && !request.keyword().isBlank()) {
      specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
    }
    if (request.filter() != null
        && request.filter().categoryIds() != null
        && !request.filter().categoryIds().isEmpty()) {
      specification =
          specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
    }
    return productRepository.findAll(specification, pageable).map(this::toProductListing);
  }

  private Long createProduct(CreateProductRequest request, User seller) {
    long now = Instant.now().getEpochSecond();
    Product newProduct =
        new Product(
            request.name(),
            seller,
            request.stock(),
            request.price(),
            categoryService.loadCategories(request.categoryIds()),
            request.description(),
            ProductStatus.ACTIVE,
            now,
            now);
    return productRepository.save(newProduct).getId();
  }

  @Transactional
  private void updateProduct(long productId, UpdateProductRequest request) {
    long now = Instant.now().getEpochSecond();
    int updated =
        productRepository.updateProduct(
            productId,
            request.name(),
            request.price(),
            request.stock(),
            request.description(),
            now);
    if (updated == 0) {
      throw new ProductNotFoundException();
    }

    updateProductCategories(productId, categoryService.loadCategories(request.categoryIds()));
  }

  private void updateProductCategories(long productId, Set<Category> categories) {
    Product product =
        productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

    product.getCategories().clear();
    product.getCategories().addAll(categories);
  }

  private void setProductStatus(Long productId, ProductStatus newStatus) {
    ProductStatus expectedCurrent =
        switch (newStatus) {
          case ACTIVE -> ProductStatus.INACTIVE;
          case INACTIVE -> ProductStatus.ACTIVE;
          default -> throw new IllegalStateException("Invalid target status");
        };

    int updated = productRepository.updateStatusIfCurrent(productId, expectedCurrent, newStatus);

    if (updated == 0) {
      throw new IllegalStateException("Invalid status transition or product not found");
    }
  }

  private void validateProductOwnership(long productId) {
    User currentUser = authService.getCurrentUser();
    if (currentUser.getRole() == Role.ADMIN) {
      return;
    }
    boolean owned = productRepository.isOwnedBySeller(productId, currentUser.getId());
    if (!owned) {
      throw new AccessDeniedException("You do not own this product");
    }
  }

  private ProductListing toProductListing(Product product) {
    return new ProductListing(
        product.getId(),
        product.getName(),
        product.getSeller().getId(),
        product.getStock(),
        product.getPrice());
  }

  private ProductInfo toProductInfo(Product product) {
    return new ProductInfo(
        product.getId(),
        product.getName(),
        product.getSeller().getId(),
        product.getStock(),
        product.getPrice(),
        product.getDescription(),
        product.getProductStatus());
  }
}
