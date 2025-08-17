package com.market.service;

import com.market.entity.MarketProduct;
import com.market.entity.SellerRequest;
import com.market.entity.BuyerRequest;
import com.market.repository.MarketProductRepository;
import com.market.repository.SellerRequestRepository;
import com.market.repository.BuyerRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarketService {
    @Autowired
    private MarketProductRepository marketProductRepository;

    @Autowired
    private SellerRequestRepository sellerRequestRepository;

    @Autowired
    private BuyerRequestRepository buyerRequestRepository;

    // Market Product Operations
    public List<MarketProduct> getAllProducts() {
        return marketProductRepository.findByAvailableUntilGreaterThanEqual(LocalDateTime.now());
    }

    public List<MarketProduct> getBrokerProducts(Long brokerId) {
        return marketProductRepository.findByBrokerIdAndAvailableUntilGreaterThanEqual(brokerId, LocalDateTime.now());
    }

    @Transactional
    public MarketProduct addProduct(MarketProduct product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return marketProductRepository.save(product);
    }

    // Seller Request Operations
    public List<SellerRequest> getSellerRequests(Long brokerId) {
        return sellerRequestRepository.findByBrokerIdAndStatus(brokerId, SellerRequest.RequestStatus.PENDING);
    }

    @Transactional
    public SellerRequest submitSellerRequest(SellerRequest request) {
        request.setStatus(SellerRequest.RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return sellerRequestRepository.save(request);
    }

    @Transactional
    public SellerRequest acceptSellerRequest(Long requestId) {
        Optional<SellerRequest> requestOpt = sellerRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            SellerRequest request = requestOpt.get();
            request.setStatus(SellerRequest.RequestStatus.ACCEPTED);
            request.setUpdatedAt(LocalDateTime.now());

            // Create a new market product from the accepted request
            MarketProduct product = new MarketProduct();
            product.setProductName(request.getProductName());
            product.setQuality(request.getQuality());
            product.setQuantity(request.getQuantity());
            product.setPrice(request.getPrice());
            product.setDescription(request.getDescription());
            product.setAvailableUntil(request.getAvailableUntil());
            product.setBrokerId(request.getBrokerId());
            
            marketProductRepository.save(product);
            return sellerRequestRepository.save(request);
        }
        throw new RuntimeException("Seller request not found");
    }

    // Buyer Request Operations
    public List<BuyerRequest> getBuyerRequests(Long productId) {
        return buyerRequestRepository.findByProductIdAndStatus(productId, BuyerRequest.RequestStatus.PENDING);
    }

    @Transactional
    public BuyerRequest submitBuyerRequest(BuyerRequest request) {
        request.setStatus(BuyerRequest.RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return buyerRequestRepository.save(request);
    }

    @Transactional
    public BuyerRequest acceptBuyerRequest(Long requestId) {
        Optional<BuyerRequest> requestOpt = buyerRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            BuyerRequest request = requestOpt.get();
            request.setStatus(BuyerRequest.RequestStatus.ACCEPTED);
            request.setUpdatedAt(LocalDateTime.now());
            return buyerRequestRepository.save(request);
        }
        throw new RuntimeException("Buyer request not found");
    }
}