package org.uab.shop.controller;

public class ResourceCartController {
    public final static String cartItemIncreased =
            "        <div style=\"display: flex;gap: 20px;flex-wrap: wrap;justify-content: space-evenly;\">\n" +
            "            <div class=\"bike__box\">\n" +
            "                <form action=\"/cart/deleteItem\" method=\"post\" style=\"align-self: end;\">\n" +
            "                    <button name=\"id\" type=\"submit\" value=\"1\" style=\"width: 25px\">X</button>\n" +
            "                </form>\n" +
            "                <h4>Model: CITY STAR ST 500</h4>\n" +
            "\n" +
            "                <img src=\"/bike_one.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                <span>Stock: 4</span>\n" +
            "                <span>Details: red, medium</span>\n" +
            "                <span>Electric: false</span>\n" +
            "                <span>Price: 24.99€</span>\n" +
            "                <div style=\"display: flex;align-items: center;gap: 20px;\">\n" +
            "                    <form action=\"/cart/updateQuantity/decrease\" method=\"post\">\n" +
            "                        <button name=\"id\" type=\"submit\" value=\"1\" type=\"submit\" style=\"width: 85px\">Decrease -</button>\n" +
            "                    </form>\n" +
            "                    <p>2</p>\n" +
            "                    <form action=\"/cart/updateQuantity/increase\" method=\"post\">\n" +
            "                        <button name=\"id\" type=\"submit\" value=\"1\" style=\"width: 85px\">Increase +</button>\n" +
            "                    </form>\n" +
            "                </div>\n" +
            "\n" +
            "            </div>\n" +
            "        </div>";

    public final static String cartItemDecreased =
            "        <div style=\"display: flex;gap: 20px;flex-wrap: wrap;justify-content: space-evenly;\">\n" +
            "            <div class=\"bike__box\">\n" +
            "                <form action=\"/cart/deleteItem\" method=\"post\" style=\"align-self: end;\">\n" +
            "                    <button name=\"id\" type=\"submit\" value=\"1\" style=\"width: 25px\">X</button>\n" +
            "                </form>\n" +
            "                <h4>Model: CITY STAR ST 500</h4>\n" +
            "\n" +
            "                <img src=\"/bike_one.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                <span>Stock: 4</span>\n" +
            "                <span>Details: red, medium</span>\n" +
            "                <span>Electric: false</span>\n" +
            "                <span>Price: 24.99€</span>\n" +
            "                <div style=\"display: flex;align-items: center;gap: 20px;\">\n" +
            "                    <form action=\"/cart/updateQuantity/decrease\" method=\"post\">\n" +
            "                        <button name=\"id\" type=\"submit\" value=\"1\" type=\"submit\" style=\"width: 85px\">Decrease -</button>\n" +
            "                    </form>\n" +
            "                    <p>1</p>\n" +
            "                    <form action=\"/cart/updateQuantity/increase\" method=\"post\">\n" +
            "                        <button name=\"id\" type=\"submit\" value=\"1\" style=\"width: 85px\">Increase +</button>\n" +
            "                    </form>\n" +
            "                </div>\n" +
            "\n" +
            "            </div>\n" +
            "        </div>";

    public final static String cartItemDelete =
            "<div style=\"display: flex;flex-direction: row;justify-content: end;gap: 20px;padding-right: 50px;\">\n" +
            "    <h3>Total: 0.00 €</h3>\n" +
            "    <button  disabled  onclick=\"goToCheckout()\" style=\"height: 30px;align-self: center;\">Checkout</button>\n" +
            "</div>";
}
