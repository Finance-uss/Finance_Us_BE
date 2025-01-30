package finance_us.finance_us.domain.follows.repository;

import finance_us.finance_us.domain.follows.entity.Follow;
import finance_us.finance_us.domain.notifications.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByUserIdAndFollowingId(Long userId, Long followingId);

    List<Follow> findByUserId(Long userId);

    boolean existsByUserIdAndFollowingId(Long userId, Long followingId);

    @Query("SELECT f FROM Follow f WHERE f.user.Id = :userId AND f.followingId > :lastFollowingId ORDER BY f.createdAt ASC")
    List<Follow> findByUserIdAndIdGreaterThan(
            @Param("userId") Long userId,
            @Param("lastFollowingId") Long lastFollowingId,
            Pageable pageable);
}

