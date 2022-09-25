package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.cases.TestBeerOrder;
import guru.sfg.beer.order.service.cases.TestBeerOrderDto;
import guru.sfg.beer.order.service.cases.TestCustomer;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.OrderStatus;
import guru.sfg.beer.order.service.exceptions.NotFoundException;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 9/24/2022.
 */
@ExtendWith(MockitoExtension.class)
class BeerOrderServiceImplTest
{
    @Mock
    BeerOrderRepository beerOrderRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    BeerOrderMapper beerOrderMapper;

    @Mock
    ApplicationEventPublisher publisher;

    BeerOrderService beerOrderService;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        this.mocks = MockitoAnnotations.openMocks( this );

        this.beerOrderService = new BeerOrderServiceImpl(
                beerOrderRepository,
                customerRepository,
                beerOrderMapper,
                publisher
        );
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Nested
    @DisplayName("listOrders() Method")
    class ListOrdersTest
    {
        @Test
        @DisplayName("should return the customer's list of beer orders as a BeerOrderPagedList")
        void givenCustomerId_andPageable_whenListOrders_thenReturnBeerOrderPagedList()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();
            final PageRequest pageRequest = PageRequest.of( 1, 1 );

            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            beerOrder.setCustomer( customer );
            beerOrder.setCustomerRef( customerId.toString() );

            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();
            beerOrderDto.setCustomerId( customerId );
            beerOrderDto.setCustomerRef( customerId.toString() );

            final Page<BeerOrder> beerOrderPage = new PageImpl<>(List.of( beerOrder ), pageRequest, 1);

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findAllByCustomer(any( Customer.class ), any( PageRequest.class )))
                .thenReturn( beerOrderPage );

            when(beerOrderMapper.toBeerOrderDto(any( BeerOrder.class ))).thenReturn( beerOrderDto );

            BeerOrderPagedList actual = beerOrderService.listOrders( customerId, pageRequest );

