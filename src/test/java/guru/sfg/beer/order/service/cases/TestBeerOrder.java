package guru.sfg.beer.order.service.cases;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.OrderStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Sonny on 9/18/2022.
 */
public class TestBeerOrder
{
    public static BeerOrder getBeerOrder()
    {
        Customer customer = TestCustomer.getCustomer();
        customer.setId( UUID.randomUUID() );

        return getBeerOrder(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                customer,
                new HashSet<>(),
                OrderStatus.NEW,
                "callback-url"
        );
    }

    public static BeerOrder getBeerOrder(UUID id,
                                         String customerRef,
                                         Customer customer,
                                         Set<BeerOrderLine> beerOrderLines,
                                         OrderStatus orderStatus,
                                         String orderStatusCallbackUrl)
    {
        return BeerOrder.builder()
                .id( id )
                .customerRef( customerRef )
                .customer( customer )
                .beerOrderLines( beerOrderLines )
                .orderStatus( orderStatus )
                .orderStatusCallbackUrl( orderStatusCallbackUrl )
                .build();
    }
}
