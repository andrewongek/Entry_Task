package com.entry_task.entry_task.favourite.entity;

import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(
    name = "user_favourites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class UserFavourite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  protected UserFavourite() {}
  ;

  public UserFavourite(User user, Product product) {
    this.user = user;
    this.product = product;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
