package finance_us.finance_us.domain.notifications.repository;

import finance_us.finance_us.domain.notifications.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
 /*   @Query("SELECT n FROM Notification n WHERE n.user.Id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);*/

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user.Id = :userId AND n.isRead = false")
    boolean existsUnreadNotificationsForUserId(@Param("userId") Long userId);

    // 첫 조회: 최신 알림
    @Query("SELECT n FROM Notification n WHERE n.user.Id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findTopByUserIdAAndIsReadFalseOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 스크롤: 특정 ID 이후 알림
    @Query("SELECT n FROM Notification n WHERE n.user.Id = :userId AND n.id < :lastNotificationId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndIdANDIsReadFalseLessThanOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("lastNotificationId") Long lastNotificationId,
            Pageable pageable
    );
}
