package guru.sfg.beer.order.service.cases;

import guru.sfg.beer.order.service.web.dto.BeerOrderDto;
import guru.sfg.beer.order.service.web.dto.BeerOrderLineDto;
import guru.sfg.beer.order.service.web.model.OrderStatus;

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
                    OrderStatus.NEW,
                    "callback-url"
        );
    }

    public static BeerOrderDto getBeerOrderDto(UUID customerId,
                                               String customerRef,
                                               List<BeerOrderLineDto> beerOrderLines,
                                               OrderStatus orderStatus,
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
