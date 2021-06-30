package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.model.Review;

@Mapper(config = NonBuilderConfigMapper.class)
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    ReviewDTO reviewToDTO(Review review);

    Review DTOtoReview(ReviewDTO reviewDTO);

}

