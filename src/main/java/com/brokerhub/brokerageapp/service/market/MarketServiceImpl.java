package com.brokerhub.brokerageapp.service.market;

import com.brokerhub.brokerageapp.dto.market.MarketProductDTO;
import com.brokerhub.brokerageapp.dto.market.SellerRequestDTO;
import com.brokerhub.brokerageapp.dto.market.BuyerRequestDTO;
import com.brokerhub.brokerageapp.entity.market.MarketProduct;
import com.brokerhub.brokerageapp.entity.market.SellerRequest;
import com.brokerhub.brokerageapp.entity.market.BuyerRequest;
import com.brokerhub.brokerageapp.repository.market.MarketProductRepository;
import com.brokerhub.brokerageapp.repository.market.SellerRequestRepository;
import com.brokerhub.brokerageapp.repository.market.BuyerRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final MarketProductRepository marketProductRepository;
    private final SellerRequestRepository sellerRequestRepository;
    private final BuyerRequestRepository buyerRequestRepository;

    @Override
    public List<MarketProductDTO> getMarketProducts() {
        return marketProductRepository.findAll().stream()
            .map(this::toMarketProductDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MarketProductDTO addMarketProduct(MarketProductDTO productDTO) {
        MarketProduct product = MarketProduct.builder()
            .productId(UUID.randomUUID().toString())
            .productName(productDTO.getProductName())
            .quality(productDTO.getQuality())
            .quantity(productDTO.getQuantity())
            .price(productDTO.getPrice())
            .description(productDTO.getDescription())
            .availableUntil(productDTO.getAvailableUntil())
            .firmName(productDTO.getSeller().getFirmName())
            .location(productDTO.getSeller().getLocation())
            .status(MarketProduct.MarketProductStatus.ACTIVE)
            .build();

        return toMarketProductDTO(marketProductRepository.save(product));
    }

    @Override
    public List<SellerRequestDTO> getSellerRequests() {
        return sellerRequestRepository.findAll().stream()
            .map(this::toSellerRequestDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SellerRequestDTO submitSellerRequest(SellerRequestDTO requestDTO) {
        SellerRequest request = SellerRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .productName(requestDTO.getProductName())
            .quality(requestDTO.getQuality())
            .quantity(requestDTO.getQuantity())
            .price(requestDTO.getPrice())
            .description(requestDTO.getDescription())
            .availableUntil(requestDTO.getAvailableUntil())
            .firmName(requestDTO.getSeller().getFirmName())
            .location(requestDTO.getSeller().getLocation())
            .status(SellerRequest.SellerRequestStatus.PENDING)
            .build();

        return toSellerRequestDTO(sellerRequestRepository.save(request));
    }

    @Override
    @Transactional
    public SellerRequestDTO acceptSellerRequest(String requestId) {
        SellerRequest request = sellerRequestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new RuntimeException("Seller request not found"));

        request.setStatus(SellerRequest.SellerRequestStatus.ACCEPTED);
        
        // Create a new market product from the accepted request
        MarketProduct product = MarketProduct.builder()
            .productId(UUID.randomUUID().toString())
            .productName(request.getProductName())
            .quality(request.getQuality())
            .quantity(request.getQuantity())
            .price(request.getPrice())
            .description(request.getDescription())
            .availableUntil(request.getAvailableUntil())
            .firmName(request.getFirmName())
            .location(request.getLocation())
            .status(MarketProduct.MarketProductStatus.ACTIVE)
            .build();

        marketProductRepository.save(product);
        return toSellerRequestDTO(sellerRequestRepository.save(request));
    }

    @Override
    public List<BuyerRequestDTO> getBuyerRequests() {
        return buyerRequestRepository.findAll().stream()
            .map(this::toBuyerRequestDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BuyerRequestDTO submitBuyerRequest(BuyerRequestDTO requestDTO) {
        BuyerRequest request = BuyerRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .productId(Long.valueOf(requestDTO.getProductId()))
            .quantity(requestDTO.getQuantity())
            .firmName(requestDTO.getBuyer().getFirmName())
            .location(requestDTO.getBuyer().getLocation())
            .status(BuyerRequest.BuyerRequestStatus.PENDING)
            .build();

        return toBuyerRequestDTO(buyerRequestRepository.save(request));
    }

    @Override
    @Transactional
    public BuyerRequestDTO acceptBuyerRequest(String requestId) {
        BuyerRequest request = buyerRequestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new RuntimeException("Buyer request not found"));

        request.setStatus(BuyerRequest.BuyerRequestStatus.ACCEPTED);

        // Update the market product quantity
        MarketProduct product = marketProductRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        BigDecimal remainingQuantity = product.getQuantity().subtract(request.getQuantity());
        if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            product.setStatus(MarketProduct.MarketProductStatus.SOLD);
        }
        product.setQuantity(remainingQuantity);

        marketProductRepository.save(product);
        return toBuyerRequestDTO(buyerRequestRepository.save(request));
    }

    private MarketProductDTO toMarketProductDTO(MarketProduct product) {
        return MarketProductDTO.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .quality(product.getQuality())
            .quantity(product.getQuantity())
            .price(product.getPrice())
            .description(product.getDescription())
            .availableUntil(product.getAvailableUntil())
            .seller(MarketProductDTO.SellerInfoDTO.builder()
                .firmName(product.getFirmName())
                .location(product.getLocation())
                .build())
            .build();
    }

    private SellerRequestDTO toSellerRequestDTO(SellerRequest request) {
        return SellerRequestDTO.builder()
            .requestId(request.getRequestId())
            .productName(request.getProductName())
            .quality(request.getQuality())
            .quantity(request.getQuantity())
            .price(request.getPrice())
            .description(request.getDescription())
            .availableUntil(request.getAvailableUntil())
            .status(request.getStatus().name())
            .seller(SellerRequestDTO.SellerInfoDTO.builder()
                .firmName(request.getFirmName())
                .location(request.getLocation())
                .build())
            .build();
    }

    private BuyerRequestDTO toBuyerRequestDTO(BuyerRequest request) {
        return BuyerRequestDTO.builder()
            .requestId(request.getRequestId())
            .productId(String.valueOf(request.getProductId()))
            .quantity(request.getQuantity())
            .status(request.getStatus().name())
            .buyer(BuyerRequestDTO.BuyerInfoDTO.builder()
                .firmName(request.getFirmName())
                .location(request.getLocation())
                .build())
            .build();
    }
}