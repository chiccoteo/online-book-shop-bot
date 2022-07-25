package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayType extends AbsName{
    private Double commission;

    public PayType(String name, Double commission) {
        super(name);
        this.commission = commission;
    }
}
