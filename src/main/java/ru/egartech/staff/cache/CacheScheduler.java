package ru.egartech.staff.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.egartech.staff.service.*;

@Component
@RequiredArgsConstructor
public class CacheScheduler {

    private final StaffService staffService;
    private final StorageService storageService;
    private final ProductService productService;
    private final MaterialService materialService;
    private final OrderService orderService;

    @Scheduled(cron = "${app.cache.ttl.staff-period}")
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    @PostConstruct
    public void updateStaffCache() {
        staffService.getAllStaff(0, 10, "asc", "id", "");
    }

    @Scheduled(cron = "${app.cache.ttl.storages-period}")
    @CacheEvict(value = Caches.STORAGES_CACHE, allEntries = true)
    @PostConstruct
    public void updateStorageCache() {
        storageService.getAllStorages(0, 10, "asc", "id");
    }

    @Scheduled(cron = "${app.cache.ttl.products-period}")
    @CacheEvict(value = Caches.PRODUCTS_CACHE, allEntries = true)
    @PostConstruct
    public void updateProductCache() {
        productService.getAllProducts(0, 10, "asc", "id", "");
    }

    @Scheduled(cron = "${app.cache.ttl.materials-period}")
    @CacheEvict(value = Caches.MATERIALS_CACHE, allEntries = true)
    @PostConstruct
    public void updateMaterialCache() {
        materialService.getAllMaterials(0, 10, "asc", "id", "");
    }

    @Scheduled(cron = "${app.cache.ttl.orders-period}")
    @CacheEvict(value = Caches.ORDERS_CACHE, allEntries = true)
    @PostConstruct
    public void updateOrderCache() {
        orderService.getAllOrders(0, 10, "asc", "id", "");
    }
}
