package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.cases.TestCustomer;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 9/25/2022.
 */
@ExtendWith(MockitoExtension.class)
class TastingRoomServiceTest
{
    @Mock
    CustomerRepository customerRepository;

    @Mock
    BeerOrderService beerOrderService;

    @InjectMocks
    TastingRoomService tastingRoomService;

    @Test
    @DisplayName("should call BeerOrderService to place order")
    void whenPlaceTastingRoomOrder_thenPlaceOrder_viaBeerOrderService()
    {
        // given
        final Customer customer = TestCustomer.getCustomer();
        final List<Customer> customerList = List.of( customer );

        // when
        when(customerRepository.findAllByCustomerNameLike( anyString() )).thenReturn( customerList );
        when(beerOrderService.placeOrder(any( UUID.class ), any( BeerOrderDto.class ))).thenReturn( null );

        tastingRoomService.placeTastingRoomOrder();

        // then
        verify(customerRepository, times(1)).findAllByCustomerNameLike( anyString() );
        verify(beerOrderService, times(1)).placeOrder(any( UUID.class ), any( BeerOrderDto.class ));
    }

    @Test
    @DisplayName("should log error when no customers")
    void givenNoCustomer_whenPlaceTastingRoomOrder_thenLogError()
    {
        // given
        final List<Customer> customerList = new ArrayList<>();

        // when
        when(customerRepository.findAllByCustomerNameLike( anyString() )).thenReturn( customerList );

        tastingRoomService.placeTastingRoomOrder();

        // then
        verify(customerRepository, times(1)).findAllByCustomerNameLike( anyString() );
        verify(beerOrderService, times(0)).placeOrder(any( UUID.class ), any( BeerOrderDto.class ));
    }

    @Test
    @DisplayName("should log error when more than one customer")
    void givenMultipleCustomers_whenPlaceTastingRoomOrder_thenLogError()
    {
        // given
        final Customer customer1 = TestCustomer.getCustomer();
        final Customer customer2 = TestCustomer.getCustomer();
        final List<Customer> customerList = List.of( customer1, customer2 );

        // when
        when(customerRepository.findAllByCustomerNameLike( anyString() )).thenReturn( customerList );

        tastingRoomService.placeTastingRoomOrder();

        // then
        verify(customerRepository, times(1)).findAllByCustomerNameLike( anyString() );
        verify(beerOrderService, times(0)).placeOrder(any( UUID.class ), any( BeerOrderDto.class ));
    }
}