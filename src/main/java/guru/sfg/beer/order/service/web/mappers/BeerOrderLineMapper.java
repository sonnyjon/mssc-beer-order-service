package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper
{
    BeerOrderLineDto toBeerOrderLineDto(BeerOrderLine line);

    BeerOrderLine toBeerOrderLine(BeerOrderLineDto dto);
}
