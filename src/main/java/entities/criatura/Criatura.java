package entities.criatura;

import core.Atacable;
import core.Defendible;
import entities.Personaje;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_CRIATURA")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 30)

public class Criatura implements Atacable, Defendible {

	// TODO: MANUEL
	// Debemos de crear criaturas:
	// Son clases PUBLICAS que van extendidas de criatura, heredando sus atributos
	// Conejo, raton, gusano (una criatura que le va a ayudar)
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criatura_seq")
    @SequenceGenerator(name = "criatura_seq", sequenceName = "SEQ_CRIATURA", allocationSize = 1)
    private Long id;
    
    @Column(name="nombre", nullable=false, length=50)
	private String nombre;
    
    @Column(name="alias", length=50)
	private String alias;
    
    @Column(name="nivel", nullable=false)
	private int nivel;
    
    @Column(name="experiencia", nullable=false)
	private int experiencia;
    
    @Column(name="puntos_vida", nullable=false)
	private int puntosVida;
    
    @Column(name="puntos_ataque", nullable=false)
	private int puntosAtaque;
    
    @Column(name="tipo_ataque", length=50)
	private String tipoAtaque;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="personaje_id", nullable=false)
    private Personaje personaje;

	public Criatura(String alias, int nivel, int experiencia, int puntosVida, int puntosAtaque) {
		super();
		this.alias = alias;
		this.nivel = nivel;
		this.experiencia = experiencia;
		this.puntosVida = puntosVida;
		this.puntosAtaque = puntosAtaque;
	}

	public Criatura() {
		super();
	}

	public Criatura(String nombre, String alias, int nivel, int experiencia, int puntosVida, int puntosAtaque,
			String tipoAtaque) {
		super();
		this.nombre = nombre;
		this.alias = alias;
		this.nivel = nivel;
		this.experiencia = experiencia;
		this.puntosVida = puntosVida;
		this.puntosAtaque = puntosAtaque;
		this.tipoAtaque = tipoAtaque;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Personaje getPersonaje() {
		return personaje;
	}

	public void setPersonaje(Personaje personaje) {
		this.personaje = personaje;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipoAtaque() {
		return tipoAtaque;
	}

	public void setTipoAtaque(String tipoAtaque) {
		this.tipoAtaque = tipoAtaque;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(int experiencia) {
		this.experiencia = experiencia;
	}

	public int getPuntosVida() {
		return puntosVida;
	}

	public void setPuntosVida(int puntosVida) {
		this.puntosVida = puntosVida;
	}

	public int getPuntosAtaque() {
		return puntosAtaque;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setPuntosAtaque(int puntosAtaque) {
		this.puntosAtaque = puntosAtaque;
	}
	

	@Override
	public void recibirDanio(int danio) {
		// TODO Auto-generated method stub
		if (danio <= 0) return;
		
		this.puntosVida -= danio;
		if (this.puntosVida < 0) {
			System.out.println(this.nombre + " esta muerto, tiene 0 puntos de vida");
			this.puntosVida = 0;
		}

	}

	@Override
	public boolean estaVivo() {
		// TODO Auto-generated method stub
		return this.puntosVida > 0;
	}

	@Override
	public int atacar(Defendible objetivo) {
		// TODO Auto-generated method stub
		int danio = this.puntosAtaque;
		
		objetivo.recibirDanio(danio);
		return danio;
	}

	@Override
	public String toString() {
		return "Criatura [nombre=" + nombre + ", alias=" + alias + ", nivel=" + nivel + ", experiencia=" + experiencia
				+ ", puntosVida=" + puntosVida + ", puntosAtaque=" + puntosAtaque + ", tipoAtaque=" + tipoAtaque + "]";
	}
	
	

	// TODO
	// Metodos:
	// public int atacar (Atacable a); IMPLEMENTA ATACABLE
	// public int defender(Defendible d); IMPLEMENTA DEFENDIBLE
	// public void recibirDaño(int);

}
