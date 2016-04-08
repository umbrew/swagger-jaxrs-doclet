package fixtures.informationcard;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("javadoc")
@XmlRootElement(name = "informationCardCollection")
public class InformationCardCollection implements Serializable {

	/**
	 * This
	 */
	private static final long serialVersionUID = -3020829134656233763L;
	private Collection<InformationCard> cards;

	/**
	 * Default constructor
	 */
	public InformationCardCollection() {
		super();
		this.cards = Collections.emptyList();
	}

	public InformationCardCollection(List<InformationCard> cards) {
		this.cards = cards;
	}

	public Collection<InformationCard> getCards() {
		return this.cards;
	}

	public void setCards(Collection<InformationCard> cards) {
		this.cards = cards;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InformationCards [cards=" + this.cards + "]";
	}

}