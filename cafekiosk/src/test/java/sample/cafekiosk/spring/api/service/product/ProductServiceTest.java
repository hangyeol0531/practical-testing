package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeAll
    static void beforeAll() {

    }

    @BeforeEach
    void setUp() {
        // 각 테스트 입장에서 봤을때, 아예 몰라도 테스트 내용을 이해하는데 문제가 없는가
        // 수정해도 모든 테스트에 영향을 주지 않는가
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다.")
    @Test
    void createProduct() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
            .type(HANDMADE)
            .sellingStatus(SELLING)
            .name("카푸치노")
            .price(5000)
            .build();

        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        assertThat(productResponse)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .contains("004", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products)
            .hasSize(4)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                tuple("002", HANDMADE, HOLD, "카페라떼", 4500),
                tuple("003", HANDMADE, STOP_SELLING, "팥빙수", 7000),
                tuple("004", HANDMADE, SELLING, "카푸치노", 5000)
            );
    }

    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품 번호는 001이다.")
    @Test
    void createProductWhenProductsIsEmpty() {
        // given
        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
            .type(HANDMADE)
            .sellingStatus(SELLING)
            .name("카푸치노")
            .price(5000)
            .build();

        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        assertThat(productResponse)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .contains("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products)
            .hasSize(1)
            .extracting("productNumber", "type", "sellingStatus", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
            );
    }

    private Product createProduct(String productNumber, ProductType handmade, ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
            .productNumber(productNumber)
            .type(handmade)
            .sellingStatus(sellingStatus)
            .name(name)
            .price(price)
            .build();
    }

}