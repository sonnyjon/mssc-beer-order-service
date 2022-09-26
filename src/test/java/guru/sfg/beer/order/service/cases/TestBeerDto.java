package guru.sfg.beer.order.service.cases;

import guru.sfg.brewery.model.BeerDto;

import java.math.BigDecimal;

/**
 * Created by Sonny on 9/25/2022.
 */
public class TestBeerDto
{
    public static BeerDto getBeerDto()
    {
        return getBeerDto(
                "Test Beer",
                "IPA",
                TestConstants.BEER_1_UPC,
                12,
                new BigDecimal( "19.95" )
        );
    }

    public static BeerDto getBeerDto(String name,
                                     String style,
                                     String upc,
                                     Integer quantityOnHand,
                                     BigDecimal price)
    {
        return BeerDto.builder()
                .name( name )
                .style( style )
                .upc( upc )
                .quantityOnHand( quantityOnHand )
                .price( price )
                .build();
    }
}
