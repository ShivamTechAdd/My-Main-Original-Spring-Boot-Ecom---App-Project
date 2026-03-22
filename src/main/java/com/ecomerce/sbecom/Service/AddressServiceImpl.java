package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Exceptions.ApiException;
import com.ecomerce.sbecom.Exceptions.ResourceNotFoundException;
import com.ecomerce.sbecom.Model.Address;
import com.ecomerce.sbecom.Model.User;
import com.ecomerce.sbecom.Payload.AddressDto;
import com.ecomerce.sbecom.Repository.AddressRepository;
import com.ecomerce.sbecom.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDto createAddress(AddressDto addressDto, User user) {
        Address address = modelMapper.map(addressDto,Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(address,AddressDto.class);
    }

    @Override
    public List<AddressDto> getAddress() {
        List<Address> addressList = addressRepository.findAll();
        List<AddressDto> addressDtos = addressList.stream().map(address ->
                modelMapper.map(address,AddressDto.class)).toList();
        return addressDtos;
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));
        return modelMapper.map(address,AddressDto.class);
    }

    @Override
    public List<AddressDto> getUserAddress(User user) {
        List<Address> addressList = user.getAddresses();
        List<AddressDto> addressDtos = addressList.stream().map(address ->
                modelMapper.map(address,AddressDto.class)).toList();
        return addressDtos;
    }

    @Override
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));

        addressFromDb.setCity(addressDto.getCity());
        addressFromDb.setState(addressDto.getState());
        addressFromDb.setPincode(addressDto.getPincode());
        addressFromDb.setCountry(addressDto.getCountry());
        addressFromDb.setStreet(addressDto.getStreet());
        addressFromDb.setBuildingName(addressDto.getBuildingName());

        Address updatedAddress = addressRepository.save(addressFromDb);
        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,AddressDto.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDb = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address","AddressId",addressId));

        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDb);
        return "Address deleted Successfully with id "+addressId +" from the Database.";
    }


}
