package com.marketplace.Digital_Marketplace.repositories;

import com.marketplace.Digital_Marketplace.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByReviewerId(Long reviewerId);
}
