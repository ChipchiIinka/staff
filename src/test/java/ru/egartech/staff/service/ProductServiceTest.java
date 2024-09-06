package ru.egartech.staff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.ManualRepository;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.ManualMapper;
import ru.egartech.staff.service.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @Mock
    ManualRepository manualRepository;

    @Mock
    ManualMapper manualMapper;

    @Mock
    MaterialRepository materialRepository;

    @Mock
    StorageRepository storageRepository;

    @InjectMocks
    ProductService productService;

    private ProductEntity productEntity;
    private ProductListInfoResponseDto productListInfoResponseDto;
    private ProductInfoResponseDto productInfoResponseDto;
    private ProductSaveRequestDto productSaveRequestDto;

    @BeforeEach
    void setUp() {
        productEntity = new ProductEntity();
        productListInfoResponseDto = new ProductListInfoResponseDto();
        productInfoResponseDto = new ProductInfoResponseDto();
        productSaveRequestDto = new ProductSaveRequestDto();
    }

    @Test
    void testGetAllProducts() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id"));

        Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity), pageRequest, 3);
        List<ProductListInfoResponseDto> productDtos = List.of(productListInfoResponseDto);

        when(productRepository.findAll(pageRequest)).thenReturn(productEntities);
        when(productMapper.toListDto(productEntities)).thenReturn(productDtos);

        ProductInfoPagingResponseDto response = productService.getAllProducts(1, 2, "asc", "id");

        assertEquals(2, response.getPaging().getPages());
        assertEquals(3, response.getPaging().getCount());
        assertEquals(1, response.getPaging().getPageNumber());
        assertEquals(2, response.getPaging().getPageSize());
        assertEquals(productDtos, response.getContent());
    }

    @Test
    void testGetProductById() {
        Long productId = 1L;
        productEntity.setId(productId);
        productEntity.setName("Test Product");

        Long availableQuantity = 100L;
        List<ManualEntity> manualEntities = List.of();
        List<ManualDto> manualDtos = List.of(new ManualDto());


        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(storageRepository.findAvailableByProductId(productId)).thenReturn(availableQuantity);
        when(manualMapper.toManualDtoList(manualEntities)).thenReturn(manualDtos);
        when(productMapper.toDto(productEntity, manualDtos, availableQuantity)).thenReturn(productInfoResponseDto);

        ProductInfoResponseDto actualDto = productService.getProductById(productId);

        assertEquals(productInfoResponseDto, actualDto);
    }

    @Test
    void testCreateProduct() {
        MaterialEntity materialEntity = new MaterialEntity();
        materialEntity.setId(1L);
        ManualDto manualDto = new ManualDto();
        manualDto.setMaterialId(1L);
        manualDto.setQuantity(2);
        productSaveRequestDto.setManual(List.of(manualDto));
        productEntity.setId(1L);

        when(productMapper.toEntity(productSaveRequestDto, new ProductEntity())).thenReturn(productEntity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
        when(materialRepository.findById(materialEntity.getId())).thenReturn(Optional.of(materialEntity));
        when(productRepository.findById(productEntity.getId())).thenReturn(Optional.ofNullable(productEntity));

        ManualEntity manualEntity = new ManualEntity(productEntity, materialEntity, 2);
        when(manualRepository.save(any(ManualEntity.class))).thenReturn(manualEntity);

        productService.createProduct(productSaveRequestDto);

        verify(productRepository, times(1)).save(any(ProductEntity.class));
        verify(manualRepository, times(1)).save(any(ManualEntity.class));
    }


    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        ManualDto manualDto = new ManualDto();
        manualDto.setMaterialId(1L);
        manualDto.setQuantity(2);

        productSaveRequestDto.setManual(List.of(manualDto));
        productEntity.setId(1L);

        MaterialEntity materialEntity = new MaterialEntity();
        materialEntity.setId(1L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productMapper.toEntity(productSaveRequestDto, productEntity)).thenReturn(productEntity);
        when(materialRepository.findById(materialEntity.getId())).thenReturn(Optional.of(materialEntity));
        doNothing().when(manualRepository).deleteAllByProduct(productEntity);
        ManualEntity manualEntity = new ManualEntity(productEntity, materialEntity, 2);
        when(manualRepository.save(any(ManualEntity.class))).thenReturn(manualEntity);

        productService.updateProduct(productId, productSaveRequestDto);

        verify(manualRepository, times(1)).deleteAllByProduct(productEntity);
        verify(manualRepository, times(1)).save(any(ManualEntity.class));
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }


    @Test
    void testDeleteProductById() {
        Long productId = 1L;

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }
}