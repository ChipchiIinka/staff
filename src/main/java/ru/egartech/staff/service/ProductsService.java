package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.model.ManualSaveRequestDto;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductListInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;
import ru.egartech.staff.repository.ProductsRepository;
import ru.egartech.staff.service.mapper.ProductsMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final ProductsMapper productMapper;

    public List<ProductListInfoResponseDto> getAllProducts(){
        return productMapper.toListDto(productsRepository.findAll());
    }

    public ProductInfoResponseDto getProductById(Long productId){
        List<ManualSaveRequestDto> manual = productMapper.toManualSaveRequestDto(getManualMapInfo(productId));
        return productMapper.toDto(productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")), manual); //TODO временная заглушка, поменять на обработчик
    }

    @Transactional
    public void createProduct(ProductSaveRequestDto productDto){
        List<ManualSaveRequestDto> manuals = productDto.getManual();
        ProductEntity productEntity = new ProductEntity();
        Long productId = productsRepository.save(productMapper.toEntity(productDto, productEntity)).getId();
        if(!manuals.isEmpty()){
            for(ManualSaveRequestDto manual : manuals){
                productsRepository.saveManual(productId, manual.getMaterial(), manual.getQuantity());
            }
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductSaveRequestDto productDto){
        List<ManualSaveRequestDto> manuals = productDto.getManual();
        ProductEntity productEntity = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")); //TODO временная заглушка, поменять на обработчик
        productsRepository.save(productMapper.toEntity(productDto, productEntity));
        if(!manuals.isEmpty()){
            productsRepository.deleteAllManualByProductId(productId);
            for(ManualSaveRequestDto manual : manuals){
                productsRepository.saveManual(productId, manual.getMaterial(), manual.getQuantity());
            }
        }
    }

    public Map<Long, Integer> getManualMapInfo(Long productId) {
        List<Object[]> results = productsRepository.findManualMapInfoMaterialQuantity(productId);
        Map<Long, Integer> manualMap = new HashMap<>();
        for (Object[] result : results) {
            Long materialId = (Long) result[0];
            Integer quantity = (Integer) result[1];
            manualMap.put(materialId, quantity);
        }
        return manualMap;
    }
}

