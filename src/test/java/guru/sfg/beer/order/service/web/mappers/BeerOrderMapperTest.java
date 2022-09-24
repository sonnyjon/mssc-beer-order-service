package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.cases.TestBeerOrder;
import guru.sfg.beer.order.service.cases.TestBeerOrderDto;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.brewery.model.BeerOrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Sonny on 9/18/2022.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DateMapper.class, BeerOrderMapperImpl.class})
class BeerOrderMapperTest
{
    @Autowired
    BeerOrderMapper beerOrderMapper;

    @Nested
    @DisplayName("toBeerOrderDto() Method")
    class toDto
    {
        @Test
        @DisplayName("should convert BeerOrder and return as BeerOrderDto")
        void givenBeerOrder_whenToBeerOrderDto_thenConvertToDto()
        {
            // given
            final BeerOrder expected = TestBeerOrder.getBeerOrder();

            // when
            BeerOrderDto actual = beerOrderMapper.toBeerOrderDto( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.getCustomerRef(), actual.getCustomerRef() );
            assertEquals( expected.getCustomer().getId(), actual.getCustomerId() );
            assertEquals( expected.getBeerOrderLines().size(), actual.getBeerOrderLines().size() );
            assertEquals( expected.getOrderStatus().name(), actual.getOrderStatus() );
            assertEquals( expected.getOrderStatusCallbackUrl(), actual.getOrderStatusCallbackUrl() );
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenToBeerOrderDto_thenNull()
        {
            final BeerOrder expected = null;                                    // given
            BeerOrderDto actual = beerOrderMapper.toBeerOrderDto( expected );   // when

            assertNull( actual );                                               // then
        }
    }

    @Nested
    @DisplayName("toBeerOrder() Method")
    class toEntity
    {
        @Test
        @DisplayName("should convert BeerOrderDto and return as BeerOrder")
        void givenBeerOrderDto_whenToBeerOrder_thenConvertToEntity()
        {
            // given
            final BeerOrderDto expected = TestBeerOrderDto.getBeerOrderDto();

            // when
            BeerOrder actual = beerOrderMapper.toBeerOrder( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.getCustomerRef(), actual.getCustomerRef() );
            // TODO: create BeerOrderMapperDecorator to connect Customer object correctly.
//            assertEquals( expected.getCustomerId(), actual.getCustomer().getId() );
            assertEquals( expected.getBeerOrderLines().size(), actual.getBeerOrderLines().size() );
            assertEquals( expected.getOrderStatus(), actual.getOrderStatus().name() );
            assertEquals( expected.getOrderStatusCallbackUrl(), actual.getOrderStatusCallbackUrl() );
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenToBeerOrder_thenNull()
        {
            final BeerOrderDto expected = null;                             // given
            BeerOrder actual = beerOrderMapper.toBeerOrder( expected );     // when

            assertNull( actual );                                           // then
        }
    }
}