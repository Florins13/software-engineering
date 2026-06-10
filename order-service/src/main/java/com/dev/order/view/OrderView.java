package com.dev.order.view;

import com.dev.order.controller.dto.OrderDTO;
import com.dev.order.model.ShippingItem;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;

import java.util.List;

public class OrderView {

    public static final HtmlView<List<OrderDTO>> orderHistoryView = HtmlFlow.view(content -> {
        content
                .div()
                .attrStyle("padding-left: 10px")
                .h3().text("Order history:").__()
                .table().attrStyle("width: 100%")
                .tr()
                .th().text("Transaction").__()
                .th().text("Order state").__()
                .th().text("Acquire Type").__()
                .th().text("Items:").__()
                .th().text("Total").__()
                .th().text("Address").__()
                .th().text("User").__()
                .__()
                .<List<OrderDTO>>dynamic((table, orders) -> {
                    for (OrderDTO order : orders) {
                        table.tr()
                                .td().text(order.transaction).__()
                                .td().text(String.valueOf(order.orderState)).__()
                                .td().text(String.valueOf(order.acquireType)).__()
                                .td().attrStyle("display: flex;flex-direction: column;")
                                .of(tdCol -> {
                                    for (ShippingItem item : order.shippingItems) {
                                        tdCol.span().text(item.getQuantity() + "x " + item.getBikeModel()).__();
                                    }
                                })
                                .__()
                                .td().text(String.valueOf(order.totalPrice)).__()
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
