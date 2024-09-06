package ru.egartech.staff.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.ProductsApi;
import ru.egartech.staff.model.ProductInfoPagingResponseDto;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;
import ru.egartech.staff.service.ProductService;

@RestController
@RequiredArgsConstructor
public class ProductRestController implements ProductsApi {

    private final ProductService productService;

    @Override
    public ProductInfoPagingResponseDto getAllProducts(Integer pageNumber, Integer pageSize,
                                                       String sortType, String sortFieldName, String searchingFilter) {
        return productService.getAllProducts(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
    }

    @Override
    public ProductInfoResponseDto getProductById(Long productId) {
        return productService.getProductById(productId);
    }

    @Override
    public void createProduct(ProductSaveRequestDto request) {
        productService.createProduct(request);
    }

    @Override
    public void updateProduct(Long productId, ProductSaveRequestDto request) {
        productService.updateProduct(productId, request);
    }

    @Override
    public void deleteProductById(Long productId) {
        productService.deleteProductById(productId);
    }
}
