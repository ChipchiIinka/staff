package ru.egartech.staff.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;
import ru.egartech.staff.service.ProductService;

import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/products")
public class ProductMvcController {

    private static final String MODEL_ATTRIBUTE_PRODUCT = "product";
    private static final String REDIRECT_TO_PRODUCTS = "redirect:/api/products";
    private static final String REDIRECT_TO_PRODUCTS_WITH_ID = "redirect:/api/products/";

    private final ProductService productService;

    @GetMapping
    public String getAllProducts(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
            @RequestParam(value = "sortFieldName", required = false, defaultValue = "id") String sortFieldName,
            @RequestParam(value = "searchingFilter", required = false, defaultValue = "") String searchingFilter,
            Model model) {
        var response = productService.getAllProducts(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
        model.addAttribute("products", response);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortType", sortType);
        model.addAttribute("sortFieldName", sortFieldName);
        model.addAttribute("searchingFilter", searchingFilter);
        model.addAttribute("sortLink", (Function<String, String>) field ->
                productService.generateSortLink(field, sortFieldName, sortType, pageNumber, pageSize, searchingFilter));
        return "products/list";
    }

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        ProductInfoResponseDto response = productService.getProductById(productId);
        model.addAttribute(MODEL_ATTRIBUTE_PRODUCT, response);
        return "products/info";
    }

    @GetMapping("/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_PRODUCT, new ProductSaveRequestDto());
        return "products/create";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute(MODEL_ATTRIBUTE_PRODUCT) @Valid ProductSaveRequestDto request,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "products/create";
        }
        productService.createProduct(request);
        return REDIRECT_TO_PRODUCTS;
    }

    @GetMapping("/{productId}/update")
    public String showUpdateProductForm(@PathVariable Long productId, Model model) {
        ProductInfoResponseDto product = productService.getProductById(productId);
        model.addAttribute(MODEL_ATTRIBUTE_PRODUCT, product);
        return "products/update";
    }

    @PutMapping("/{productId}/update")
    public String updateProduct(@PathVariable Long productId,
                                @ModelAttribute(MODEL_ATTRIBUTE_PRODUCT) @Valid ProductSaveRequestDto request,
                                BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "products/update";
        }
        productService.updateProduct(productId, request);
        return REDIRECT_TO_PRODUCTS_WITH_ID + productId;
    }

    @DeleteMapping("/{productId}/delete")
    public String deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return REDIRECT_TO_PRODUCTS;
    }

}
