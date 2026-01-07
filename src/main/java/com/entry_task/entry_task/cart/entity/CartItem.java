package com.entry_task.entry_task.cart.entity;

import com.entry_task.entry_task.product.entity.Product;
import jakarta.persistence.*;

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "c_time", nullable = false, updatable = false)
  private Long cTime;

  @Column(name = "m_time", nullable = false)
  private Long mTime;

  @Version private Long version;

  protected CartItem() {
    // for JPA only
  }

  public CartItem(Cart cart, Product product, int quantity, Long cTime, Long mTime) {
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
    this.cTime = cTime;
    this.mTime = mTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Long getCTime() {
    return cTime;
  }

  public void setCTime(Long cTime) {
    this.cTime = cTime;
  }

  public Long getMTime() {
    return mTime;
  }

  public void setMTime(Long mTime) {
    this.mTime = mTime;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
