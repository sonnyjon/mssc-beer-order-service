package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.cases.TestBeerDto;
import guru.sfg.beer.order.service.cases.TestConstants;
import guru.sfg.brewery.model.BeerDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sonny on 9/25/2022.
 */
@ExtendWith(MockitoExtension.class)
class BeerServiceImplTest
{
    @Mock
    RestTemplate restTemplate;

    BeerService beerService;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks( this );

        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when( restTemplateBuilder.build() ).thenReturn( restTemplate );

        beerService = new BeerServiceImpl( restTemplateBuilder );
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Nested
    @DisplayName("getBeerById() Method")
    class GetBeerByIdTest
    {
        @Test
        @DisplayName("should return BeerDto for the given ID")
        void givenId_whenGetBeerById_thenReturnBeerDto()
        {
            // given
            final BeerDto expected = TestBeerDto.getBeerDto();

            // when
            when(restTemplate.getForObject( anyString(), any() )).thenReturn( expected );

            BeerDto actual = beerService.getBeerById( expected.getId() ).orElse(null);

            // then
            assertNotNull( actual );
            assertEquals( expected, actual );
        }

        @Test
        @DisplayName("should return an empty optional when not found")
        void givenBadId_whenGetBeerById_thenReturnEmptyOptional()
        {
            // given
            final UUID badBeerId = UUID.randomUUID();

            // when
            when(restTemplate.getForObject( anyString(), any() )).thenReturn( null );

            Optional<BeerDto> optional = beerService.getBeerById( badBeerId );

            // then
            assertTrue( optional.isEmpty() );
        }
    }

    @Nested
    @DisplayName("getBeerByUpc() Method")
    class GetBeerByUpcTest
    {
        @Test
        @DisplayName("should return BeerDto for the given UPC")
        void givenUpc_whenGetBeerByUpc_thenReturnBeerDto()
        {
            // given
            final BeerDto expected = TestBeerDto.getBeerDto();
            final String upc = TestConstants.BEER_1_UPC;

            // when
            when(restTemplate.getForObject( anyString(), any() )).thenReturn( expected );

            BeerDto actual = beerService.getBeerByUpc( upc ).orElse(null);

            // then
            assertNotNull( actual );
            assertEquals( expected, actual );
        }

        @Test
        @DisplayName("should return an empty optional when UPC not found")
        void givenBadUpc_whenGetBeerByUpc_thenReturnEmptyOptional()
        {
            // given
            final String upc = null;

            // when
            when(restTemplate.getForObject( anyString(), any() )).thenReturn( null );

            Optional<BeerDto> optional = beerService.getBeerByUpc( upc );

            // then
            assertTrue( optional.isEmpty() );
        }
    }
}