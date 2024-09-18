package ru.egartech.staff.service;

import org.springframework.stereotype.Service;

//Сервис для mvc контроллеров
@Service
public class MvcService {

    /**
     * Генерация ссылки с сортировкой, пагинацией и поисковым фильтром.
     *
     * @param entityName        Имя сущности (например, orders, products)
     * @param field             Поле для сортировки
     * @param currentSortField  Текущее поле сортировки
     * @param currentSortType   Текущий тип сортировки (asc/desc)
     * @param pageNumber        Номер страницы для пагинации
     * @param pageSize          Размер страницы для пагинации
     * @param searchingFilter   Поисковый фильтр для фильтрации результатов
     * @return Сформированная ссылка с параметрами сортировки, пагинации и фильтрации
     */
    public String generateSortLink(String entityName, String field, String currentSortField, String currentSortType,
                                   int pageNumber, int pageSize, String searchingFilter) {
        // Логика изменения типа сортировки (asc/desc) и создания ссылки с поисковым фильтром
        String newSortType = "asc".equals(currentSortType) && field.equals(currentSortField) ? "desc" : "asc";
        return String.format("/api/%s?pageNumber=%d&pageSize=%d&sortFieldName=%s&sortType=%s&searchingFilter=%s",
                entityName, pageNumber, pageSize, field, newSortType, searchingFilter);
    }

    /**
     * Генерация ссылки с сортировкой и пагинацией без поискового фильтра.
     *
     * @param entityName        Имя сущности (например, orders, products)
     * @param field             Поле для сортировки
     * @param currentSortField  Текущее поле сортировки
     * @param currentSortType   Текущий тип сортировки (asc/desc)
     * @param pageNumber        Номер страницы для пагинации
     * @param pageSize          Размер страницы для пагинации
     * @return Сформированная ссылка с параметрами сортировки и пагинации
     */
    public String generateSortLink(String entityName, String field, String currentSortField, String currentSortType,
                                   int pageNumber, int pageSize) {
        // Логика изменения типа сортировки (asc/desc) и создания ссылки с поисковым фильтром
        String newSortType = "asc".equals(currentSortType) && field.equals(currentSortField) ? "desc" : "asc";
        return String.format("/api/%s?pageNumber=%d&pageSize=%d&sortFieldName=%s&sortType=%s",
                entityName, pageNumber, pageSize, field, newSortType);
    }
}
