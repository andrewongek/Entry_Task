package com.entry_task.entry_task.user.repository;

import com.entry_task.entry_task.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  @Query(
      """
                select u
                from User u
                where u.id = :sellerId
                  and u.role = com.entry_task.entry_task.enums.Role.SELLER
            """)
  Optional<User> findSellerById(@Param("sellerId") Long sellerId);
}
