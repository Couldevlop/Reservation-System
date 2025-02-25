package com.openlab.payment_service.utils;

import org.springframework.stereotype.Component;

@Component
public class CardValidationService {
    public boolean isValideNumber(String number){
        return number != null && number.startsWith("4") && number.length() == 16;
    }
}
