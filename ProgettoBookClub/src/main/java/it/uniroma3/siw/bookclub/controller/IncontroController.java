package it.uniroma3.siw.bookclub.controller;

import java.time.LocalDate;
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

import it.uniroma3.siw.bookclub.model.Credentials;
import it.uniroma3.siw.bookclub.model.Incontro;
import it.uniroma3.siw.bookclub.model.Libro;
import it.uniroma3.siw.bookclub.model.Recensione;
import it.uniroma3.siw.bookclub.model.User;
import it.uniroma3.siw.bookclub.service.AutoreService;
import it.uniroma3.siw.bookclub.service.CredentialsService;
import it.uniroma3.siw.bookclub.service.IncontroService;
import it.uniroma3.siw.bookclub.service.LibroService;
import it.uniroma3.siw.bookclub.service.RecensioneService;
import it.uniroma3.siw.bookclub.service.UserService;
import it.uniroma3.siw.bookclub.controller.validator.AutoreValidator;
import it.uniroma3.siw.bookclub.controller.validator.IncontroValidator;

@Controller
public class IncontroController {
	
	@Autowired
	AutoreService autoreService;
	
	@Autowired
	LibroService libroService;
	
	@Autowired
	IncontroValidator validator;
	
	@Autowired
	RecensioneService recensioneService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	IncontroService incontroService;

	@Autowired
	CredentialsService credentialsService;
	
	/****************************************************************************
	 * PUBLIC
	 ***********************************************************************/
	
	@GetMapping(value="/incontri")
	public String getPublicIncontri(Model model) {
		List<Incontro> incontri = this.incontroService.getProssimiIncontri();
		model.addAttribute("incontri", incontri);
		return "/incontri.html";
	}
	
	@GetMapping(value = "/incontro/{id}")
	public String getPublicIncontro(@PathVariable("id") Long id, Model model) {
		Incontro incontro = incontroService.findById(id);
		model.addAttribute("incontro", incontro);
		Libro libro = incontro.getLibro();
		model.addAttribute("libro", libro);
		User moderatore = incontro.getModeratore();
		model.addAttribute("moderatore", moderatore);
		return "/incontro.html";
	}
	/****************************************************************************
	 * USER
	 ***********************************************************************/

	@GetMapping(value = "/user/incontro/{id}")
	public String getIncontro(@PathVariable("id") Long id, Model model) {
		Incontro incontro = incontroService.findById(id);
		model.addAttribute("incontro", incontro);
		Libro libro = incontro.getLibro();
		model.addAttribute("libro", libro);
		User moderatore = incontro.getModeratore();
		model.addAttribute("moderatore", moderatore);
		return "user/incontro/incontro.html";
	}
	
	@GetMapping(value="/user/incontri")
	public String getIncontri(Model model) {
		List<Incontro> incontri = this.incontroService.getProssimiIncontri();
		model.addAttribute("incontri", incontri);
		return "user/incontro/incontri.html";
	}
	
	@GetMapping(value = "/user/toAddPartecipazione/{id}")
	public String toAddPartecipazione(@PathVariable("id") Long id, Model model) {
		Incontro incontro = this.incontroService.findById(id);
		List<User> partecipanti = incontro.getPartecipanti();
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	User user = credentials.getUser();
		if(partecipanti.size()>=incontro.getNumeroPosti()) {
			return "user/incontro/incontri";
		}
		else if (partecipanti.contains(user)) {
			return "user/incontro/incontri";
		}
		else {
			partecipanti.add(user);
			user.getPartecipazioni().add(incontro);
		}
		this.userService.saveUser(user);
		model.addAttribute("utente", user);
		List<Recensione> recensioni = user.getRecensioni();
		model.addAttribute("recensioni", recensioni);
		List<Incontro> prossimiIncontri = user.getPartecipazioni();
		prossimiIncontri.removeIf(i -> (i.getData().isBefore(LocalDate.now())));
		model.addAttribute("prossimiIncontri", prossimiIncontri);
		return "user/utente/utente.html";
	}
	
	@GetMapping(value = "/user/toDeletePartecipazione/{id}")
	public String toDeletePartecipazione(@PathVariable("id") Long id, Model model) {
		Incontro incontro = this.incontroService.findById(id);
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	User user = credentials.getUser();
    	incontro.getPartecipanti().remove(user);
    	user.getPartecipazioni().remove(incontro);
    	this.incontroService.save(incontro);
    	List<Incontro> partecipazioni = user.getPartecipazioni();
		model.addAttribute("partecipazioni", partecipazioni);
		model.addAttribute("utente", user);
		List<Recensione> recensioni = user.getRecensioni();
		model.addAttribute("recensioni", recensioni);
		List<Incontro> prossimiIncontri = user.getPartecipazioni();
		prossimiIncontri.removeIf(i -> (i.getData().isBefore(LocalDate.now())));
		model.addAttribute("prossimiIncontri", prossimiIncontri);
		return "user/utente/utente.html";
	}


