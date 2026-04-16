package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item_details")
@Getter
@Setter
public class OrderItemDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstyle_detail_id", nullable = false)
    private HairstyleDetail hairstyleDetail;

    @Column(name = "detail_name", nullable = false, length = 100)
    private String detailName;

    @Column(name = "additional_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalPrice;
}
