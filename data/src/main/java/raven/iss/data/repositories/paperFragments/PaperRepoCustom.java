package raven.iss.data.repositories.paperFragments;

import raven.iss.data.model.Paper;

import java.util.List;

public interface PaperRepoCustom {
    Paper findByIdWithMembersAndAuthors(Integer id);
    Paper findByIdWithMembersAndReviews(Integer id);
    Paper findByIdWithAuthors(Integer id);
    Paper findByIdWithTopicsAndKeywords(Integer id);
    List<Paper> findAllByConfIdWithTopicsAndKeywords(Integer id);
}
