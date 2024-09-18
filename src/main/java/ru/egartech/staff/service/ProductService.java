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

    /**
     * Получение списка всех товаров с постраничной навигацией, фильтрацией и сортировкой
     *
     * @param pageNo          Номер страницы
     * @param pageSize        Размер страницы
     * @param sortType        Тип сортировки (asc/desc)
     * @param sortFieldName   Поле для сортировки
     * @param searchingFilter Фильтр для поиска по названию или типу
     * @return Страница товаров с информацией о постраничной навигации
     */
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

    /**
     * Получение информации о товаре по его идентификатору
     *
     * @param productId Идентификатор товара
     * @return Информация о товаре, включая доступное количество на складе
     */
    @Cacheable(value = Caches.PRODUCTS_CACHE, key = "#productId")
    public ProductInfoResponseDto getProductById(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Этот товар не найден"));
        Long availableQuantity = storageRepository.findAvailableByProductId(productId);
        if (availableQuantity == null) {
            availableQuantity = 0L;
        }
        List<ManualDto> manual = manualMapper.toManualDtoList(product.getManuals());
        return productMapper.toDto(product, manual, availableQuantity);
    }

    /**
     * Создание нового товара, включая привязку мануалов (списка нужных материалов)
     *
     * @param productDto Данные для создания нового товара
     */
    @Transactional
    @CacheEvict(value = Caches.PRODUCTS_CACHE, allEntries = true)
    public void createProduct(ProductSaveRequestDto productDto) {
        List<ManualDto> manualsDto = productDto.getManual();
        Long productId = productRepository.save(productMapper.toEntity(productDto, new ProductEntity())).getId();
        manualsDto.forEach(manualDto ->
                createManual(productId, manualDto.getMaterialId(), manualDto.getQuantity()));
    }

    /**
     * Обновление информации о товаре по его идентификатору, включая обновление мануалов
     *
     * @param productId  Идентификатор товара
     * @param productDto DTO с обновленной информацией о товаре
     */
    @Transactional
    @CacheEvict(value = Caches.PRODUCTS_CACHE, key = "#productId")
    public void updateProduct(Long productId, ProductSaveRequestDto productDto) {
        List<ManualDto> manualsDto = productDto.getManual();
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Этот товар не найден"));
        if(!manualsDto.isEmpty()){
            manualRepository.deleteAllByProduct(productEntity);
            manualsDto.forEach(manualDto ->
                    createManual(productId, manualDto.getMaterialId(), manualDto.getQuantity()));
        }
        productRepository.save(productMapper.toEntity(productDto, productEntity));
    }

    /**
     * Удаление товара по его идентификатору
     *
     * @param productId Идентификатор товара
     */
    @CacheEvict(value = Caches.PRODUCTS_CACHE, allEntries = true)
    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }

    /**
     * Создание мануала (связь между товаром и материалом) для товара
     *
     * @param productId  Идентификатор товара
     * @param materialId Идентификатор материала
     * @param quantity   Количество материала для товара
     */
    private void createManual(Long productId, Long materialId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Материал не найден"));
        ManualEntity manual = new ManualEntity(product, material, quantity);
        manualRepository.save(manual);
    }
}

