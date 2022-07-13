package it.uniroma3.siw.bookclub.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.bookclub.model.Credentials;
import it.uniroma3.siw.bookclub.model.Incontro;
import it.uniroma3.siw.bookclub.model.Recensione;
import it.uniroma3.siw.bookclub.model.User;
import it.uniroma3.siw.bookclub.service.CredentialsService;
import it.uniroma3.siw.bookclub.service.IncontroService;
import it.uniroma3.siw.bookclub.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private IncontroService incontroService;

	/****************************************************************************
	 * USER
	 ***********************************************************************/

	@GetMapping(value = "/user/utente/{id}")
	public String getUtente(@PathVariable("id") Long id, Model model) {
		User utente = this.userService.findById(id);
		model.addAttribute("utente", utente);
		List<Recensione> recensioni = utente.getRecensioni();
		model.addAttribute("recensioni", recensioni);
		List<Incontro> prossimiIncontri = utente.getPartecipazioni();
		prossimiIncontri.removeIf(incontro -> (incontro.getData().isBefore(LocalDate.now())));
		model.addAttribute("prossimiIncontri", prossimiIncontri);
		return "user/utente/utente.html";
	}

	@GetMapping(value = "/user/profile")
	public String getProfilo(Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User utente = credentials.getUser();
		model.addAttribute("utente", utente);
		List<Recensione> recensioni = utente.getRecensioni();
		model.addAttribute("recensioni", recensioni);
		List<Incontro> prossimiIncontri = utente.getPartecipazioni();
		prossimiIncontri.removeIf(incontro -> (incontro.getData().isBefore(LocalDate.now())));
		model.addAttribute("prossimiIncontri", prossimiIncontri);
		return "user/utente/utente.html";
	}

	/****************************************************************************
	 * ADMIN
	 ***********************************************************************/

	@GetMapping(value = "/admin/utente/{id}")
	public String getAdminUtente(@PathVariable("id") Long id, Model model) {
		User utente = this.userService.findById(id);
		model.addAttribute("utente", utente);
		List<Recensione> recensioni = utente.getRecensioni();
		model.addAttribute("recensioni", recensioni);
		return "admin/utente/utente.html";
	}

	@GetMapping(value = "/admin/utenti")
	public String getAdminUtenti(Model model) {
		List<User> utenti = this.userService.getAllUsers();
		model.addAttribute("utenti", utenti);
		return "admin/utente/utenti.html";
	}

}
