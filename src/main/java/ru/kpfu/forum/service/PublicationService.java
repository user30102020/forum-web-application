package ru.kpfu.forum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.kpfu.forum.dto.PublicationTranslation;
import ru.kpfu.forum.entity.Publication;
import ru.kpfu.forum.entity.User;
import ru.kpfu.forum.repository.PublicationRepository;
import ru.kpfu.forum.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final TranslationService translationService;

    public List<Publication> findAll() {
        return publicationRepository.findAllWithAuthor();
    }

    public Publication getById(Long publicationId) {
        return publicationRepository.findByIdWithAuthor(publicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Публикация " + publicationId + " не найдена"));
    }

    public boolean isAuthor(Publication publication, String username) {
        return publication.getAuthor().getUsername().equals(username);
    }

    public Publication getByIdAndCheckAuthor(Long publicationId, String username) {
        Publication publication = getById(publicationId);
        if (!isAuthor(publication, username)) {
            throw new AccessDeniedException(
                    "Пользователь " + username + " не является автором публикации " + publicationId);
        }
        return publication;
    }

    @Transactional
    public Publication create(String title, String content, String authorUsername) {
        User author = userRepository.findByUsername(authorUsername).orElseThrow();
        Publication publication = new Publication();
        publication.setTitle(title);
        publication.setContent(content);
        publication.setAuthor(author);
        return publicationRepository.save(publication);
    }

    @Transactional
    public void update(Long publicationId, String title, String content, String username) {
        Publication publication = getByIdAndCheckAuthor(publicationId, username);
        publication.setTitle(title);
        publication.setContent(content);
    }

    @Transactional
    public void delete(Long publicationId, String username) {
        publicationRepository.delete(getByIdAndCheckAuthor(publicationId, username));
    }

    public PublicationTranslation translate(Publication publication, String targetLanguage) {
        return new PublicationTranslation(
                translationService.translate(publication.getTitle(), targetLanguage),
                translationService.translate(publication.getContent(), targetLanguage));
    }

    public Map<Long, String> translateTitles(List<Publication> publications, String targetLanguage) {
        Map<Long, String> translatedTitles = new HashMap<>();
        for (Publication publication : publications) {
            translatedTitles.put(publication.getId(), translationService.translate(publication.getTitle(), targetLanguage));
        }
        return translatedTitles;
    }
}