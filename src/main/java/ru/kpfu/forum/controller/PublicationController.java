package ru.kpfu.forum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kpfu.forum.dto.PublicationForm;
import ru.kpfu.forum.entity.Publication;
import ru.kpfu.forum.service.PublicationService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping("/")
    public String feed(@RequestParam(defaultValue = "false") boolean translate, Locale locale, Model model) {
        List<Publication> publications = publicationService.findAll();
        model.addAttribute("publications", publications);
        if (translate) {
            try {
                model.addAttribute("translatedTitles",
                        publicationService.translateTitles(publications, locale.getLanguage()));
            } catch (RestClientException exception) {
                model.addAttribute("translationFailed", true);
            }
        }
        return "feed";
    }

    @GetMapping("/publications/{id}")
    public String view(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean translate,
                       @AuthenticationPrincipal UserDetails currentUser, Locale locale, Model model) {
        Publication publication = publicationService.getById(id);
        model.addAttribute("publication", publication);
        model.addAttribute("isAuthor",
                currentUser != null && publicationService.isAuthor(publication, currentUser.getUsername()));
        if (translate) {
            try {
                model.addAttribute("translation", publicationService.translate(publication, locale.getLanguage()));
            } catch (RestClientException exception) {
                model.addAttribute("translationFailed", true);
            }
        }
        return "publication";
    }

    @GetMapping("/publications/new")
    public String createForm(Model model) {
        model.addAttribute("publicationForm", new PublicationForm());
        return "publication-form";
    }

    @PostMapping("/publications")
    public String create(@ModelAttribute PublicationForm publicationForm,
                         @AuthenticationPrincipal UserDetails currentUser) {
        Publication publication = publicationService.create(
                publicationForm.getTitle(), publicationForm.getContent(), currentUser.getUsername());
        return "redirect:/publications/" + publication.getId();
    }

    @GetMapping("/publications/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser, Model model) {
        Publication publication = publicationService.getByIdAndCheckAuthor(id, currentUser.getUsername());
        PublicationForm publicationForm = new PublicationForm();
        publicationForm.setTitle(publication.getTitle());
        publicationForm.setContent(publication.getContent());
        model.addAttribute("publicationForm", publicationForm);
        model.addAttribute("publicationId", id);
        return "publication-form";
    }

    @PostMapping("/publications/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute PublicationForm publicationForm,
                       @AuthenticationPrincipal UserDetails currentUser) {
        publicationService.update(
                id, publicationForm.getTitle(), publicationForm.getContent(), currentUser.getUsername());
        return "redirect:/publications/" + id;
    }

    @PostMapping("/publications/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        publicationService.delete(id, currentUser.getUsername());
        return "redirect:/";
    }
}
