package it.uniroma3.siw.bookclub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Incontro {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	@Size(min = 3, max = 30)
	private String nome;
	
	@NotBlank
	@Size(min = 3, max = 3000)
	private String descrizione;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate data;

	@Min(5)
	@Max(15)
	private int numeroPosti;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private Libro libro;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private User moderatore;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<User> partecipanti;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public int getNumeroPosti() {
		return numeroPosti;
	}

	public void setNumeroPosti(int numeroPosti) {
		this.numeroPosti = numeroPosti;
	}

	public Libro getLibro() {
		return libro;
	}

	public void setLibro(Libro libro) {
		this.libro = libro;
	}

	public User getModeratore() {
		return moderatore;
	}

	public void setModeratore(User moderatore) {
		this.moderatore = moderatore;
	}

	public List<User> getPartecipanti() {
		return partecipanti;
	}

	public void setPartecipanti(List<User> partecipanti) {
		this.partecipanti = partecipanti;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, libro, moderatore, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Incontro other = (Incontro) obj;
		return Objects.equals(data, other.data) && Objects.equals(libro, other.libro)
				&& Objects.equals(moderatore, other.moderatore) && Objects.equals(nome, other.nome);
	}
	
	
	
}