package com.entry_task.entry_task.cart.entity;

import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items = new ArrayList<>();

  @Column(name = "c_time")
  private Long cTime;

  @Column(name = "m_time")
  private Long mTime;

  @Version private Long version;

  protected Cart() {
    // for JPA only
  }

  public Cart(User user, Long cTime, Long mTime) {
    this.user = user;
    this.cTime = cTime;
    this.mTime = mTime;
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

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(List<CartItem> items) {
    this.items = items;
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
