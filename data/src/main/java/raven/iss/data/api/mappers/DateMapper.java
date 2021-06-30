package raven.iss.data.api.mappers;

import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface DateMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd//HH::mm::ss");

    @Named("stringToDate")
    static LocalDateTime stringToDate(String dateString) {
        if(dateString == null) return null;
        return LocalDateTime.parse(dateString, formatter);
    }

    @Named("dateToString")
    static String dateToString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(formatter);
    }
}
