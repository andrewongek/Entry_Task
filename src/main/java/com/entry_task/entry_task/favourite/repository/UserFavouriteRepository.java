package com.entry_task.entry_task.favourite.repository;

import com.entry_task.entry_task.favourite.entity.UserFavourite;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFavouriteRepository extends JpaRepository<UserFavourite, Long> {
  Optional<UserFavourite> findByUserIdAndProductId(Long userId, Long productId);
}
