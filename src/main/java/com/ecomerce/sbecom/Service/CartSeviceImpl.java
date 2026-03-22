package com.ecomerce.sbecom.Service;

import com.ecomerce.sbecom.Exceptions.ApiException;
import com.ecomerce.sbecom.Exceptions.ResourceNotFoundException;
import com.ecomerce.sbecom.Model.Cart;
import com.ecomerce.sbecom.Model.CartItem;
import com.ecomerce.sbecom.Model.Product;
import com.ecomerce.sbecom.Payload.CartDto;
import com.ecomerce.sbecom.Payload.ProductDTO;
import com.ecomerce.sbecom.Repository.CartItemRepository;
import com.ecomerce.sbecom.Repository.CartRepository;
import com.ecomerce.sbecom.Repository.ProductRepository;
import com.ecomerce.sbecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartSeviceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto addProductToCart(Long productId, Integer quantity) {
        // find existing cart or create one
        Cart cart = createCart();

        // Retrieve product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));

        // Perform Validations
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        if (cartItem!=null){
            throw new ApiException("Product "+product.getProductName()+" Already exist in the cart");
        }
        if(product.getQuantity() == 0.0){
            throw new ApiException("Product "+product.getProductName()+" is not available.");
        }

        if(product.getQuantity() < quantity){
            throw new ApiException("Please make an order of the "+product.getProductName()
                    +" less than or equal to the quantity "+product.getQuantity() +" .");
        }

        // Create cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //Save Cart Item
        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        //Return the updated cart
        CartDto cartDto = modelMapper.map(cart,CartDto.class);
        List<CartItem> cartItemList = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItemList.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartDto.setProducts(productStream.toList());
        return cartDto;
    }

    // helper.
    private  Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }


    @Override
    public List<CartDto> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new ApiException("No cart exists");
        }

        List<CartDto> cartDTOs = carts.stream().map(cart -> {
            CartDto cartDto = modelMapper.map(cart, CartDto.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());

            cartDto.setProducts(products);
            return cartDto;

        }).collect(Collectors.toList());

        return cartDTOs;
    }


    @Override
    public CartDto getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart == null) throw new ResourceNotFoundException("Cart","CartId",cartId);
        CartDto cartDto = modelMapper.map(cart,CartDto.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDto.setProducts(products);
        return cartDto;
    }

    @Override
    @Transactional
    public CartDto updateProdeuctQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","CartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));

        if(product.getQuantity() == 0.0){
            throw new ApiException("Product "+product.getProductName()+" is not available.");
        }

        if(product.getQuantity() < quantity){
            throw new ApiException("Please make an order of the "+product.getProductName()
                    +" less than or equal to the quantity "+product.getQuantity() +" .");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null){
            throw new ApiException("Product "+product.getProductName()+" not available in the cart.");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        //Validation to prevent negative quantities;
        if(newQuantity < 0 ){
            throw new ApiException("The resulting quantity can not be negative.");
        }

        if(newQuantity == 0) {
            deleteProductFromCart(cartId, productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDto cartDto = modelMapper.map(cart,CartDto.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct() ,ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });
        cartDto.setProducts(productDTOStream.toList());
        return cartDto;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","CartId",cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product","ProducyId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product "+cartItem.getProduct().getProductName() +" removed from the cart!!!!";
    }


    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","CartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem == null){
            throw new ApiException("Product "+product.getProductName() + "not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem = cartItemRepository.save(cartItem);
    }


}
