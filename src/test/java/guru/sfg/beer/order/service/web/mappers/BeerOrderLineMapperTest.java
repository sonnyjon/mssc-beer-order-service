package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.cases.TestBeerOrderLine;
import guru.sfg.beer.order.service.cases.TestBeerOrderLineDto;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.brewery.model.BeerOrderLineDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Sonny on 9/24/2022.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DateMapper.class, BeerOrderLineMapperImpl_.class})
class BeerOrderLineMapperTest
{
    @Autowired
    BeerOrderLineMapper beerOrderLineMapper;

    @Nested
    @DisplayName("toBeerOrderLineDto() Method")
    class ToBeerOrderLineDto
    {
        @Test
        @DisplayName("should convert BeerOrderLine and return BeerOrderLineDto")
        void givenBeerOrderLine_whenToBeerOrderLineDto_thenConvertToDto()
        {
            // given
            final BeerOrderLine expected = TestBeerOrderLine.getBeerOrderLine();

            // when
            BeerOrderLineDto actual = beerOrderLineMapper.toBeerOrderLineDto( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.getBeerId(), actual.getBeerId() );
            assertEquals( expected.getUpc(), actual.getUpc() );
            assertEquals( expected.getOrderQuantity(), actual.getOrderQuantity() );
            assertEquals( expected.getQuantityAllocated(), actual.getQuantityAllocated() );

            // TODO: Need tests for Beer attributes in BeerOrderLineDto
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenToBeerOrderLineDto_thenReturnNull()
        {
            final BeerOrderLine expected = null;                                            // given
            BeerOrderLineDto actual = beerOrderLineMapper.toBeerOrderLineDto( expected );   // when

            assertNull( actual );                                                           // then
        }
    }

    @Nested
    @DisplayName("toBeerOrderLine() Method")
    class ToBeerOrderLine
    {
        @Test
        @DisplayName("should convert BeerOrderLineDto and return BeerOrderLine")
        void givenBeerOrderLineDto_whenToBeerOrderLine_thenConvertToEntity()
        {
            // given
            final BeerOrderLineDto expected = TestBeerOrderLineDto.getBeerOrderLineDto();

            // when
            BeerOrderLine actual = beerOrderLineMapper.toBeerOrderLine( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.getBeerId(), actual.getBeerId() );
            assertEquals( expected.getUpc(), actual.getUpc() );
            assertEquals( expected.getOrderQuantity(), actual.getOrderQuantity() );
            assertEquals( expected.getQuantityAllocated(), actual.getQuantityAllocated() );
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenToBeerOrderLine_thenReturnNull()
        {
            final BeerOrderLineDto expected = null;                                     // given
            BeerOrderLine actual = beerOrderLineMapper.toBeerOrderLine( expected );     // when

            assertNull( actual );                                                       // then
        }
    }
}