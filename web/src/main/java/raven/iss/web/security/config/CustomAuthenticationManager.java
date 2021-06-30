package raven.iss.web.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.Author;
import raven.iss.data.repositories.*;
import raven.iss.data.repositories.authorFragments.AuthorRepo;
import raven.iss.data.repositories.listenerFragments.ListenerRepo;
import raven.iss.data.repositories.paperFragments.PaperRepo;
import raven.iss.data.repositories.pcMemberFragments.PCMemberRepo;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager {

    private final ChairRepo chairRepo;
    private final AuthorRepo authorRepo;
    private final PCMemberRepo pcMemberRepo;
    private final ListenerRepo listenerRepo;

    public boolean chairConferenceIdMatches(Authentication authentication, Integer conferenceId) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        return chairRepo.findByUsernameAndConfId(username, conferenceId) != null;
    }

    public boolean authorConferenceIdMatches(Authentication authentication, Integer conferenceId) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        return authorRepo.findByUsernameAndConfId(username, conferenceId) != null;
    }

    public boolean pcMemberConferenceIdMatches(Authentication authentication, Integer conferenceId) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        return pcMemberRepo.findByUsernameAndConfId(username, conferenceId) != null;
    }

    public boolean listenerConferenceIdMatches(Authentication authentication, Integer conferenceId) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        return listenerRepo.findByUsernameAndConfId(username, conferenceId) != null;
    }

    @Transactional
    public boolean isPaperOfAuthor(Authentication authentication, Integer conferenceId, Integer paperId) {
        if (paperId == null) return true;

        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        Author author = authorRepo.findByUsernameAndConfIdWithPapers(username, conferenceId);
        if (author == null) return false;

        return author.getPapers().stream().anyMatch(paper -> paper.getId().equals(paperId));
    }

}
