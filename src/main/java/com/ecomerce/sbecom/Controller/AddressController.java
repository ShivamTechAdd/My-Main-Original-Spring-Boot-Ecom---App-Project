package com.ecomerce.sbecom.Controller;

import com.ecomerce.sbecom.Model.User;
import com.ecomerce.sbecom.Payload.AddressDto;
import com.ecomerce.sbecom.Service.AddressService;
import com.ecomerce.sbecom.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;
    @Autowired
    AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto){
        User user = authUtil.loggedInUser();
        AddressDto savedAddressDto = addressService.createAddress(addressDto,user);
        return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddress(){
        List<AddressDto> addressList = addressService.getAddress();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId){
        AddressDto addressDto = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDto, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDto>> getAddressByUser(){
        User user = authUtil.loggedInUser();
        List<AddressDto> addressList = addressService.getUserAddress(user);
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddressById(@PathVariable Long addressId,@RequestBody AddressDto addressDto){
        AddressDto updatedAddress = addressService.updateAddress(addressId,addressDto);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
