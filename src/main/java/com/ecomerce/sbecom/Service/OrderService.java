package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Payload.OrderDto;

public interface OrderService {

    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName,
                        String pgPaymentId, String pgStatus, String pgResponseMessage);


}
