package finance_us.finance_us.domain.account.entity;

import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    private LocalDate date;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private int score;

    private String content;

    private int totalLike;

    private int totalCheer;

    private String imageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_asset_id")
    private SubAsset subAsset;
}
