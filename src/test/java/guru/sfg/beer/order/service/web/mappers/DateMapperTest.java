package guru.sfg.beer.order.service.web.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Sonny on 9/24/2022.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DateMapper.class)
class DateMapperTest
{
    @Autowired
    private DateMapper dateMapper;

    @Nested
    @DisplayName("asOffsetDateTime() Method")
    class ToOffsetDateTime
    {
        @Test
        @DisplayName("should convert Timestamp and return as OffsetDateTime")
        void givenTimeStamp_whenAsOffsetDateTime_thenConvertToOffsetDateTime()
        {
            // given
            final Timestamp expected = Timestamp.valueOf( LocalDateTime.now() );

            // when
            OffsetDateTime actual = dateMapper.asOffsetDateTime( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.toLocalDateTime().getYear(), actual.getYear() );
            assertEquals( expected.toLocalDateTime().getMonth(), actual.getMonth() );
            assertEquals( expected.toLocalDateTime().getDayOfMonth(), actual.getDayOfMonth() );
            assertEquals( expected.toLocalDateTime().getHour(), actual.getHour() );
            assertEquals( expected.toLocalDateTime().getMinute(), actual.getMinute() );
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenAsOffsetDateTime_thenReturnNull()
        {
            final Timestamp expected = null;
            OffsetDateTime actual = dateMapper.asOffsetDateTime( expected );

            assertNull( actual );
        }
    }

    @Nested
    @DisplayName("asTimestamp() Method")
    class ToTimestamp
    {
        @Test
        @DisplayName("should convert OffsetDateTime and return as Timestamp")
        void givenOffsetDateTime_whenAsTimestamp_thenConvertToTimestamp()
        {
            // given
            final OffsetDateTime expected = OffsetDateTime.now().withOffsetSameInstant( ZoneOffset.UTC );


            // when
            Timestamp actual = dateMapper.asTimestamp( expected );

            // then
            assertNotNull( actual );
            assertEquals( expected.getYear(), actual.toLocalDateTime().getYear() );
            assertEquals( expected.getMonth(), actual.toLocalDateTime().getMonth() );
            assertEquals( expected.getDayOfMonth(), actual.toLocalDateTime().getDayOfMonth() );
            assertEquals( expected.getHour(), actual.toLocalDateTime().getHour() );
            assertEquals( expected.getMinute(), actual.toLocalDateTime().getMinute() );
        }

        @Test
        @DisplayName("should return null")
        void givenNull_whenAsTimestamp_thenReturnNull()
        {
            final OffsetDateTime expected = null;
            Timestamp actual = dateMapper.asTimestamp( expected );

            assertNull( actual );
        }
    }
}