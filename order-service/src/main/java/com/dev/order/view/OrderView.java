package com.dev.order.view;

import com.dev.order.controller.dto.OrderDTO;
import com.dev.order.model.ShippingItem;
import com.dev.cart.client.CartDTO;
import com.dev.cart.client.CartItemDTO;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import java.math.BigDecimal;
import java.util.List;

public class OrderView {

    public record CheckoutData(CartDTO cart, List<OrderDTO> orders) {}

//    public static final HtmlView<List<OrderDTO>> orderHistoryView = HtmlFlow.view(content -> {
//        content
//                .div()
//                .attrStyle("padding-left: 10px")
//                .h3().text("Order history:").__()
//                .table().attrStyle("width: 100%; border-spacing: 10px;")
//                .tr()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Transaction").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Order state").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Acquire Type").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Items:").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Total").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Address").__()
//                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("User").__()
//                .__()
//                .<List<OrderDTO>>dynamic((table, orders) -> {
//                    for (OrderDTO order : orders) {
//                        table.tr()
//                                .td().text(order.transaction).__()
//                                .td().text(String.valueOf(order.orderState)).__()
//                                .td().text(String.valueOf(order.acquireType)).__()
//                                .td().attrStyle("display: flex; flex-direction: column;")
//                                .of(tdCol -> {
//                                    for (ShippingItem item : order.shippingItems) {
//                                        tdCol.span().text(item.getQuantity() + "x " + item.getBikeModel()).__();
//                                    }
//                                })
//                                .__()
//                                .td().text(order.totalPrice + " €").__()
//                                .td()
//                                .span().text(
//                                        order.shippingAddress.getFullName() + ", "
//                                                + order.shippingAddress.getAddress() + ", "
//                                                + order.shippingAddress.getTelephone() + ". "
//                                                + order.shippingAddress.getZipCode()
//                                ).__()
//                                .__()
//                                .td().text(order.userId).__()
//                                .__();
//                    }
//                });
//    });

    public static final HtmlView<CheckoutData> checkoutView = HtmlFlow.view(content -> {
        content
                .div()
                    .h1().attrStyle("margin-left: 5px").text("Checkout").__()
                    .div().attrStyle("padding: 20px; display: flex; justify-content: space-around;")
                        .div()
                            .form().attrMethod(EnumMethodType.POST).attrAction("/order/finalise")
                                .attrStyle("display: flex; flex-direction: column; align-items: baseline; gap: 5px;")
                                .label().attrFor("fullName").text("Full name").__()
                                .input().attrType(EnumTypeInputType.TEXT).attrId("fullName").attrName("fullName").attrRequired(true).__()
                                .label().attrFor("address").text("Address").__()
                                .input().attrType(EnumTypeInputType.TEXT).attrId("address").attrName("address").attrRequired(true).__()
                                .label().attrFor("telephone").text("Telephone").__()
                                .input().attrType(EnumTypeInputType.NUMBER).attrId("telephone").attrName("telephone").attrRequired(true).__()
                                .label().attrFor("zipCode").text("Zipcode").__()
                                .input().attrType(EnumTypeInputType.TEXT).attrId("zipCode").attrName("zipCode").attrRequired(true).__()
                                .div()
                                    .label().attrFor("buy").text("Buy").__()
                                    .input().attrType(EnumTypeInputType.RADIO).attrId("buy").attrName("acquireType").attrValue("buy").attrChecked(true).__()
                                    .label().attrFor("rent").text("Rent for 3 days").__()
                                    .input().attrType(EnumTypeInputType.RADIO).attrId("rent").attrName("acquireType").attrValue("rent").__()
                                .__()
                                .button().attrId("finaliseOrder").attrStyle("align-self: end;").text("Finalise").__()
                            .__()
                        .__()
                        .div()
                            .table().addAttr("style", "width: 400px; border-spacing: 10px;")
                                .tr()
                                    .th().addAttr("style", "text-align: left; border-bottom: 1px solid;").text("Bike").__()
                                    .th().addAttr("style", "text-align: left; border-bottom: 1px solid;").text("Quantity").__()
                                    .th().addAttr("style", "text-align: left; border-bottom: 1px solid;").text("Price").__()
                                .__()
                                .<CheckoutData>dynamic((table, data) -> {
                                    if (data.cart() != null && data.cart().cartItems != null) {
                                        BigDecimal total = BigDecimal.ZERO;
                                        for (CartItemDTO item : data.cart().cartItems) {
                                            table.tr()
                                                    .td().text(item.model).__()
                                                    .td().text(String.valueOf(item.quantity)).__()
                                                    .td().text(item.price + " €").__()
                                            .__();
                                            total = total.add(item.price.multiply(BigDecimal.valueOf(item.quantity)));
                                        }
                                        table.tr()
                                                .td().__()
                                                .td().attrStyle("text-align: end; font-weight: bold;").text("Total:").__()
                                                .td().attrStyle("font-weight: bold;").text(total + " €").__()
                                        .__();
                                    }
                                })
                            .__()
                        .__()
                    .__()
                    // Order history built inline
                    .div().attrStyle("padding-left: 10px")
                        .h3().text("Order history:").__()
                        .table().attrStyle("width: 100%; border-spacing: 10px;")
                            .tr()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Transaction").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Order state").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Acquire Type").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Items:").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Total").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("Address").__()
                                .th().attrStyle("text-align: left; border-bottom: 1px solid;").text("User").__()
                            .__()
                            .<CheckoutData>dynamic((table, data) -> {
                                for (OrderDTO order : data.orders()) {
                                    table.tr()
                                            .td().text(order.transaction).__()
                                            .td().text(String.valueOf(order.orderState)).__()
                                            .td().text(String.valueOf(order.acquireType)).__()
                                            .td().attrStyle("display: flex; flex-direction: column;")
                                            .of(tdCol -> {
                                                for (ShippingItem item : order.shippingItems) {
                                                    tdCol.span().text(item.getQuantity() + "x " + item.getBikeModel()).__();
                                                }
                                            })
                                            .__()
                                            .td().text(order.totalPrice + " €").__()
                                            .td()
                                            .span().text(
                                                    order.shippingAddress.getFullName() + ", "
                                                            + order.shippingAddress.getAddress() + ", "
                                                            + order.shippingAddress.getTelephone() + ". "
                                                            + order.shippingAddress.getZipCode()
                                            ).__()
                                            .__()
                                            .td().text(order.userId).__()
                                            .__();
                                }
                            });
    });
}
