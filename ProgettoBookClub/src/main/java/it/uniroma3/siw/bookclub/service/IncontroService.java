package it.uniroma3.siw.bookclub.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.bookclub.model.Autore;
import it.uniroma3.siw.bookclub.model.Incontro;
import it.uniroma3.siw.bookclub.model.comparator.ComparatoreDiAutoriPerCognomeENome;
import it.uniroma3.siw.bookclub.model.comparator.ComparatoreDiIncontriPerData;
import it.uniroma3.siw.bookclub.repository.IncontroRepository;

@Service
public class IncontroService {
	
	@Autowired
	IncontroRepository incontroRepository;

	public Incontro getIncontro(String nome) {
		return this.incontroRepository.findByNome(nome);
	}

	public Incontro findById(Long id) {
		return this.incontroRepository.findById(id).get();
	}

	public void save(@Valid Incontro incontro) {
		this.incontroRepository.save(incontro);
		
	}

	public void deleteById(Long id) {
		
		this.incontroRepository.deleteById(id);
	}

	public List<Incontro> getProssimiIncontri() {
		List<Incontro> incontri = new ArrayList<>();
		for(Incontro incontro : this.incontroRepository.findAll()) {
			incontri.add(incontro);
		}
		incontri.removeIf(incontro -> incontro.getData().isBefore(LocalDate.now()));
	    incontri.sort(new ComparatoreDiIncontriPerData());
	    return incontri;
		}

}
