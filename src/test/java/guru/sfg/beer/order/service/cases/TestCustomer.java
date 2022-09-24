package guru.sfg.beer.order.service.cases;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.Customer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Sonny on 9/18/2022.
 */
public class TestCustomer
{
    public static Customer getCustomer()
    {
        return getCustomer("John Thompson", UUID.randomUUID(), new HashSet<>());
    }

    public static Customer getCustomer(String customerName, UUID apiKey, Set<BeerOrder> beerOrders)
    {
        return Customer.builder()
                .customerName( customerName )
                .apiKey( apiKey )
                .beerOrders( beerOrders )
                .build();
    }
}
