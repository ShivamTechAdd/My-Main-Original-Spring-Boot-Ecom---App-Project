package com.ecomerce.sbecom.Repository;

import com.ecomerce.sbecom.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
