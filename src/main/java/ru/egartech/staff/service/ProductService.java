package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.egartech.staff.cache.Caches;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.ManualRepository;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.repository.specification.ProductSpecification;
import ru.egartech.staff.service.mapper.ManualMapper;
import ru.egartech.staff.service.mapper.ProductMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final ManualRepository manualRepository;
    private final ManualMapper manualMapper;

    private final StorageRepository storageRepository;
    private final MaterialRepository materialRepository;

    @Cacheable(Caches.PRODUCTS_CACHE)
    public ProductInfoPagingResponseDto getAllProducts(Integer pageNo, Integer pageSize,
                                                       String sortType, String sortFieldName, String searchingFilter) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Specification<ProductEntity> productSpecification = Specification
                .where(ProductSpecification.hasName(searchingFilter))
                .or(ProductSpecification.hasType(searchingFilter));
        Page<ProductEntity> productEntities = productRepository.findAll(productSpecification, pageRequest);
        PagingDto paging = new PagingDto()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .count(productEntities.getTotalElements())
                .pages(productEntities.getTotalPages());
        return new ProductInfoPagingResponseDto()
                .paging(paging)
                .content(productMapper.toListDto(productEntities));
    }

    @Cacheable(value = Caches.PRODUCTS_CACHE, key = "#productId")
    public ProductInfoResponseDto getProductById(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден"));
        Long availableQuantity = storageRepository.findAvailableByProductId(productId);
        if (availableQuantity == null) {
            availableQuantity = 0L;
        }
        List<ManualDto> manual = manualMapper.toManualDtoList(product.getManuals());
        return productMapper.toDto(product, manual, availableQuantity);
    }

    @Transactional
    @CacheEvict(value = Caches.PRODUCTS_CACHE, allEntries = true)
    public void createProduct(ProductSaveRequestDto productDto) {
        List<ManualDto> manualsDto = productDto.getManual();
        Long productId = productRepository.save(productMapper.toEntity(productDto, new ProductEntity())).getId();
        manualsDto.forEach(manualDto ->
                createManual(productId, manualDto.getMaterialId(), manualDto.getQuantity()));
    }

    @Transactional
    @CacheEvict(value = Caches.PRODUCTS_CACHE, key = "#productId")
    public void updateProduct(Long productId, ProductSaveRequestDto productDto) {
        List<ManualDto> manualsDto = productDto.getManual();
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден"));
        if(!manualsDto.isEmpty()){
            manualRepository.deleteAllByProduct(productEntity);
            manualsDto.forEach(manualDto ->
                    createManual(productId, manualDto.getMaterialId(), manualDto.getQuantity()));
        }
        productRepository.save(productMapper.toEntity(productDto, productEntity));
    }

    @CacheEvict(value = Caches.PRODUCTS_CACHE, allEntries = true)
    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }

    private void createManual(Long productId, Long materialId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        ManualEntity manual = new ManualEntity(product, material, quantity);
        manualRepository.save(manual);
    }
}

