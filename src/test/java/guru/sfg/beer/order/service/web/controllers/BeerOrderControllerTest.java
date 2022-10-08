package guru.sfg.beer.order.service.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.beer.order.service.cases.TestBeerOrderDto;
import guru.sfg.beer.order.service.cases.TestBeerOrderLineDto;
import guru.sfg.beer.order.service.cases.TestCustomer;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.OrderStatus;
import guru.sfg.beer.order.service.exceptions.ArgumentMismatchException;
import guru.sfg.beer.order.service.exceptions.NotFoundException;
import guru.sfg.beer.order.service.services.BeerOrderService;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Sonny on 9/25/2022.
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest
{
    private static final String BASE_PATH = "/api/v1/customers/";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerOrderService beerOrderService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks( this );
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
        @DisplayName("should return all orders for the customer as a BeerOrderPagedList")
        void givenCustomerId_andPageNumber_andPageSize_whenListOrders_thenBeerOrderPagedList() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders";
            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();
            final PageRequest pageRequest = PageRequest.of( 1, 1 );
            final List<BeerOrderDto> orders = List.of( beerOrderDto );
            final BeerOrderPagedList expected = new BeerOrderPagedList(orders, pageRequest, 1);

            // when, then
            when(beerOrderService.listOrders( any(UUID.class), any() )).thenReturn( expected );

            mockMvc.perform(get( PATH )
                            .accept( MediaType.APPLICATION_JSON ))
                    .andDo( print() )
                    .andExpect( status().isOk() );

            verify(beerOrderService, times(1)).listOrders( any(UUID.class), any() );
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenListOrders_thenNotFoundException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders";

            // when, then
            when(beerOrderService.listOrders( any(UUID.class), any(PageRequest.class) ))
                .thenThrow( NotFoundException.class );

            mockMvc.perform(get( PATH ))
                    .andExpect( status().isNotFound() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

            verify(beerOrderService, times(1))
                .listOrders( any(UUID.class), any(PageRequest.class) );
        }
    }

    @Nested
    @DisplayName("placeOrder() Method")
    class PlaceOrderTest
    {
        @Test
        @DisplayName("should attach customer to BeerOrder, then save and publish BeerOrder")
        void givenCustomerId_andBeerOrderDto_whenPlaceOrder_thenSave_andPublishBeerOrder() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders";
            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();
            final String orderContent = objectMapper.writeValueAsString( beerOrderDto );

            // when, then
            when(beerOrderService.placeOrder( any(UUID.class), any(BeerOrderDto.class) )).thenReturn( beerOrderDto );

            mockMvc.perform(post( PATH )
                            .contentType( MediaType.APPLICATION_JSON )
                            .content( orderContent ))
                    .andExpect( status().isCreated() );

            verify(beerOrderService, times(1))
                    .placeOrder( any(UUID.class), any(BeerOrderDto.class) );
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer ID")
        void givenBadCustomerId_whenPlaceOrder_thenNotFoundException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders";
            final BeerOrderDto beerOrderDto = TestBeerOrderDto.getBeerOrderDto();
            final String orderContent = objectMapper.writeValueAsString( beerOrderDto );

            // when, then
            when(beerOrderService.placeOrder( any(UUID.class), any(BeerOrderDto.class) ))
                    .thenThrow( NotFoundException.class );

            mockMvc.perform(post( PATH )
                            .contentType( MediaType.APPLICATION_JSON )
                            .content( orderContent ))
                    .andExpect( status().isNotFound() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
        }
    }

    @Nested
    @DisplayName("getOrder() Method")
    class GetOrderByIdTest
    {
        @Test
        @DisplayName("should return the BeerOrderDto associated with the customer and order IDs")
        void givenCustomerId_andOrderId_whenGetOrder_thenBeerOrderDto() throws Exception
        {
            // given
            Customer customer = TestCustomer.getCustomer();
            UUID customerId = customer.getId();
            UUID orderId = UUID.randomUUID();
            List<BeerOrderLineDto> customerOrders = List.of( TestBeerOrderLineDto.getBeerOrderLineDto() );
            final String PATH = BASE_PATH + customerId + "/orders/" + orderId;

            BeerOrderDto expectedOrder = TestBeerOrderDto.getBeerOrderDto(
                    customerId,
                    customerId.toString(),
                    customerOrders,
                    OrderStatus.NEW.name(),
                    "call-back-url"
            );


            // when, then
            when(beerOrderService.getOrderById( any(UUID.class), any(UUID.class) )).thenReturn( expectedOrder );

            MvcResult result = mockMvc.perform(get( PATH )
                                            .accept( MediaType.APPLICATION_JSON ))
                                    .andExpect( status().isOk() )
                                    .andReturn();

            String returnValue = result.getResponse().getContentAsString();
            BeerOrderDto actualOrder = objectMapper.readValue( returnValue, BeerOrderDto.class );

            assertNotNull( actualOrder );
            assertEquals( expectedOrder.getCustomerId(), actualOrder.getCustomerId() );
            assertEquals( expectedOrder.getCustomerRef(), actualOrder.getCustomerRef() );
            verify(beerOrderService, times(1)).getOrderById( any(UUID.class), any(UUID.class) );

        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer or order ID")
        void givenBadCustomerId_orBadOrderId_whenGetOrder_thenNotFoundException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders/" + UUID.randomUUID();

            // when, then
            when(beerOrderService.getOrderById( any(UUID.class), any(UUID.class) ))
                    .thenThrow( NotFoundException.class );

            mockMvc.perform(get( PATH ))
                    .andExpect( status().isNotFound() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
        }

        @Test
        @DisplayName("should throw ArgumentMismatchException: Customer and order IDs don't match")
        void givenNonMatching_customerAndOrderIDs_whenGetOrder_thenIllegalStateException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders/" + UUID.randomUUID();

            // when, then
            when(beerOrderService.getOrderById( any(UUID.class), any(UUID.class) ))
                    .thenThrow( ArgumentMismatchException.class );

            mockMvc.perform(get( PATH ))
                    .andExpect( status().isBadRequest() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ArgumentMismatchException));
        }
    }

    @Nested
    @DisplayName("pickupOrder() Method")
    class PickupOrderTest
    {
        @Test
        @DisplayName("should change the status of the BeerOrder to PICKED_UP and save")
        void givenCustomerId_andOrderId_whenPickupOrder_thenChangeStatusToPickedUp() throws Exception
        {
            // given
            final UUID customerId = UUID.randomUUID();
            final UUID orderId = UUID.randomUUID();
            final String PATH = BASE_PATH + customerId + "/orders/" + orderId + "/pickup";

            // when, then
            doNothing().when(beerOrderService).pickupOrder( any(UUID.class), any(UUID.class) );

            mockMvc.perform(put( PATH ).contentType( MediaType.APPLICATION_JSON ))
                    .andExpect( status().isNoContent()  );

            verify(beerOrderService, times(1)).pickupOrder( any(UUID.class), any(UUID.class) );
        }

        @Test
        @DisplayName("should throw NotFoundException: Bad customer or order ID")
        void givenBadCustomerId_whenPickupOrder_thenNotFoundException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders/" + UUID.randomUUID() + "/pickup";

            // when, then
            doThrow(new NotFoundException())
                    .when(beerOrderService)
                    .pickupOrder( any(UUID.class), any(UUID.class) );

            mockMvc.perform(put( PATH ))
                    .andExpect( status().isNotFound() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
        }

        @Test
        @DisplayName("should throw ArgumentMismatchException: Customer and order IDs don't match")
        void givenNonMatching_customerAndOrderIDs_whenPickupOrder_thenArgumentMismatchException() throws Exception
        {
            // given
            final String PATH = BASE_PATH + UUID.randomUUID() + "/orders/" + UUID.randomUUID() + "/pickup";

            // when, then
            doThrow(new ArgumentMismatchException())
                    .when(beerOrderService)
                    .pickupOrder( any(UUID.class), any(UUID.class) );

            mockMvc.perform(put( PATH ))
                    .andExpect( status().isBadRequest() )
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ArgumentMismatchException));
        }
    }
}