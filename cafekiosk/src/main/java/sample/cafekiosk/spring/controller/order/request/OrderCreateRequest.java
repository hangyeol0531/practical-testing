package sample.cafekiosk.spring.controller.order.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {

    private List<String> productNumber;

    @Builder
    public OrderCreateRequest(List<String> productNumber) {
        this.productNumber = productNumber;
    }
}
