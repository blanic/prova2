package it.uniroma3.siw.bookclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	
	@GetMapping(value = "/about")
	public String getAbout(Model model) {
		return "/about";
	}

}
