package raven.iss.data.validators;

import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.exceptions.NotFoundException;

import java.time.LocalDateTime;

public interface Helper {

    static void validateDates(LocalDateTime l1, LocalDateTime l2, String message) {
        if (l1.isAfter(l2)) throw new InternalErrorException(message);
    }

    static void compareStrings(Boolean flag, String s1, String s2, String message) {
        if ((flag && s1.equals(s2)) || (!flag && !s1.equals(s2)))
            throw new InternalErrorException(message);
    }

    static void checkBoolean(Boolean bool, String message) {
        if(bool == null || !bool)
            throw new InternalErrorException(message);
    }

    static Object checkNull(Object obj, String message) {
        if (obj == null)
            throw new NotFoundException(message);
        return obj;
    }

    static void checkNotNull(Object obj, String message) {
        if (obj != null)
            throw new InternalErrorException(message);
    }

    static void checkNumbers(Integer n1, Integer n2, String message) {
        if (!(n1 < n2))
            throw new InternalErrorException(message);
    }

}
