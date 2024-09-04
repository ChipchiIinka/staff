package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.model.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ProductMapperImpl.class)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void testToListDto() {
        ProductEntity productEntity1 = createProductEntity(1L);
        ProductEntity productEntity2 = createProductEntity(2L);

        List<ProductEntity> productList = List.of(productEntity1, productEntity2);
        Page<ProductEntity> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());

        List<ProductListInfoResponseDto> result = productMapper.toListDto(productPage);

        assertEquals(2, result.size());
        assertEquals("Название 1", result.get(0).getName());
        assertEquals("Название 2", result.get(1).getName());
        assertEquals(ProductTypeDto.OTHER, result.get(0).getType());
        assertEquals(ProductTypeDto.OTHER, result.get(1).getType());
        assertEquals(100, result.get(0).getPrice());
        assertEquals(200, result.get(1).getPrice());
    }

    @Test
    void testToDto() {
        ProductEntity productEntity = createProductEntity(1L);

        ProductListInfoResponseDto result = productMapper.toDto(productEntity);

        assertNotNull(result);
        assertEquals("Название 1", result.getName());
        assertEquals(ProductTypeDto.OTHER, result.getType());
        assertEquals(100, result.getPrice());
    }

    @Test
    void testToInfoResponseDto() {
        ProductEntity productEntity = createProductEntity(1L);
        Long available = 100L;

        List<ManualDto> manualSaveRequestDtoList = List.of(createManual(1L), createManual(2L));

        ProductInfoResponseDto result = productMapper.toDto(productEntity, manualSaveRequestDtoList, available);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Название 1", result.getName());
        assertEquals("Описание 1", result.getDescription());
        assertEquals(ProductTypeDto.OTHER, result.getType());
        assertEquals(100, result.getPrice());
        assertEquals(manualSaveRequestDtoList, result.getManual());
        assertEquals(100L, result.getAvailable());
    }

    @Test
    void toEntity() {
        ProductSaveRequestDto requestDto = new ProductSaveRequestDto()
                .name("Название 1")
                .description("Описание 1")
                .type(ProductTypeDto.OTHER)
                .price(100.0)
                .manual(List.of(createManual(1L), createManual(2L)));

        ProductEntity product = new ProductEntity();
        productMapper.toEntity(requestDto, product);

        assertNotNull(product);
        assertEquals("Название 1", product.getName());
        assertEquals("Описание 1", product.getDescription());
        assertEquals(ProductType.OTHER, product.getType());
        assertEquals(BigDecimal.valueOf(100.0), product.getPrice());
    }

    private static ProductEntity createProductEntity(Long productId) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setName("Название " + productId);
        productEntity.setDescription("Описание " + productId);
        productEntity.setType(ProductType.OTHER);
        productEntity.setPrice(BigDecimal.valueOf(productId * 100));
        return productEntity;
    }

    private static ManualDto createManual(Long id) {
        return new ManualDto()
                .materialId(id)
                .quantity(id.intValue() * 10);
    }
}