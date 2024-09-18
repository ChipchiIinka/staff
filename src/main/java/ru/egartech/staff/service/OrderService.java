package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.egartech.staff.cache.Caches;
import ru.egartech.staff.entity.*;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.repository.specification.OrderSpecification;
import ru.egartech.staff.service.mapper.OrderMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StaffRepository staffRepository;
    private final ProductRepository productRepository;

    /**
     * Получение информации о необходимых материалах для заказа по его идентификатору.
     *
     * @param orderId Идентификатор заказа
     * @return Информация о необходимых материалах для выполнения заказа(для всех товаров заказа)
     */
    @Cacheable(value = Caches.ORDERS_CACHE, key = "'materials:' + #orderId")
    public OrderMaterialInfoResponseDto getAllNeededMaterialsInfo(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ c id=" + orderId + " не найден"));
        List<ProductEntity> productList = order.getOrderDetails().getProducts();
        //Нахождение количества материалов с одинаковым id путем сложения
        Map<Long, ManualDto> materialMap = productList.stream()
                .flatMap(product -> product.getManuals().stream())
                .collect(Collectors.toMap(
                        ManualEntity::getMaterialId,
                        manualEntity -> new ManualDto()
                                .materialId(manualEntity.getId().getMaterialId())
                                .quantity(manualEntity.getQuantity()),
                        (existing, incoming) -> {
                            existing.setQuantity(existing.getQuantity() + incoming.getQuantity());
                            return existing;
                        }
                ));
        OrderMaterialInfoResponseDto orderMaterialInfoResponseDto = new OrderMaterialInfoResponseDto();
        orderMaterialInfoResponseDto.setId(orderId);
        orderMaterialInfoResponseDto.setNeededMaterials(new ArrayList<>(materialMap.values()));
        return orderMaterialInfoResponseDto;
    }

    /**
     * Получение списка всех заказов с постраничной навигацией, фильтрацией и сортировкой
     *
     * @param pageNo          Номер страницы
     * @param pageSize        Размер страницы
     * @param sortType        Тип сортировки (asc/desc)
     * @param sortFieldName   Поле для сортировки
     * @param searchingFilter Фильтр для поиска по статусу заказа
     * @return Страница заказов с информацией о постраничной навигации
     */
    @Cacheable(Caches.ORDERS_CACHE)
    public OrderInfoPagingResponseDto getAllOrders(Integer pageNo, Integer pageSize,
                                                   String sortType, String sortFieldName, String searchingFilter){
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Specification<OrderEntity> orderSpecification = Specification
                .where(OrderSpecification.hasStatus(searchingFilter));
        Page<OrderEntity> orderEntities = orderRepository.findAll(orderSpecification, pageRequest);
        PagingDto paging = new PagingDto()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .count(orderEntities.getTotalElements())
                .pages(orderEntities.getTotalPages());
        return new OrderInfoPagingResponseDto()
                .paging(paging)
                .content(orderMapper.toListDto(orderEntities));
    }

    /**
     * Получение информации о заказе по его идентификатору.
     *
     * @param orderId Идентификатор заказа
     * @return Полная информация о заказе, включая сотрудников и товары
     */
    @Cacheable(value = Caches.ORDERS_CACHE, key = "'order:' + #orderId")
    public OrderInfoResponseDto getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ не найден"));
        //Считаем дубликаты товара
        Map<ProductEntity, Long> products = order.getOrderDetails().getProducts().stream()
                .collect(Collectors.groupingBy(
                        product -> product, // Группируем по продукту
                        Collectors.counting() // Считаем количество каждого продукта
                ));
        OrderInfoResponseDto responseDto = orderMapper.toInfoResponseDto(order);
        responseDto.setOrderStaff(orderMapper.toStaffShortInfo(order.getOrderDetails().getStaff()));
        responseDto.setOrderProducts(orderMapper.toProductShortInfo(products));
        return responseDto;
    }

    /**
     * Создание нового заказа.
     *
     * @param orderDto DTO для создания нового заказа
     */
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void createOrder(OrderSaveRequestDto orderDto) {
        OrderEntity order = new OrderEntity();
        order.setOrderDetails(new OrderDetailsEntity());
        List<StaffEntity> staff = List.of(staffRepository.findById(orderDto.getManagerId())
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден")));
        List<ProductEntity> productList = orderDto.getOrderProducts().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден с id=" + productId)))
                .toList();
        BigDecimal amount = productList.stream()
                .map(ProductEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orderRepository.save(orderMapper.toEntity(orderDto, order, staff, productList, amount));
    }

    /**
     * Перевод заказа на следующий статус и привязка нового сотрудника, если требуется.
     *
     * @param orderId Идентификатор заказа
     * @param staffId Идентификатор сотрудника
     */
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void orderToNextStatus(Long orderId, Long staffId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ не найден"));
        StaffEntity staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        if (!order.getOrderDetails().getStaff().contains(staff)){
            order.getOrderDetails().getStaff().add(staff);
        }
        if (order.getOrderDetails().getStatus().equals(Status.DELIVERY)){
            order.getOrderDetails().setPaid(true);
        }
        toNextStatus(order);
        orderRepository.save(order);
    }

    /**
     * Перезапуск заказа, путем перевода в статус "Подготовка", если это возможно.
     *
     * @param orderId Идентификатор заказа
     */
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void orderToPreparationStatus(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ с таким id не найден"));
        if (Status.canRestart.contains(order.getOrderDetails().getStatus())) {
            order.getOrderDetails().setStatus(Status.PREPARATION);
            orderRepository.save(order);
        } else throw new StaffException(ErrorType.CLIENT_ERROR, "Нельзя перезапустить заказ с этого состояния");
    }

    /**
     * Перевод заказа в статус "Отменен".
     *
     * @param orderId Идентификатор заказа
     */
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void orderToCancelStatus(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ с таким id не найден"));
        order.getOrderDetails().setStatus(Status.CANCELED);
        orderRepository.save(order);
    }

    /**
     * Удаление заказа по его идентификатору.
     *
     * @param orderId Идентификатор заказа
     */
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void deleteOrderById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    /**
     * Удаление всех старых отмененных заказов, срок которых превышает один месяц.
     */
    public void deleteAllUnusedOrders(){
        LocalDate thresholdDate = LocalDate.now().minusMonths(1);
        orderRepository.deleteExpiredCanceledOrders(thresholdDate, Status.CANCELED);
    }

    /**
     * Удаление старых отмененных заказов в конце каждого месяца.
     * Запуск по расписанию в последний день месяца в 23:59:59.
     */
    @Scheduled(cron = "59 59 23 L * ?")
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    public void deleteOldUnusedOrdersAtEveryEndOfMonth() {
        deleteAllUnusedOrders();
    }

    private void toNextStatus(OrderEntity order){
        Status status = order.getOrderDetails().getStatus();
        switch (status){
            case ACCEPTED -> order.getOrderDetails().setStatus(Status.PREPARATION);
            case PREPARATION -> order.getOrderDetails().setStatus(Status.ASSEMBLY);
            case ASSEMBLY -> order.getOrderDetails().setStatus(Status.PACKAGING);
            case PACKAGING -> order.getOrderDetails().setStatus(Status.WAITING_FOR_DELIVERY);
            case WAITING_FOR_DELIVERY -> order.getOrderDetails().setStatus(Status.DELIVERY);
            case CANCELED -> order.getOrderDetails().setStatus(Status.CANCELED);
            default -> order.getOrderDetails().setStatus(Status.COMPLETED);
        }
    }
}
