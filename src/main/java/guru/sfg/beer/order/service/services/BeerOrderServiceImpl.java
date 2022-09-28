/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.OrderStatus;
import guru.sfg.beer.order.service.exceptions.ArgumentMismatchException;
import guru.sfg.beer.order.service.exceptions.NotFoundException;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService
{
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher publisher;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                                CustomerRepository customerRepository,
                                BeerOrderMapper beerOrderMapper,
                                ApplicationEventPublisher publisher)
    {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable)
    {
        Customer customer = customerRepository.findById( customerId ).orElseThrow( NotFoundException::new );
        Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer( customer, pageable );

        return new BeerOrderPagedList(
                        beerOrderPage
                                .stream()
                                .map( beerOrderMapper::toBeerOrderDto )
                                .collect(Collectors.toList()),
                        PageRequest.of(
                                beerOrderPage.getPageable().getPageNumber(),
                                beerOrderPage.getPageable().getPageSize()
                        ),
                        beerOrderPage.getTotalElements()
        );
    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto)
    {
        Customer customer  = customerRepository.findById( customerId ).orElseThrow( NotFoundException::new );

        BeerOrder beerOrder = beerOrderMapper.toBeerOrder( beerOrderDto );
        beerOrder.setId( null ); //should not be set by outside client
        beerOrder.setCustomer( customer );
        beerOrder.setOrderStatus( OrderStatus.NEW );
        beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder( beerOrder ));

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush( beerOrder );

        log.debug("Saved Beer Order: " + beerOrder.getId());

        //todo impl
      //  publisher.publishEvent(new NewBeerOrderEvent(savedBeerOrder));

        return beerOrderMapper.toBeerOrderDto( savedBeerOrder );
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId)
    {
        return beerOrderMapper.toBeerOrderDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId)
    {
        BeerOrder beerOrder = getOrder( customerId, orderId );
        beerOrder.setOrderStatus( OrderStatus.PICKED_UP );

        beerOrderRepository.save( beerOrder );
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId)
    {
        Customer customer = customerRepository.findById( customerId ).orElseThrow( NotFoundException::new );
        BeerOrder beerOrder = beerOrderRepository.findById( orderId ).orElseThrow( NotFoundException::new );

        // fall to exception if customer id's do not match - order not for customer
        if (beerOrder.getCustomer().getId().equals( customer.getId() )) return beerOrder;
        else throw new ArgumentMismatchException("Customer ID does not match the order's customer ID");
    }
}
