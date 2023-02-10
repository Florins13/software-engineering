package org.uab.shop.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.uab.shop.model.CartItem;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartItemRepository implements PanacheRepository<CartItem> {

    public void deleteCartItemById(Long id){
        this.delete("cart_id", id);
    }

    public void deleteWhereCartIdAndCartItemId(Long cartItemId, Long cartId){
        this.delete("id = ?1 and cart_id = ?2", cartItemId, cartId);
    }

}