	/****************************************************************************
	 * ADMIN
	 ***********************************************************************/

	@GetMapping(value="/admin/incontri")
	public String getAdminIncontri(Model model) {
		List<Incontro> incontri = this.incontroService.getProssimiIncontri();
		model.addAttribute("incontri", incontri);
		return "admin/incontro/incontri.html";
	}
	
	@GetMapping(value = "/admin/incontro/{id}")
	public String getAdminIncontro(@PathVariable("id") Long id, Model model) {
		Incontro incontro = incontroService.findById(id);
		model.addAttribute("incontro", incontro);
		Libro libro = incontro.getLibro();
		model.addAttribute("libro", libro);
		User moderatore = incontro.getModeratore();
		model.addAttribute("moderatore", moderatore);
		return "admin/incontro/incontro.html";
	}
	
	@GetMapping("admin/nuovoIncontro")
	public String creaIncontro(Model model) {
		List<Libro> libri = this.libroService.findAllOrdered();
		model.addAttribute("libri", libri);
		return "admin/libro/libri.html";
	}

	@PostMapping(value = "/admin/incontroForm/{idLibro}")
	public String getNewIncontro(@PathVariable("idLibro") Long idLibro, Model model) {
		Libro libro = this.libroService.findById(idLibro);
		model.addAttribute("incontro", new Incontro());
		model.addAttribute("libro", libro);
		return "admin/incontro/incontroForm.html";
	}

	@PostMapping(value = "/admin/creaIncontro/{idLibro}")
	public String toSetLibroIncontro(@Valid @ModelAttribute("incontro") Incontro incontro,
			@PathVariable("idLibro") Long idLibro,
			BindingResult bindingResult, Model model) {
		validator.validate(incontro, bindingResult);
		if (!bindingResult.hasErrors()) {
			Libro libro = this.libroService.findById(idLibro);
			incontro.setLibro(libro);
			libro.getIncontri().add(incontro);
			this.incontroService.save(incontro);
			User moderatore = incontro.getModeratore();
			model.addAttribute("moderatore", moderatore);
			model.addAttribute("libro", libro);
			return "admin/incontro/incontro.html";
		}
		return "admin/incontro/incontroForm.html";
	}
	
	@GetMapping(value = "/admin/toSetModeratore/{idIncontro}")
	public String toSetModeratore(@PathVariable("idIncontro") Long idIncontro, Model model) {
		Incontro incontro = this.incontroService.findById(idIncontro);
		model.addAttribute("incontro", incontro);
		List<User> utenti = this.userService.getAllUsers();
		model.addAttribute("utenti", utenti);
		return "admin/incontro/toSetModeratore.html";
	}
	
	@GetMapping(value = "/admin/setModeratore/{idIncontro}/{idModeratore}")
	public String setModeratore(@PathVariable("idIncontro") Long idIncontro, 
			                    @PathVariable("idModeratore") Long idModeratore, 
			                    Model model) {
		Incontro incontro = this.incontroService.findById(idIncontro);
		User moderatore = this.userService.findById(idModeratore);
		incontro.setModeratore(moderatore);
		this.incontroService.save(incontro);
		model.addAttribute("incontro", incontro);
		Libro libro = incontro.getLibro();
		model.addAttribute("libro", libro);
		model.addAttribute("moderatore", moderatore);
		return "admin/incontro/incontro.html";
	}

	@GetMapping(value = "/admin/toDeleteIncontro/{id}")
	public String toDeleteIncontro(@PathVariable("id") Long id, Model model) {
		Incontro incontro = this.incontroService.findById(id);
		model.addAttribute("incontro", incontro);
		return "admin/incontro/toDeleteIncontro.html";
	}

	@GetMapping(value = "/admin/deleteIncontro/{id}")
	public String deleteIncontro(@PathVariable("id") Long id, Model model) {
		Incontro incontro = this.incontroService.findById(id);
		model.addAttribute("incontro", incontro);
		Libro libro = incontro.getLibro();
		libro.getIncontri().remove(incontro);
		List<User> partecipanti = incontro.getPartecipanti();
		for(User partecipante : partecipanti) {
			partecipante.getPartecipazioni().remove(incontro);
			this.userService.saveUser(partecipante);
		}
		this.incontroService.deleteById(id);
		this.libroService.save(libro);
		return "admin/incontro/incontri.html";
	}
	
}