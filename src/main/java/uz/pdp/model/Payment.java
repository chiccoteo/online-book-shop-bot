package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private UUID id = UUID.randomUUID();
    private Double paySum;
    private PayType payType;

    public Payment(Double paySum, PayType payType) {
        this.paySum = paySum;
        this.payType = payType;
    }
}
