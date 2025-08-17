package com.brokerhub.brokerageapp.entity.market;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "buyer_request")
public class BuyerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", unique = true)
    private String requestId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "firm_name", nullable = false)
    private String firmName;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuyerRequestStatus status;

    @Column(name = "response_notes", columnDefinition = "TEXT")
    private String responseNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private MarketProduct product;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = BuyerRequestStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BuyerRequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}