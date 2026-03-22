package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Model.User;
import com.ecomerce.sbecom.Payload.AddressDto;

import java.util.List;

public interface AddressService {
    AddressDto createAddress(AddressDto addressDto, User user);
    List<AddressDto> getAddress();
    AddressDto getAddressById(Long addressId);

    List<AddressDto> getUserAddress(User user);

    AddressDto updateAddress(Long addressId, AddressDto addressDto);

    String deleteAddress(Long addressId);

}
