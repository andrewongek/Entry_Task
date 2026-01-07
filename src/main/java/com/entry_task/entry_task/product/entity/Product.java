package com.entry_task.entry_task.product.entity;

import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "seller_id")
  private User seller;

  private int stock;

  private int price;

  @ManyToMany
  @JoinTable(
      name = "product_categories",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  @Column(columnDefinition = "TEXT") // Use columnDefinition for large text in some databases
  private String description;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status", nullable = false)
  private ProductStatus productStatus;

  @Column(name = "c_time")
  private Long cTime;

  @Column(name = "m_time")
  private Long mTime;

  @Version private Long version;

  protected Product() {
    // for JPA only
  }

  public Product(
      String name,
      User seller,
      int stock,
      int price,
      Set<Category> categories,
      String description,
      ProductStatus productStatus,
      Long cTime,
      Long mTime) {
    this.name = name;
    this.seller = seller;
    this.stock = stock;
    this.price = price;
    this.categories = categories;
    this.description = description;
    this.productStatus = productStatus;
    this.cTime = cTime;
    this.mTime = mTime;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getSeller() {
    return seller;
  }

  public void setSeller(User seller) {
    this.seller = seller;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public void setCategories(Set<Category> categories) {
    this.categories = categories;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductStatus getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

  public Long getcTime() {
    return cTime;
  }

  public void setcTime(Long cTime) {
    this.cTime = cTime;
  }

  public Long getmTime() {
    return mTime;
  }

  public void setmTime(Long mTime) {
    this.mTime = mTime;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
