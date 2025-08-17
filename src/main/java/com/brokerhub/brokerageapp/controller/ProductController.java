package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.BulkUploadResponseDTO;
import com.brokerhub.brokerageapp.dto.ProductBasicInfoDTO;
import com.brokerhub.brokerageapp.entity.Product;
import com.brokerhub.brokerageapp.service.ProductService;
import com.brokerhub.brokerageapp.utils.ProductExcelTemplateGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/BrokerHub/Product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity createProduct(@Valid @RequestBody Product product){
        return productService.createProduct(product);
    }

    @PostMapping(value = "/bulkUpload", consumes = "multipart/form-data")
    public ResponseEntity<BulkUploadResponseDTO> bulkUploadProducts(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(List.of("Please select a file to upload"))
                        .message("No file selected")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

            BulkUploadResponseDTO response = productService.bulkUploadProducts(file);

            if (response.getSuccessfulRecords() > 0) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            BulkUploadResponseDTO errorResponse = BulkUploadResponseDTO.builder()
                    .totalRecords(0)
                    .successfulRecords(0)
                    .failedRecords(0)
                    .errorMessages(List.of("Server error: " + e.getMessage()))
                    .message("Upload failed")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<ByteArrayResource> downloadBulkUploadTemplate() {
        return ProductExcelTemplateGenerator.generateTemplate();
    }

    @PutMapping("/updateProduct")
    public Object updateProduct(@Valid @RequestBody Product product){
        return productService.updateProduct(product);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity deleteProduct(@RequestParam Long productId){
        return productService.deleteProduct(productId);
    }

    @GetMapping("/allProducts")
    public List<Product> getAllProducts(Pageable pageable){
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/allProducts/")
    public List<Product> getAllProductsByName(@RequestParam String productName){
        return productService.getAllProductsByName(productName);
    }

    // ==================== OPTIMIZED CACHED ENDPOINTS ====================

    @GetMapping("/getProductNames")
    public ResponseEntity<List<String>> getProductNames() {
        List<String> productNames = productService.getProductNames();
        return ResponseEntity.ok(productNames);
    }

    @GetMapping("/getDistinctProductNames")
    public ResponseEntity<List<String>> getDistinctProductNames() {
        List<String> distinctProductNames = productService.getDistinctProductNames();
        return ResponseEntity.ok(distinctProductNames);
    }

    @GetMapping("/getProductNamesAndIds")
    public ResponseEntity<List<HashMap<String, Long>>> getProductNamesAndIds() {
        List<HashMap<String, Long>> productNamesAndIds = productService.getProductNamesAndIds();
        return ResponseEntity.ok(productNamesAndIds);
    }

    @GetMapping("/getBasicProductInfo")
    public ResponseEntity<List<ProductBasicInfoDTO>> getBasicProductInfo() {
        List<ProductBasicInfoDTO> basicProductInfo = productService.getBasicProductInfo();
        return ResponseEntity.ok(basicProductInfo);
    }

    @GetMapping("/getProductNamesAndQualities")
    public ResponseEntity<List<ProductBasicInfoDTO>> getProductNamesAndQualities() {
        List<ProductBasicInfoDTO> productNamesAndQualities = productService.getProductNamesAndQualities();
        return ResponseEntity.ok(productNamesAndQualities);
    }

    @GetMapping("/getProductNamesAndQualitiesAndQuantitesWithId")
    public ResponseEntity<List<Map<String,Long>>> getProductNamesAndQualitiesAndQuantitesWithId() {
        List<HashMap<String, Long>> productInfo = productService.getProductNamesAndQualitiesAndQuantitiesWithIds();
        return ResponseEntity.ok(productInfo.stream()
                .map(hashMap -> (Map<String, Long>) hashMap)
                .collect(java.util.stream.Collectors.toList()));
    }
}