            // then
            assertNotNull( actual );
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenListOrders_thenNotFoundException()
        {
            // given
            final UUID badCustomerId = UUID.randomUUID();
            final PageRequest pageRequest = PageRequest.of( 2, 10 );

            // when
            when(customerRepository.findById( any(UUID.class) )).thenThrow( NotFoundException.class );
            Executable executable = () -> beerOrderService.listOrders( badCustomerId, pageRequest );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }
    }

    @Nested
    @DisplayName("placeOrder() Method")
    class PlaceOrderTest
    {
        @Test
        @DisplayName("should convert and save the BeerOrder. Then publishes BeerOrder.")
        void givenCustomerId_andBeerOrderDto_whenPlaceOrder_thenSaveBeerOrder_andPublish()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();

            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();
            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            beerOrder.setCustomer( customer );
            beerOrderDto.setCustomerId( customerId );

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderMapper.toBeerOrder(any( BeerOrderDto.class ))).thenReturn( beerOrder );
            when(beerOrderRepository.saveAndFlush(any( BeerOrder.class ))).thenReturn( beerOrder );
            when(beerOrderMapper.toBeerOrderDto(any( BeerOrder.class ))).thenReturn( beerOrderDto );

            BeerOrderDto actual = beerOrderService.placeOrder( customerId, beerOrderDto );

            // then
            assertNotNull( actual );
            assertEquals( customerId, actual.getCustomerId() );
            assertEquals( beerOrder.getOrderStatus().name(), actual.getOrderStatus());
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenPlaceOrder_thenNotFoundException()
        {
            // given
            final UUID badCustomerId = UUID.randomUUID();
            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();

            // when
            when(customerRepository.findById( any(UUID.class) )).thenThrow( NotFoundException.class );
            Executable executable = () -> beerOrderService.placeOrder( badCustomerId, beerOrderDto );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }
    }

    @Nested
    @DisplayName("getOrderById() Method")
    class GetOrderByIdTest
    {
        @Test
        @DisplayName("should return the BeerOrderDto associated with the customer and order IDs")
        void givenCustomerId_andOrderId_whenGetOrderById_thenReturnBeerOrderDto()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();

            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            final UUID orderId = beerOrder.getId();
            final BeerOrderDto expectedDto = TestBeerOrderDto.getBeerOrderDto();

            beerOrder.setCustomer( customer );
            beerOrder.setCustomerRef( customerId.toString() );
            expectedDto.setCustomerId( customerId );

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenReturn(Optional.of( beerOrder ));
            when(beerOrderMapper.toBeerOrderDto( beerOrder )).thenReturn( expectedDto );

            BeerOrderDto actualDto = beerOrderService.getOrderById( customerId, orderId );

            // then
            assertNotNull( actualDto );
            assertEquals( expectedDto.getCustomerId(), actualDto.getCustomerId() );
            assertEquals( expectedDto.getId(), actualDto.getId() );
            assertEquals( expectedDto.getOrderStatus(), actualDto.getOrderStatus() );
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenGetOrderById_thenNotFoundException()
        {
            // given
            final UUID badCustomerId = UUID.randomUUID();
            final UUID orderId = UUID.randomUUID();

            // when
            when(customerRepository.findById( any(UUID.class) )).thenThrow( NotFoundException.class );
            Executable executable = () -> beerOrderService.getOrderById( badCustomerId, orderId );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad BeerOrder ID")
        void givenBadOrderId_whenGetOrderById_thenNotFoundException()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();
            final UUID orderId = UUID.randomUUID();

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenThrow( NotFoundException.class );

            Executable executable = () -> beerOrderService.getOrderById( customerId, orderId );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }

        @Test
        @DisplayName("should throw IllegalStateException: Customer ID doesn't match one from BeerOrder")
        void givenNonMatching_customerAndOrderIds_whenGetOrderById_thenIllegalStateException()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();
            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            final UUID orderId = beerOrder.getId();

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenReturn(Optional.of( beerOrder ));

            Executable executable = () -> beerOrderService.getOrderById( customerId, orderId );

            // then
            assertThrows( IllegalStateException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
            verify(beerOrderRepository, times(1)).findById(any( UUID.class ));
        }
    }

    @Nested
    @DisplayName("pickupOrder() Method")
    class PickupOrderTest
    {

        @Test
        @DisplayName("should change the order status of BeerOrder to PICKED_UP and save BeerOrder")
        void givenCustomerId_andOrderId_whenPickupOrder_thenChangeOrderStatusToPickedUp_andSave()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();

            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            final UUID orderId = beerOrder.getId();

            beerOrder.setCustomer( customer );
            beerOrder.setCustomerRef( customerId.toString() );
            beerOrder.setOrderStatus( OrderStatus.PICKED_UP );

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenReturn(Optional.of( beerOrder ));
            when(beerOrderRepository.save(any( BeerOrder.class ))).thenReturn( beerOrder );

            beerOrderService.pickupOrder( customerId, orderId );

            // then
            verify(customerRepository, times(1)).findById(any( UUID.class ));
            verify(beerOrderRepository, times(1)).findById(any( UUID.class ));
            verify(beerOrderRepository, times(1)).save(any( BeerOrder.class ));
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenPickupOrder_thenNotFoundException()
        {
            // given
            final UUID badCustomerId = UUID.randomUUID();
            final UUID orderId = UUID.randomUUID();

            // when
            when(customerRepository.findById( any(UUID.class) )).thenThrow( NotFoundException.class );
            Executable executable = () -> beerOrderService.pickupOrder( badCustomerId, orderId );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad BeerOrder ID")
        void givenBadOrderId_whenPickupOrder_thenNotFoundException()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();
            final UUID orderId = UUID.randomUUID();

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenThrow( NotFoundException.class );

            Executable executable = () -> beerOrderService.pickupOrder( customerId, orderId );

            // then
            assertThrows( NotFoundException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
        }

        @Test
        @DisplayName("should throw IllegalStateException: Customer ID doesn't match one from BeerOrder")
        void givenNonMatching_customerAndOrderIds_whenPickupOrder_thenIllegalStateException()
        {
            // given
            final Customer customer = TestCustomer.getCustomer();
            final UUID customerId = customer.getId();
            final BeerOrder beerOrder = TestBeerOrder.getBeerOrder();
            final UUID orderId = beerOrder.getId();

            // when
            when(customerRepository.findById(any( UUID.class ))).thenReturn(Optional.of( customer ));
            when(beerOrderRepository.findById(any( UUID.class ))).thenReturn(Optional.of( beerOrder ));

            Executable executable = () -> beerOrderService.pickupOrder( customerId, orderId );

            // then
            assertThrows( IllegalStateException.class, executable );
            verify(customerRepository, times(1)).findById(any( UUID.class ));
            verify(beerOrderRepository, times(1)).findById(any( UUID.class ));
        }
    }
}