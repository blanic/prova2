package it.uniroma3.siw.bookclub.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.bookclub.controller.validator.AutoreValidator;
import it.uniroma3.siw.bookclub.controller.validator.CommentoValidator;
import it.uniroma3.siw.bookclub.model.Commento;
import it.uniroma3.siw.bookclub.model.Credentials;
import it.uniroma3.siw.bookclub.model.Libro;
import it.uniroma3.siw.bookclub.model.Recensione;
import it.uniroma3.siw.bookclub.model.User;
import it.uniroma3.siw.bookclub.service.AutoreService;
import it.uniroma3.siw.bookclub.service.CommentoService;
import it.uniroma3.siw.bookclub.service.CredentialsService;
import it.uniroma3.siw.bookclub.service.LibroService;
import it.uniroma3.siw.bookclub.service.RecensioneService;
import it.uniroma3.siw.bookclub.service.UserService;

@Controller
public class CommentoController {
	
	@Autowired
	AutoreService autoreService;
	
	@Autowired
	LibroService libroService;
	
	@Autowired
	CommentoValidator validator;

	@Autowired
	CommentoService commentoService;
	
	@Autowired
	CredentialsService credentialsService;
	
	@Autowired 
	UserService userService;
	
	@Autowired 
	RecensioneService recensioneService;
	
	/****************************************************************************
	 * USER
	 ***********************************************************************/

//	@GetMapping(value = "/user/commento/{id}")
//	public String getCommento(@PathVariable("id") Long id, Model model) {
//		Commento commento = this.commentoService.findById(id);
//		model.addAttribute("commento", commento);
//		return "user/autore/autore.html";
//	}
	
//	@GetMapping(value = "user/commento/commentoForm/{idRecensione}")
//	public String getNewCommento(@PathVariable("id") Long id, Model model) {
//		Recensione recensione = this.recensioneService.findById(id);
//		model.addAttribute("recensione", recensione);
//		model.addAttribute("commento", new Commento());
//		return "user/commento/commentoForm.html";
//	}

	@PostMapping(value = "/user/creaCommento/{idRecensione}")
	public String addCommento(@Valid @ModelAttribute("commento") Commento commento, 
			                  @PathVariable("idRecensione") Long idRecensione,
			                  BindingResult bindingResult, Model model) {
		validator.validate(commento, bindingResult);
		if (!bindingResult.hasErrors()) {
			Recensione recensione = this.recensioneService.findById(idRecensione);
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
	    	User autoreCommento = credentials.getUser();
	    	recensione.getCommenti().add(commento);
			commento.setAutore(autoreCommento);
			commento.setRecensione(recensione);
			commentoService.save(commento);
			model.addAttribute("recensione", recensione);
			User autoreRecensione = recensione.getAutore();
			model.addAttribute("autore", autoreRecensione);
			Libro libro = recensione.getLibro();
			model.addAttribute("libro", libro);
			List<Commento> commenti = recensione.getCommenti();
			model.addAttribute("commenti", commenti);
			return "user/recensione/recensione.html";
		}
		return "user/commento/failed.html";
	}

	/****************************************************************************
	 * ADMIN
	 ***********************************************************************/

//	@GetMapping(value = "/admin/commento/{id}")
//	public String getAdminCommentoUtente(@PathVariable("id") Long id, Model model) {
//		Commento commento = this.commentoService.findById(id);
//		model.addAttribute("commento", commento);
//		return "admin/commento/commento.html";
//	}

//	@GetMapping(value = "/admin/commentiRecensione/{id}")
//	public String getAdminCommentiRecensione(@PathVariable("id") Long id, Model model) {
//		Recensione recensione = this.recensioneService.findById(id);
//		List<Commento> commenti = this.commentoService.findAllByRecensione(recensione);
//		model.addAttribute("commenti", commenti);
//		return "admin/commento/commenti.html";
//	}
	
//	@GetMapping(value = "/admin/commentiUtente/{id}")
//	public String getAdminAllAutore(@PathVariable("id") Long id, Model model) {
//		User autore = this.userService.findById(id);
//		List<Commento> commenti = this.commentoService.findAllByAutore(autore);
//		model.addAttribute("commenti", commenti);
//		return "admin/commento/commenti.html";
//	}

	@GetMapping(value = "/admin/toDeleteCommento/{id}")
	public String toDeleteCommento(@PathVariable("id") Long id, Model model) {
		Commento commento = this.commentoService.findById(id);
		model.addAttribute("commento", commento);
		return "admin/commento/toDeleteCommento.html";
	}

	@GetMapping(value = "/admin/deleteCommento/{id}")
	public String deleteCommento(@PathVariable("id") Long id, Model model) {
		Commento commento = this.commentoService.findById(id);
		Recensione recensione = commento.getRecensione();
		recensione.getCommenti().remove(commento);
		User autoreCommento = commento.getAutore();
		autoreCommento.getCommenti().remove(commento);
		this.commentoService.deleteById(id);
		
		model.addAttribute("recensione", recensione);
		User autoreRecensione = recensione.getAutore();
		model.addAttribute("autore", autoreRecensione);
		Libro libro = recensione.getLibro();
		model.addAttribute("libro", libro);
		List<Commento> commenti = recensione.getCommenti();
		model.addAttribute("commenti", commenti);
		return "admin/recensione/recensione.html";
	}

}
