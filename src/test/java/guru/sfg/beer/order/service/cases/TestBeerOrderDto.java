package guru.sfg.beer.order.service.cases;

import guru.sfg.beer.order.service.domain.OrderStatus;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sonny on 9/24/2022.
 */
public class TestBeerOrderDto
{
    public static BeerOrderDto getBeerOrderDto()
    {
        return getBeerOrderDto(
                    UUID.randomUUID(),
                    UUID.randomUUID().toString(),
                    new ArrayList<>(),
                    OrderStatus.NEW.name(),
                    "callback-url"
        );
    }

    public static BeerOrderDto getBeerOrderDto(UUID customerId,
                                               String customerRef,
                                               List<BeerOrderLineDto> beerOrderLines,
                                               String orderStatus,
                                               String orderStatusCallbackUrl)
    {
        return BeerOrderDto.builder()
                .customerId( customerId )
                .customerRef( customerRef )
                .beerOrderLines( beerOrderLines )
                .orderStatus( orderStatus )
                .orderStatusCallbackUrl( orderStatusCallbackUrl )
                .build();
    }

}
