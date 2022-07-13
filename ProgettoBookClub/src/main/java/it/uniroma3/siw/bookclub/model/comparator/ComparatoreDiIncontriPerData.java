package it.uniroma3.siw.bookclub.model.comparator;

import java.util.Comparator;

import it.uniroma3.siw.bookclub.model.Incontro;

public class ComparatoreDiIncontriPerData implements Comparator<Incontro> {

	@Override
	public int compare(Incontro o1, Incontro o2) {
		
		return o1.getData().compareTo(o2.getData());
	}

}
