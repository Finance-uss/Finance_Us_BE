package finance_us.finance_us.domain.user.entity;

import finance_us.finance_us.domain.common.entity.BaseEntity;
import finance_us.finance_us.domain.user.entity.status.AgeGroup;
import finance_us.finance_us.domain.user.entity.status.JobCategory;
import finance_us.finance_us.domain.user.entity.status.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup age;

    @Enumerated(EnumType.STRING)
    private JobCategory job;

    private String one_liner;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isAuthenticated=false;

}
