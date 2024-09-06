package ru.egartech.staff.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egartech.staff.model.*;
import ru.egartech.staff.service.OrderService;
import ru.egartech.staff.service.ProductService;

import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderMvcController {

    private static final String MODEL_ATTRIBUTE_ORDER = "order";
    private static final String REDIRECT_TO_ORDERS = "redirect:/api/orders";
    private static final String REDIRECT_TO_ORDERS_WITH_ID = "redirect:/api/orders/";

    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping
    public String getAllOrders(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
            @RequestParam(value = "sortFieldName", required = false, defaultValue = "id") String sortFieldName,
            @RequestParam(value = "searchingFilter", required = false, defaultValue = "") String searchingFilter,
            Model model) {
        var orders = orderService.getAllOrders(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
        model.addAttribute("orders", orders);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortType", sortType);
        model.addAttribute("sortFieldName", sortFieldName);
        model.addAttribute("searchingFilter", searchingFilter);
        model.addAttribute("sortLink", (Function<String, String>) field ->
                orderService.generateSortLink(field, sortFieldName, sortType, pageNumber, pageSize, searchingFilter));
        return "orders/list";
    }

    @GetMapping("/{orderId}")
    public String getOrderById(@PathVariable Long orderId, Model model) {
        OrderInfoResponseDto responseOrder = orderService.getOrderById(orderId);
        OrderMaterialInfoResponseDto responseNeededMaterials = orderService.getAllNeededMaterialsInfo(orderId);
        model.addAttribute(MODEL_ATTRIBUTE_ORDER, responseOrder);
        model.addAttribute("neededMaterials", responseNeededMaterials);
        return "orders/info";
    }

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_ORDER, new OrderSaveRequestDto());
        model.addAttribute("products", productService.getAllProducts(0,100, "asc","id", ""));
        return "orders/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute(MODEL_ATTRIBUTE_ORDER) @Valid OrderSaveRequestDto orderSaveRequestDto, BindingResult result) {
        if (result.hasErrors()) {
            return "orders/create";
        }
        orderService.createOrder(orderSaveRequestDto);
        return REDIRECT_TO_ORDERS;
    }

    @PatchMapping("/{orderId}/{staffId}")
    public String updateOrderStatus(@PathVariable Long orderId, @PathVariable Long staffId) {
        orderService.orderToNextStatus(orderId, staffId);
        return REDIRECT_TO_ORDERS_WITH_ID + orderId;
    }

    @PatchMapping("/{orderId}/restart")
    public String updateOrderStatusToPreparation(@PathVariable Long orderId) {
        orderService.orderToPreparationStatus(orderId);
        return REDIRECT_TO_ORDERS_WITH_ID + orderId;
    }

    @PatchMapping("/{orderId}/cancel")
    public String updateOrderStatusToCancel(@PathVariable Long orderId) {
        orderService.orderToCancelStatus(orderId);
        return REDIRECT_TO_ORDERS_WITH_ID + orderId;
    }

    @DeleteMapping("/{orderId}/delete")
    public String deleteOrderById(@PathVariable Long orderId) {
        orderService.deleteOrderById(orderId);
        return REDIRECT_TO_ORDERS;
    }
}
