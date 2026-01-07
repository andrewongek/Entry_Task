package com.entry_task.entry_task.order.entity;

import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "orders",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_orders_user_idem",
            columnNames = {"user_id", "idempotency_key"}))
public class Order {
  protected Order() {
    // required by JPA
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "idempotency_key", nullable = false, length = 64)
  private String idempotencyKey;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  @Column(name = "total_amount", nullable = false)
  private int totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @Column(name = "c_time", nullable = false, updatable = false)
  private Long cTime;

  @Column(name = "m_time")
  private Long mTime;

  public Order(User user) {
    long now = Instant.now().getEpochSecond();
    this.user = user;
    this.status = OrderStatus.CREATED;
    this.cTime = now;
    this.mTime = now;
  }

  public Order(User user, String idempotencyKey) {
    long now = Instant.now().getEpochSecond();
    this.user = user;
    this.idempotencyKey = idempotencyKey;
    this.status = OrderStatus.CREATED;
    this.cTime = now;
    this.mTime = now;
  }

  /* ===== Domain helpers ===== */
  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void recalculateTotal() {
    this.totalAmount = items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
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

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }

  public int getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(int totalAmount) {
    this.totalAmount = totalAmount;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
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

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public void setIdempotencyKey(String idempotencyKey) {
    this.idempotencyKey = idempotencyKey;
  }
}
