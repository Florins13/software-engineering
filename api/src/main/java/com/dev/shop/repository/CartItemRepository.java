package com.dev.shop.repository;

import com.dev.shop.model.CartItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartItemRepository implements PanacheRepository<CartItem> {

    public void emptyCart(){
//        this.deleteById(id);
        this.deleteAll();
    }

    public void deleteWhereCartIdAndCartItemId(Long cartItemId, Long cartId){
        this.delete("id = ?1 and cart.id = ?2", cartItemId, cartId);
    }

}
