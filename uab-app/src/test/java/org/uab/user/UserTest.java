package org.uab.user;


import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uab.shop.model.Cart;
import org.uab.user.model.Role;
import org.uab.user.model.User;

@QuarkusTest
public class UserTest {

    User user;

    @BeforeEach
    void setup() {
        User mockUser = new User();
        Cart cart = new Cart();
        cart.setId(2L);
        mockUser.setId(1L);
        mockUser.setUsername("test");
        mockUser.setPassword(BcryptUtil.bcryptHash("test"));
        mockUser.setRole(Role.BASIC);
        mockUser.setCart(cart);
        mockUser.setName("name");
        mockUser.setEmail("test@test.com");
        this.user = mockUser;
    }

    @Test
    public void shouldHaveInitialValues() throws Throwable{
        Assertions.assertEquals(1L,user.getId());
        Assertions.assertEquals("test", user.getUsername());
        Assertions.assertTrue(BcryptUtil.matches("test",user.getPassword()));
        Assertions.assertEquals(Role.BASIC.toString(), user.getRole());
        Assertions.assertEquals(2L,user.getCart().getId());
        Assertions.assertEquals("name", user.getName());
        Assertions.assertEquals("test@test.com", user.getEmail());
    }

    @Test
    public void shouldConstructBasicUser() throws  Throwable{
        User testUser = new User("test","test", Role.BASIC, user.getCart());
        Assertions.assertEquals("test", testUser.getUsername());
        Assertions.assertTrue(BcryptUtil.matches("test",testUser.getPassword()));
        Assertions.assertEquals(Role.BASIC.toString(), testUser.getRole());
        Assertions.assertEquals(2L,testUser.getCart().getId());
    }

    @Test
    public void shouldConstructAnyUser() throws  Throwable{
        User testUser = new User("test","test", Role.MANAGER);
        Assertions.assertEquals("test", testUser.getUsername());
        Assertions.assertTrue(BcryptUtil.matches("test",testUser.getPassword()));
        Assertions.assertEquals(Role.MANAGER.toString(), testUser.getRole());
    }
}
