package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.ProductMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final StorageRepository storageRepository;

    public ProductInfoPagingResponseDto getAllProducts(Integer pageNo, Integer pageSize,
                                                       String sortType, String sortFieldName) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PagingDto paging = new PagingDto();
        Page<ProductEntity> productEntities = productRepository.findAll(
                PageRequest.of(pageNo, pageSize, sort));
        paging.setPageNumber(pageNo);
        paging.setPageSize(pageSize);
        paging.setCount(productEntities.getTotalElements());
        paging.setPages(productEntities.getTotalPages());
        return new ProductInfoPagingResponseDto()
                .paging(paging)
                .content(productMapper.toListDto(productEntities));
    }

    public ProductInfoResponseDto getProductById(Long productId) {
        Long availableQuantity = storageRepository.findAvailableByProductId(productId);
        List<ManualSaveRequestDto> manual = productMapper.toManualSaveRequestDto(getManualMapInfo(productId));
        return productMapper.toDto(productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден")), manual, availableQuantity);
    }

    @Transactional
    public void createProduct(ProductSaveRequestDto productDto) {
        List<ManualSaveRequestDto> manuals = productDto.getManual();
        ProductEntity productEntity = new ProductEntity();
        Long productId = productRepository.save(productMapper.toEntity(productDto, productEntity)).getId();
        if(!manuals.isEmpty()){
            for(ManualSaveRequestDto manual : manuals){
                productRepository.saveManual(productId, manual.getMaterial(), manual.getQuantity());
            }
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductSaveRequestDto productDto) {
        List<ManualSaveRequestDto> manuals = productDto.getManual();
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден"));
        productRepository.save(productMapper.toEntity(productDto, productEntity));
        if(!manuals.isEmpty()){
            productRepository.deleteAllManualByProductId(productId);
            for(ManualSaveRequestDto manual : manuals){
                productRepository.saveManual(productId, manual.getMaterial(), manual.getQuantity());
            }
        }
    }

    public Map<Long, Integer> getManualMapInfo(Long productId) {
        List<Object[]> results = productRepository.findManualMapInfoMaterialQuantity(productId);
        Map<Long, Integer> manualMap = new HashMap<>();
        for (Object[] result : results) {
            Long materialId = (Long) result[0];
            Integer quantity = (Integer) result[1];
            manualMap.put(materialId, quantity);
        }
        return manualMap;
    }

    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }
}

