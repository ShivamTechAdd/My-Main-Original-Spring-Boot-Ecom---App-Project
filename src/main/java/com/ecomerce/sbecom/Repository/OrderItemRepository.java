package com.ecomerce.sbecom.Repository;

import com.ecomerce.sbecom.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
