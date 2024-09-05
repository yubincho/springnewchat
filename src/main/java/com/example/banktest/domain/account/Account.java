package com.example.banktest.domain.account;


import com.example.banktest.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private Long number;  // 계좌번호

    @Column(nullable = false, length = 4)
    private Long password;  // 계좌비번

    @Column(nullable = false)
    private Long balance;

    // FK, 많은 쪽이 주인이다. (계좌가 많은 쪽)
    @ManyToOne(fetch = FetchType.LAZY)  // account.getUser().아무페이지호출() == Lazy 발동
    private User user; // user_id


    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @Builder
    public Account(Long id, Long number, Long password, Long balance,
                   User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
