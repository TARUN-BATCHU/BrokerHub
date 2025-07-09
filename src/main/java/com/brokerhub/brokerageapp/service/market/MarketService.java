package com.brokerhub.brokerageapp.service.market;

import com.brokerhub.brokerageapp.dto.market.MarketProductDTO;
import com.brokerhub.brokerageapp.dto.market.SellerRequestDTO;
import com.brokerhub.brokerageapp.dto.market.BuyerRequestDTO;

import java.util.List;

public interface MarketService {
    List<MarketProductDTO> getMarketProducts();
    MarketProductDTO addMarketProduct(MarketProductDTO productDTO);
    List<SellerRequestDTO> getSellerRequests();
    SellerRequestDTO submitSellerRequest(SellerRequestDTO requestDTO);
    SellerRequestDTO acceptSellerRequest(String requestId);
    List<BuyerRequestDTO> getBuyerRequests();
    BuyerRequestDTO submitBuyerRequest(BuyerRequestDTO requestDTO);
    BuyerRequestDTO acceptBuyerRequest(String requestId);
}