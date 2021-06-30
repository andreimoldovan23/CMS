package raven.iss.data.repositories.pcMemberFragments;

import raven.iss.data.model.PCMember;

public interface PCMemberRepoCustom {
    PCMember findByUsernameAndConfIdWithPapers(String username, Integer id);
}
