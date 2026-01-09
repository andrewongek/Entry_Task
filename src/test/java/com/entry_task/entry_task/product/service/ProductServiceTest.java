package com.entry_task.entry_task.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.service.CategoryService;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.common.dto.Pagination;
import com.entry_task.entry_task.common.dto.Sort;
import com.entry_task.entry_task.common.mapper.PageMapper;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.exceptions.ProductNotActiveException;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.mapper.ProductMapper;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ProductServiceTest {

  @InjectMocks private ProductService productService;

  @Mock private ProductRepository productRepository;

  @Mock private AuthService authService;

  @Mock private UserService userService;

  @Mock private CategoryService categoryService;

  @Mock private ProductMapper productMapper;

  @Mock private PageMapper pageMapper;

  @Test
  void getActiveProductById_productIsActive_shouldReturnProduct() {
    // Given
    User seller = TestEntityFactory.createSellerWithId("seller");

    Product product = TestEntityFactory.createProductWithId("Product", seller);
    product.setProductStatus(ProductStatus.ACTIVE);

    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    // When
    Product result = productService.getActiveProductById(product.getId());

    // Then
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(ProductStatus.ACTIVE, result.getProductStatus());
  }

  @Test
  void getActiveProductById_productNotActive_shouldThrowException() {
    // Given
    User seller = TestEntityFactory.createSellerWithId("seller");

    Product product = TestEntityFactory.createProductWithId("Product", seller);
    product.setProductStatus(ProductStatus.INACTIVE);

    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    // When & Then
    assertThrows(
        ProductNotActiveException.class,
        () -> productService.getActiveProductById(product.getId()));
  }

  //    @Test
  //    void getSellerProductDetail_ownerShouldReturnDetail() {
  //        // Given
  //        User seller = TestEntityFactory.createSellerWithId("seller");
  //
  //        Product product = TestEntityFactory.createProductWithId("Product", seller);
  //        product.setCategories(Set.of(new Category("category")));
  //
  //        when(authService.getCurrentUser()).thenReturn(seller);
  //        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
  //
  //        // When
  //        ProductDetailResponse response = productService.getSellerProductDetail(product.getId());
  //
  //        // Then
  //        assertNotNull(response);
  //        assertEquals(product.getId(), response.id());
  //        assertEquals(seller.getId(), response.sellerId());
  //    }

  @Test
  void getUserProductListingList_validSellerId_shouldReturnProductList() {
    // Constants
    final long SELLER_ID = 5L;
    final String PRODUCT_NAME_1 = "Product A";
    final String PRODUCT_NAME_2 = "Product B";
    final int PAGE = 0;
    final int SIZE = 10;
    final long TOTAL_ELEMENTS = 2L;

    // Given
    ProductListRequest request =
        new ProductListRequest(null, new Pagination(PAGE, SIZE), null, null);

    // Mock seller validation
    doNothing().when(userService).validateSellerId(SELLER_ID);

    // Mock repository page
    User seller = TestEntityFactory.createSellerWithId("seller");
    Product product1 = TestEntityFactory.createProductWithId(PRODUCT_NAME_1, seller);
    Product product2 = TestEntityFactory.createProductWithId(PRODUCT_NAME_2, seller);

    Page<Product> productPage =
        new PageImpl<>(List.of(product1, product2), PageRequest.of(PAGE, SIZE), TOTAL_ELEMENTS);

    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.toProductListing(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product p = invocation.getArgument(0);
              return new ProductListing(
                  p.getId(), p.getName(), p.getSeller().getId(), p.getStock(), p.getPrice());
            });
    when(pageMapper.toProductListResponse(any(Page.class)))
        .thenAnswer(
            invocation -> {
              Page<?> p = invocation.getArgument(0);
              return new ProductListResponse<>(
                  p.getContent(),
                  new Metadata(p.getTotalElements(), p.getNumber(), p.getSize(), p.hasNext()));
            });
    // When
    ProductListResponse<ProductListing> response =
        productService.getUserProductListingList(request, SELLER_ID);

    // Then
    assertNotNull(response);
    assertEquals(2, response.products().size());
    assertEquals(PRODUCT_NAME_1, response.products().get(0).name());
    assertEquals(PRODUCT_NAME_2, response.products().get(1).name());

    Metadata metadata = response.metadata();
    assertEquals(TOTAL_ELEMENTS, metadata.totalItems());
    assertEquals(PAGE, metadata.currentPage());
    assertEquals(SIZE, metadata.pageSize());
    assertFalse(metadata.hasNext()); // adjust if simulating multiple pages

    // Verify interactions
    verify(userService).validateSellerId(SELLER_ID);
    verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getSellerProductInfoList_validRequest_shouldReturnProductList() {
    // Constants
    final long SELLER_ID = 1L;
    final String PRODUCT_NAME_1 = "Product A";
    final String PRODUCT_NAME_2 = "Product B";
    final int PAGE = 0;
    final int SIZE = 10;
    final long TOTAL_ELEMENTS = 2L;

    // Given
    User seller = TestEntityFactory.createSellerWithId("seller");
    seller.setId(SELLER_ID);

    when(authService.getCurrentUser()).thenReturn(seller);

    ProductListRequest request =
        new ProductListRequest(null, new Pagination(PAGE, SIZE), null, null);

    // Mock product repository to return a page of products
    Product product1 = TestEntityFactory.createProductWithId(PRODUCT_NAME_1, seller);
    Product product2 = TestEntityFactory.createProductWithId(PRODUCT_NAME_2, seller);

    Page<Product> productPage =
        new PageImpl<>(List.of(product1, product2), PageRequest.of(PAGE, SIZE), TOTAL_ELEMENTS);

    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.toProductInfo(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product p = invocation.getArgument(0);
              return new ProductInfo(
                  p.getId(),
                  p.getName(),
                  p.getSeller().getId(),
                  p.getStock(),
                  p.getPrice(),
                  p.getDescription(),
                  p.getProductStatus());
            });
    when(pageMapper.toProductListResponse(any(Page.class)))
        .thenAnswer(
            invocation -> {
              Page<?> p = invocation.getArgument(0);
              return new ProductListResponse<>(
                  p.getContent(),
                  new Metadata(p.getTotalElements(), p.getNumber(), p.getSize(), p.hasNext()));
            });
    // When
    ProductListResponse<ProductInfo> response = productService.getSellerProductInfoList(request);

    // Then
    assertNotNull(response);
    assertEquals(2, response.products().size());
    assertEquals(PRODUCT_NAME_1, response.products().get(0).name());
    assertEquals(PRODUCT_NAME_2, response.products().get(1).name());

    Metadata metadata = response.metadata();
    assertEquals(TOTAL_ELEMENTS, metadata.totalItems());
    assertEquals(PAGE, metadata.currentPage());
    assertEquals(SIZE, metadata.pageSize());
    assertFalse(metadata.hasNext()); // depends on page content; adjust if necessary

    // Verify repository queried
    verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    // Verify current user retrieved
    verify(authService).getCurrentUser();
  }

  @Test
  void getAdminProductInfoList_validSellerId_shouldReturnProductList() {
    // Constants
    final long SELLER_ID = 5L;
    final String PRODUCT_NAME_1 = "Product A";
    final String PRODUCT_NAME_2 = "Product B";
    final int PAGE = 0;
    final int SIZE = 10;
    final long TOTAL_ELEMENTS = 2L;

    // Given
    Pagination pagination = new Pagination(PAGE, SIZE);
    ProductFilter filter = new ProductFilter(null, null);
    ProductListRequest request = new ProductListRequest(null, pagination, filter, null);

    // Mock validateSellerId() to do nothing (happy path)
    doNothing().when(userService).validateSellerId(SELLER_ID);

    // Mock product repository to return two products
    User seller = TestEntityFactory.createSellerWithId("seller");
    Product product1 = TestEntityFactory.createProductWithId(PRODUCT_NAME_1, seller);
    Product product2 = TestEntityFactory.createProductWithId(PRODUCT_NAME_2, seller);

    Page<Product> productPage =
        new PageImpl<>(
            List.of(product1, product2), // products on this page
            PageRequest.of(PAGE, SIZE),
            TOTAL_ELEMENTS // total elements across pages
            );

    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.toProductInfo(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product p = invocation.getArgument(0);
              return new ProductInfo(
                  p.getId(),
                  p.getName(),
                  p.getSeller().getId(),
                  p.getStock(),
                  p.getPrice(),
                  p.getDescription(),
                  p.getProductStatus());
            });
    when(pageMapper.toProductListResponse(any(Page.class)))
        .thenAnswer(
            invocation -> {
              Page<?> p = invocation.getArgument(0);
              return new ProductListResponse<>(
                  p.getContent(),
                  new Metadata(p.getTotalElements(), p.getNumber(), p.getSize(), p.hasNext()));
            });
    // When
    ProductListResponse<ProductInfo> response =
        productService.getAdminProductInfoList(request, SELLER_ID);

    // Then
    assertEquals(TOTAL_ELEMENTS, response.products().size());
    assertEquals(PRODUCT_NAME_1, response.products().get(0).name());
    assertEquals(PRODUCT_NAME_2, response.products().get(1).name());

    Metadata metadata = response.metadata();
    assertEquals(TOTAL_ELEMENTS, metadata.totalItems()); // total across all pages
    assertEquals(PAGE, metadata.currentPage());
    assertEquals(SIZE, metadata.pageSize());
    assertFalse(metadata.hasNext());

    // Verify validateSellerId() called
    verify(userService).validateSellerId(SELLER_ID);
    // Verify repository was queried
    verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void getUserFavouriteProductListingList_validRequest_shouldReturnPage() {
    // Constants
    final long USER_ID = 1L;
    final String KEYWORD = "phone";
    final int PAGE = 0;
    final int SIZE = 10;
    final String SORT_FIELD = "name";
    final String SORT_ORDER = "ASC";

    // Given
    User user = TestEntityFactory.createCustomer("user");
    user.setId(USER_ID);

    when(authService.getCurrentUser()).thenReturn(user);

    Pagination pagination = new Pagination(PAGE, SIZE);
    Sort sort = new Sort(SORT_FIELD, SORT_ORDER);
    ProductFilter filter = new ProductFilter(null, null);
    ProductListRequest request = new ProductListRequest(KEYWORD, pagination, filter, sort);

    Product product1 = TestEntityFactory.createProductWithId("Phone A", user);
    Product product2 = TestEntityFactory.createProductWithId("Phone B", user);

    List<Product> products = List.of(product1, product2);
    Page<Product> productPage = new PageImpl<>(products);

    // Mock repository to return the page
    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.toProductListing(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product p = invocation.getArgument(0);
              return new ProductListing(
                  p.getId(), p.getName(), p.getSeller().getId(), p.getStock(), p.getPrice());
            });
    when(pageMapper.toProductListResponse(any(Page.class)))
        .thenAnswer(
            invocation -> {
              Page<?> p = invocation.getArgument(0);
              return new ProductListResponse<>(
                  p.getContent(),
                  new Metadata(p.getTotalElements(), p.getNumber(), p.getSize(), p.hasNext()));
            });
    // When
    var response = productService.getUserFavouriteProductListingList(request);

    // Then
    assertNotNull(response);
    assertEquals(2, response.products().size());
    assertEquals("Phone A", response.products().get(0).name());
    assertEquals("Phone B", response.products().get(1).name());

    verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void createProduct_validRequest_shouldReturnProductId() {
    // Constants
    final long SAVED_PRODUCT_ID = 10L;
    final String PRODUCT_NAME = "Test Product";
    final int PRODUCT_PRICE = 100;
    final int PRODUCT_STOCK = 50;
    final String PRODUCT_DESCRIPTION = "Test description";
    final Set<Long> CATEGORY_IDS = Set.of(1L, 2L);

    // Given
    User seller = TestEntityFactory.createSellerWithId("seller");

    CreateProductRequest request =
        new CreateProductRequest(
            PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK, CATEGORY_IDS, PRODUCT_DESCRIPTION);

    when(authService.getCurrentUser()).thenReturn(seller);

    // Mock categories returned by CategoryService
    Set<Category> categories =
        Set.of(
            TestEntityFactory.createCategory("Category1"),
            TestEntityFactory.createCategory("Category2"));
    when(categoryService.loadCategories(CATEGORY_IDS)).thenReturn(categories);

    // Mock repository save
    Product savedProduct =
        TestEntityFactory.createProduct(
            PRODUCT_NAME,
            seller,
            PRODUCT_STOCK,
            PRODUCT_PRICE,
            categories,
            PRODUCT_DESCRIPTION,
            ProductStatus.ACTIVE,
            Instant.now().getEpochSecond(),
            Instant.now().getEpochSecond());
    savedProduct.setId(SAVED_PRODUCT_ID);

    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // When
    Long productId = productService.createProduct(request);

    // Then
    assertNotNull(productId, "Returned product ID should not be null");
    assertEquals(SAVED_PRODUCT_ID, productId, "Returned product ID should match saved product");

    // Verify repository save called with correct data
    verify(productRepository)
        .save(
            argThat(
                product ->
                    product.getName().equals(PRODUCT_NAME)
                        && product.getPrice() == PRODUCT_PRICE
                        && product.getStock() == PRODUCT_STOCK
                        && product.getDescription().equals(PRODUCT_DESCRIPTION)
                        && product.getSeller().equals(seller)
                        && product.getCategories().equals(categories)));
  }

  @Test
  void createProductAdmin_validRequest_shouldReturnProductId() {
    // Constants
    final long SAVED_PRODUCT_ID = 10L;
    final String PRODUCT_NAME = "Admin Product";
    final int PRODUCT_PRICE = 200;
    final int PRODUCT_STOCK = 30;
    final String PRODUCT_DESCRIPTION = "Admin created product";
    final Set<Long> CATEGORY_IDS = Set.of(1L, 2L);
    final long SELLER_ID = 5L;

    // Given
    User seller = TestEntityFactory.createSellerWithId("seller");

    CreateProductRequest request =
        new CreateProductRequest(
            PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK, CATEGORY_IDS, PRODUCT_DESCRIPTION);

    // Mock userService to return the seller for given sellerId
    when(userService.findUserBySellerId(SELLER_ID)).thenReturn(seller);

    // Mock categoryService to return categories
    Set<Category> categories =
        Set.of(
            TestEntityFactory.createCategory("Category1"),
            TestEntityFactory.createCategory("Category2"));
    when(categoryService.loadCategories(CATEGORY_IDS)).thenReturn(categories);

    // Mock repository save
    Product savedProduct =
        TestEntityFactory.createProduct(
            PRODUCT_NAME,
            seller,
            PRODUCT_STOCK,
            PRODUCT_PRICE,
            categories,
            PRODUCT_DESCRIPTION,
            ProductStatus.ACTIVE,
            Instant.now().getEpochSecond(),
            Instant.now().getEpochSecond());
    savedProduct.setId(SAVED_PRODUCT_ID);

    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // When
    Long productId = productService.createProductAdmin(request, SELLER_ID);

    // Then
    assertNotNull(productId, "Returned product ID should not be null");
    assertEquals(SAVED_PRODUCT_ID, productId, "Returned product ID should match saved product");

    // Verify repository save called with correct data
    verify(productRepository)
        .save(
            argThat(
                product ->
                    product.getName().equals(PRODUCT_NAME)
                        && product.getPrice() == PRODUCT_PRICE
                        && product.getStock() == PRODUCT_STOCK
                        && product.getDescription().equals(PRODUCT_DESCRIPTION)
                        && product.getSeller().equals(seller)
                        && product.getCategories().equals(categories)));

    // Verify userService was called
    verify(userService).findUserBySellerId(SELLER_ID);
  }

  //    @Test
  //    void activateProduct_adminWithInactiveProduct_shouldActivate() {
  //        // Given
  //        User admin = TestEntityFactory.createAdminWithId("admin");
  //        User seller = TestEntityFactory.createSellerWithId("seller");
  //        Product product = TestEntityFactory.createProductWithId("Product", seller);
  //        product.setProductStatus(ProductStatus.INACTIVE);
  //
  //        // When
  //        when(authService.getCurrentUser()).thenReturn(admin);
  //        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
  //        when(productRepository.updateStatusIfCurrent(eq(product.getId()),
  //                eq(ProductStatus.INACTIVE),
  //                eq(ProductStatus.ACTIVE))).thenReturn(1);
  //        productService.activateProduct(product.getId());
  //
  //        // Then
  //        verify(productRepository).updateStatusIfCurrent(anyLong(), any(), any());
  //    }

  //    @Test
  //    void deactivateProduct_adminWithActiveProduct_shouldDeactivate() {
  //        // Given
  //        User admin = TestEntityFactory.createAdminWithId("admin");
  //        User seller = TestEntityFactory.createSellerWithId("seller");
  //        Product product = TestEntityFactory.createProductWithId("Product", seller);
  //        product.setProductStatus(ProductStatus.ACTIVE);
  //
  //        // When
  //        when(authService.getCurrentUser()).thenReturn(admin);
  //        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
  //        when(productRepository.updateStatusIfCurrent(eq(product.getId()),
  //                eq(ProductStatus.ACTIVE),
  //                eq(ProductStatus.INACTIVE))).thenReturn(1);
  //        productService.deactivateProduct(product.getId());
  //
  //        // Then
  //        verify(productRepository).updateStatusIfCurrent(anyLong(), any(), any());
  //    }

  //    @Test
  //    void updateProductById_ownerShouldUpdateSuccessfully() {
  //        // Given
  //        final long PRODUCT_ID = 1L;
  //        final int OLD_STOCK = 10;
  //        final int OLD_PRICE = 100;
  //        final String OLD_NAME = "Old Product";
  //
  //        final String NEW_NAME = "New Product";
  //        final int NEW_PRICE = 150;
  //        final int NEW_STOCK = 20;
  //        final String NEW_DESCRIPTION = "Updated description";
  //        final Set<Long> NEW_CATEGORY_IDS = Set.of(1L, 2L);
  //
  //        User seller = TestEntityFactory.createSellerWithId("seller");
  //
  //        Product product = TestEntityFactory.createProductWithId(OLD_NAME, seller);
  //        product.setId(PRODUCT_ID);
  //        product.setStock(OLD_STOCK);
  //        product.setPrice(OLD_PRICE);
  //
  //        UpdateProductRequest request = new UpdateProductRequest(
  //                NEW_NAME,
  //                NEW_PRICE,
  //                NEW_STOCK,
  //                NEW_CATEGORY_IDS,
  //                NEW_DESCRIPTION
  //        );
  //
  //        // Mock current user and repository
  //        when(authService.getCurrentUser()).thenReturn(seller);
  //        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
  //
  //        // Mock categories returned by CategoryService
  //        Set<Category> categories = Set.of(
  //                new Category("Category1"),
  //                new Category("Category2")
  //        );
  //        when(categoryService.loadCategories(NEW_CATEGORY_IDS)).thenReturn(categories);
  //
  //        // When
  //        productService.updateProductById(PRODUCT_ID, request);
  //
  //        // Then
  //        assertEquals(NEW_NAME, product.getName());
  //        assertEquals(NEW_PRICE, product.getPrice());
  //        assertEquals(NEW_STOCK, product.getStock());
  //        assertEquals(NEW_DESCRIPTION, product.getDescription());
  //        assertEquals(categories, product.getCategories());
  //
  //        verify(productRepository).save(product);
  //    }

}
