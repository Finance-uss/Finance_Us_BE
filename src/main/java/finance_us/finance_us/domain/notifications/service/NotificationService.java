package finance_us.finance_us.domain.notifications.service;

import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.notifications.dto.NotificationListResponse;
import finance_us.finance_us.domain.notifications.dto.NotificationResponse;
import finance_us.finance_us.domain.notifications.dto.UnreadNotificationsResponse;
import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.entity.status.ResourceType;
import finance_us.finance_us.domain.notifications.entity.status.Type;
import finance_us.finance_us.domain.notifications.repository.NotificationRepository;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(String token, Long lastNotificationId, int size) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        List<Notification> notifications;
        PageRequest pageRequest = PageRequest.of(0, size);

        if (lastNotificationId == null) {
            // 첫 조회: 최신 알림부터 가져오기
            notifications = notificationRepository.findTopByUserIdAAndIsReadFalseOrderByCreatedAtDesc(userId, pageRequest);
        } else {
            // 스크롤: 특정 ID 이후의 알림 가져오기
            notifications = notificationRepository.findByUserIdAndIdANDIsReadFalseLessThanOrderByCreatedAtDesc(userId, lastNotificationId, pageRequest);
        }

        return NotificationListResponse.fromEntities(notifications, this);
    }

    //리소스 제목 조회
    public String getResourceTitle(ResourceType resourceType, Long resourceId) {
        if(resourceType == null || resourceId == null) {
            return null;
        }
        if (resourceType == ResourceType.POST) {
            return postRepository.findById(resourceId)
                    .map(Post::getTitle)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));
        } else if (resourceType == ResourceType.ACCOUNT) {
            return accountRepository.findById(resourceId)
                    .map(Account::getTitle)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
        }
        throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
    }

    @Transactional
    public void markAsRead(String token, Long notificationId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if(!notification.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        if(notification.getIsRead()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_ALREADY_READ);
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationsResponse hasUnreadNotifications(String token) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        boolean hasUnread = notificationRepository.existsUnreadNotificationsForUserId(userId);
        return new UnreadNotificationsResponse(hasUnread);
    }

    //팔로우 시 알림 생성
    public void addFollowNotification(Long targetUserId, Long followerId){
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String message = follower.getName() + "님이 팔로우 했습니다.";

        Notification notification = Notification.builder()
                .type(Type.FOLLOW)
                .message(message)
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);

    }

    //가계부에 이모지 추가 시 알림 생성
    public void addEmojiNotification(Long accountId, Long senderUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 알림 대상은 가계부 주인
        User targetUser = account.getUser();
        //if (targetUser.getId().equals(senderUserId)) return; // 자기 자신에게 알림 X

        String message = "해당 가계부에 느낌을 표시했습니다.";

        Notification notification = Notification.builder()
                .type(Type.EMOJI)
                .message(message)
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId)
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);
    }

    //게시글에 좋아요 추가 시 알림 생성
    public void addPostLikeNotification(Long postId, Long senderUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 알림 대상은 게시글 주인
        User targetUser = post.getUser();
        //if (targetUser.getId().equals(senderUserId)) return; // 자기 자신에게 알림 X

        String message = "해당 게시글에 좋아요가 달렸습니다.";

        Notification notification = Notification.builder()
                .type(Type.LIKE)
                .message(message)
                .resourceType(ResourceType.POST)
                .resourceId(postId)
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);
    }

    //게시글에 댓글 추가 시 알림 생성
    public void addCommentNotification(Long postId, Long senderUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 알림 대상은 게시글 주인
        User targetUser = post.getUser();
        //if (targetUser.getId().equals(senderUserId)) return; // 자기 자신에게 알림 X

        String message = "해당 게시글에 댓글이 달렸습니다.";

        Notification notification = Notification.builder()
                .type(Type.COMMENT)
                .message(message)
                .resourceType(ResourceType.POST)
                .resourceId(postId)
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);
    }

    //댓글에 좋아요 추가 시 알림 생성
    public void addCommentLikeNotification(Long commentId, Long senderUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 알림 대상은 댓글 작성자
        User targetUser = comment.getUser();
        //if (targetUser.getId().equals(senderUserId)) return; // 자기 자신에게 알림 X


        // 해당 댓글이 달린 게시글을 찾아서 resource로 전달
        Post post = comment.getPost();

        //댓글인지, 대댓글인지 확인
        boolean isReply = comment.getParentComment() != null;

        String message = isReply
                ? "해당 게시글에 작성한 답글에 좋아요가 달렸습니다."
                : "해당 게시글에 작성한 댓글에 좋아요가 달렸습니다.";

        Notification notification = Notification.builder()
                .type(Type.LIKE)
                .message(message)
                .resourceType(ResourceType.POST)
                .resourceId(post.getId()) // 댓글이 달린 게시글 ID 전달
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);
    }

    //댓글에 대댓글이 달린 경우
    public void addReplyNotification(Long parentCommentId, Long senderUserId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 알림 대상은 부모 댓글을 작성한 사용자
        User targetUser = parentComment.getUser();

        // 부모 댓글이 달린 게시글을 찾아서 resource로 전달
        Post post = parentComment.getPost();

        // 🔹 자기 댓글에 대댓글을 달 경우 알림을 보내지 않음
        //if (targetUser.getId().equals(senderUserId)) return; 테스트를 위해 자신에게도 알림이 가도록 설정


        String message = "해당 게시글에 작성한 댓글에 답글이 달렸습니다.";

        Notification notification = Notification.builder()
                .type(Type.REPLY) // REPLY 타입의 알림
                .message(message)
                .resourceType(ResourceType.POST) // 리소스 타입은 POST
                .resourceId(post.getId()) // 대댓글이 달린 게시글 ID
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);
    }
}
