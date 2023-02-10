package org.uab.bike;

public class ResourceBikeController {
    final static String headerNoLogin =
            "        <nav class=\"nav-style\" style=\"display: flex;align-items: center;\">\n" +
            "                <div>\n" +
            "                    <img onclick=\"logIn()\" src=\"/login.png\">\n" +
            "                </div>\n" +
            "                <div>\n" +
            "                    <img src=\"/add-user.png\">\n" +
            "                </div>\n" +
            "        </nav>\n";

    final static String headerLoggedIn = "" +
            "<!-- HEADER-NAVBAR -->\n" +
            "    <header class=\"header_bar\">\n" +
            "        <div>\n" +
            "            <img onclick=\"goToBikes()\" src=\"/bicycle.png\" style=\"cursor: pointer;height:70px;width:70px;\">\n" +
            "        </div>\n" +
            "        <div onclick=\"goToBikes()\"><h1>UABikes</h1></div>\n" +
            "        <nav class=\"nav-style\" style=\"display: flex;align-items: center;\">\n" +
            "                <div>\n" +
            "                    <img onclick=\"goToOrders()\" src=\"/orders.png\">\n" +
            "                </div>\n" +
            "                <div>\n" +
            "                    <img onclick=\"goToCart()\" src=\"/shopping-cart.png\">\n" +
            "                </div>\n" +
            "                <div>\n" +
            "                    <img onclick=\"logOut()\" src=\"/logout.png\">\n" +
            "                </div>\n" +
            "        </nav>\n" +
            "    </header>";

    final static String footer = "<!-- FOOTER-INFO -->\n" +
            "<footer class=\"footer\">\n" +
            "<h3>UAB</h3>\n" +
            "</footer>\n" +
            "</body>\n" +
            "</html>";
    final static String bikesNoLogin = "    <!-- MAIN-BIKES -->\n" +
            "    <main style=\"padding: 20px;\">\n" +
            "        <div style=\"margin-bottom:10px\">\n" +
            "            <label for=\"search\">Search</label>\n" +
            "            <input type=\"text\" id=\"search\" name=\"search\">\n" +
            "        </div>\n" +
            "        <div style=\"display: flex;gap: 20px;flex-wrap: wrap;justify-content: space-evenly;\">\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: CITY STAR ST 500</h4>\n" +
            "                    <img src=\"/bike_one.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 4</span>\n" +
            "                    <span>Details: red, medium</span>\n" +
            "                    <span>Electric: false</span>\n" +
            "                    <span>Price: 24.99€</span>\n" +
            "                </div>\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: SUPER STAR FS 400</h4>\n" +
            "                    <img src=\"/bike_two.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 5</span>\n" +
            "                    <span>Details: big, black</span>\n" +
            "                    <span>Electric: true</span>\n" +
            "                    <span>Price: 50.45€</span>\n" +
            "                </div>\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: ROCK STAR FS 400</h4>\n" +
            "                    <img src=\"/bike_three.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 12</span>\n" +
            "                    <span>Details: red, medium</span>\n" +
            "                    <span>Electric: false</span>\n" +
            "                    <span>Price: 115.50€</span>\n" +
            "                </div>\n" +
            "\n" +
            "        </div>\n" +
            "    </main>\n";

    final static String bikesLoggedIn = "    <!-- MAIN-BIKES -->\n" +
            "    <main style=\"padding: 20px;\">\n" +
            "        <div style=\"margin-bottom:10px\">\n" +
            "            <label for=\"search\">Search</label>\n" +
            "            <input type=\"text\" id=\"search\" name=\"search\">\n" +
            "        </div>\n" +
            "        <div style=\"display: flex;gap: 20px;flex-wrap: wrap;justify-content: space-evenly;\">\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: CITY STAR ST 500</h4>\n" +
            "                    <img src=\"/bike_one.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 4</span>\n" +
            "                    <span>Details: red, medium</span>\n" +
            "                    <span>Electric: false</span>\n" +
            "                    <span>Price: 24.99€</span>\n" +
            "                        <form action=\"/cart/add/1\" method=\"post\">\n" +
            "                        <button  type=\"submit\">Add to cart</button>\n" +
            "                        </form>\n" +
            "                </div>\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: SUPER STAR FS 400</h4>\n" +
            "                    <img src=\"/bike_two.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 5</span>\n" +
            "                    <span>Details: big, black</span>\n" +
            "                    <span>Electric: true</span>\n" +
            "                    <span>Price: 50.45€</span>\n" +
            "                        <form action=\"/cart/add/2\" method=\"post\">\n" +
            "                        <button  type=\"submit\">Add to cart</button>\n" +
            "                        </form>\n" +
            "                </div>\n" +
            "                <div class=\"bike__box\">\n" +
            "                    <h4>Model: ROCK STAR FS 400</h4>\n" +
            "                    <img src=\"/bike_three.jpg\" height=\"70px\" width=\"70px\">\n" +
            "                    <span>Stock: 12</span>\n" +
            "                    <span>Details: red, medium</span>\n" +
            "                    <span>Electric: false</span>\n" +
            "                    <span>Price: 115.50€</span>\n" +
            "                        <form action=\"/cart/add/3\" method=\"post\">\n" +
            "                        <button  type=\"submit\">Add to cart</button>\n" +
            "                        </form>\n" +
            "                </div>\n" +
            "\n" +
            "        </div>\n" +
            "    </main>";

}
