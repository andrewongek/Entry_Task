package com.entry_task.entry_task.repository;

import com.entry_task.entry_task.model.UserFavourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFavouriteRepository extends JpaRepository<UserFavourite, Long> {
    Optional<UserFavourite> findByUserIdAndProductId(Long userId, Long productId);
}
