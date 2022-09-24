package guru.sfg.beer.order.service.cases;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Sonny on 9/23/2022.
 */
public class TestBeerOrderLine
{
    public static BeerOrderLine getBeerOrderLine()
    {
        return getBeerOrderLine(
                    UUID.randomUUID(),
                    null,
                    TestConstants.BEER_1_UPC,
                    25,
                    12
                );
    }

    public static BeerOrderLine getBeerOrderLine(UUID beerId,
                                                 BeerOrder beerOrder,
                                                 String upc,
                                                 Integer orderQuantity,
                                                 Integer quantityAllocated)
    {
        return BeerOrderLine.builder()
                .beerId( beerId )
                .beerOrder( beerOrder )
                .upc( upc )
                .orderQuantity( orderQuantity )
                .quantityAllocated( quantityAllocated )
                .build();
    }

    public static void connectBeerOrder(BeerOrderLine beerOrderLine, BeerOrder beerOrder)
    {
        if (beerOrder.getBeerOrderLines() == null) beerOrder.setBeerOrderLines( new HashSet<>() );

        beerOrderLine.setBeerOrder( beerOrder );
        beerOrder.getBeerOrderLines().add( beerOrderLine );
    }
}
