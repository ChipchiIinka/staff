package ru.egartech.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.ProductsApi;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductListInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;
import ru.egartech.staff.service.ProductsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductsController implements ProductsApi {

    private final ProductsService productsService;

    @Override
    public List<ProductListInfoResponseDto> getAllProducts() {
        return productsService.getAllProducts();
    }

    @Override
    public ProductInfoResponseDto getProductById(Long productId) {
        return productsService.getProductById(productId);
    }

    @Override
    public void createProduct(ProductSaveRequestDto request) {
        productsService.createProduct(request);
    }

    @Override
    public void updateProduct(Long productId, ProductSaveRequestDto request) {
        productsService.updateProduct(productId, request);
    }
}
