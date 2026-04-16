package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_records")
@Getter
@Setter
public class TransactionRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "operator", length = 50)
    private String operator;

    public enum TransactionType {
        RECHARGE,       // 充值
        CONSUMPTION,    // 消费
        REFUND,         // 退款
        ADJUSTMENT      // 调整
    }
}
