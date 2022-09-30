package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import modelo.Imagen;
import vista.Camara;

public class Ser extends Objeto {

	private final int CANT_OJOS = 40;// debe ser par

	private final int ENTRADAS_RNA = CANT_OJOS + +1// sensor vida
			+ 2// sensores vx y vy
			+ 1// sensor velocidad
			+ 1// cantidad misiles
			+ 1// cantidad contramedidas
			+ 4// fronteras
			+ 1// enemigo lock
			+ 1// distancia misil persiguiendo, depende del la vision
			+ 2// vx y vy de misil persiguiendo
			+ 1// hambre
	;

	private final int SALIDAS_RNA = 2 + // vx y vy
	+1// dispara misil
	+ 1// dispara contras
	+ 1// aceleracion
	;
	// linea de vision
	private final int TURNO = 20, CASTIGO = 10, VIDA_MAX = 1000, PUNTOS_MAX = 100, MIN_ESCUDO = 1, MAX_ESCUDO = 100,
			MIN_MUNICION = 1, MAX_MUNICION = 100, MIN_DANO = 1, MAX_DANO = 100,
			// //////////////////////////////////
			MAX_VISION = 600, MAX_PERSE = 1, MAX_ALCANCE = 1400, MAX_HAMBRE = 100;
	private float aceleracion, golpevx, golpevy, golpeve;
	private float[] ruedas;
	private int[] adn;

	private int cant_misiles, cant_contramedidas, puntos, frame, total_puntos, escudo, rojo, verde, azul, edad, vida,
			municion, vision, // caracterizticas propias
			dano, alcance, persecucion,// caracterizticas de sus misiles
			hambre// cuando supera el maximo, empieza a perder vida....
			;
	private ArrayList<Line2D> ojos;
	private ArrayList<Line2D> fronteras;
	private ArrayList<Misil> misiles;// mios
	private ArrayList<Contramedida> contras;
	private ArrayList<Misil> siguiendome;
	private Ser victima;
	private RNA cerebro;
	private Color color;
	private boolean dispara_misil, contramedidas, elegido;
	private Camara camara;
	private Imagen sprites;

	Ser(int ancho, int alto, Camara c, Imagen i) {
		super(ancho, alto, true);
		this.camara = c;
		this.sprites = i;
		valoresIniciales(null, null);
	}

	Ser(int ancho, int alto, Ser padre, Camara c, Imagen i) {
		super(ancho, alto, true);
		this.camara = c;
		this.sprites = i;
		valoresIniciales(padre, null);
	}

	Ser(int ancho, int alto, RNA cerebro, Camara c, Imagen i) {
		super(ancho, alto, true);
		this.camara = c;
		valoresIniciales(null, null);
		this.sprites = i;
		this.cerebro = new RNA(cerebro);
	}

	Ser(int ancho, int alto, Ser padre, Ser madre, Camara c, Imagen i) {
		super(ancho, alto, true);
		this.camara = c;
		this.sprites = i;
		valoresIniciales(padre, madre);
	}

	private void valoresIniciales(Ser padre, Ser madre) {
		this.golpeve = 0;
		this.golpevx = 0;
		this.golpevy = 0;
		this.escudo = 0;
		this.municion = 0;
		this.vision = 0;
		this.dano = 0;
		this.alcance = 0;
		this.persecucion = 0;
		this.edad = 0;
		this.hambre = MAX_HAMBRE;
		ojos = new ArrayList<Line2D>();
		fronteras = new ArrayList<Line2D>();
		misiles = new ArrayList<Misil>();
		siguiendome = new ArrayList<Misil>();
		victima = null;
		contras = new ArrayList<Contramedida>();

		/*
		 * Caracterizticas mentales y fisicas
		 */
		this.ruedas = new float[2];
		if (padre == null && madre == null) {
			crearCaracter();
			this.cerebro = new RNA(ENTRADAS_RNA, SALIDAS_RNA);
		} else {
			if (madre == null) {
				this.cerebro = new RNA(padre.cerebro);
				do {
					this.cerebro.mutar();
				} while (Math.random() < 0.5);
				heredarRasgos(padre);
				/*
				 * if (escudo * municion * vision * dano * alcance * persecucion
				 * == 0) { crearCaracter(); }
				 */
			} else {
				this.cerebro = new RNA(padre.cerebro, madre.cerebro);

				if (Math.random() < 0.5) {
					heredarRasgos(madre);
				} else {
					heredarRasgos(padre);
				}
				/*
				 * if (escudo * municion * vision * dano * alcance * persecucion
				 * == 0) { crearCaracter(); }
				 */
			}
		}
		// el color depende de las caracterizticas del ser.
		try {
			this.color = new Color(rojo, verde, azul);
		} catch (Exception el) {
			System.out.println(rojo + " " + verde + " " + azul);
			this.color = new Color(255, 255, 255);
			escudo = 1;// ser defectuoso
		}
		this.vida = VIDA_MAX;
		// this.turno = Math.random() < 0.5 ? true : false;
		this.dispara_misil = false;
		this.contramedidas = false;
		this.cant_contramedidas = municion;
		this.cant_misiles = municion;
		this.puntos = 0;
		this.total_puntos = 0;
		this.frame = 1 + (int) (Math.random() * TURNO);
	}

	private void crearCaracter() {
		Random rnd = new Random();
		int[] valores = new int[3];
		do {
			valores[0] = rnd.nextInt(PUNTOS_MAX + 1);// desde 0 a 100
			valores[1] = rnd.nextInt(PUNTOS_MAX - valores[0] + 1);
			valores[2] = PUNTOS_MAX - valores[0] - valores[1];
		} while (valores[0] + valores[1] + valores[2] != PUNTOS_MAX || valores[0] * valores[1] * valores[2] == 0);
		valores = mezclar(valores);

		dano = (int) Math.round(MIN_DANO + 1.0 * (MAX_DANO - MIN_DANO) * valores[0] / PUNTOS_MAX);
		escudo = (int) Math.round(MIN_ESCUDO + 1.0 * (MAX_ESCUDO - MIN_ESCUDO) * valores[1] / PUNTOS_MAX);
		municion = (int) Math.round(MIN_MUNICION + 1.0 * (MAX_MUNICION - MIN_MUNICION) * valores[2] / PUNTOS_MAX);

		rojo = 255 * valores[0] / PUNTOS_MAX;
		verde = 255 * valores[1] / PUNTOS_MAX;
		azul = 255 * valores[2] / PUNTOS_MAX;

		vision = MAX_VISION;
		alcance = MAX_ALCANCE;
		persecucion = MAX_PERSE;

		adn = new int[] { valores[0], valores[1], valores[2] };
	}

	private void heredarRasgos(Ser herenciador) {
		int[] valores = new int[3];
		do {
			valores[0] = -1;
			valores[1] = 0;
			valores[2] = 1;
			valores = mezclar(valores);
			valores[0] += herenciador.adn[0];
			valores[1] += herenciador.adn[1];
			valores[2] += herenciador.adn[2];

			if (valores[0] >= 0 && valores[1] >= 0 && valores[2] >= 0 && valores[0] <= PUNTOS_MAX
					&& valores[1] <= PUNTOS_MAX && valores[2] <= PUNTOS_MAX) {
				break;
			}
		} while (true);

		dano = (int) Math.round(MIN_DANO + 1.0 * (MAX_DANO - MIN_DANO) * valores[0] / PUNTOS_MAX);
		escudo = (int) Math.round(MIN_ESCUDO + 1.0 * (MAX_ESCUDO - MIN_ESCUDO) * valores[1] / PUNTOS_MAX);
		municion = (int) Math.round(MIN_MUNICION + 1.0 * (MAX_MUNICION - MIN_MUNICION) * valores[2] / PUNTOS_MAX);

		rojo = 255 * valores[0] / PUNTOS_MAX;
		verde = 255 * valores[1] / PUNTOS_MAX;
		azul = 255 * valores[2] / PUNTOS_MAX;

		vision = herenciador.vision;
		alcance = herenciador.alcance;
		persecucion = herenciador.persecucion;

		adn = new int[] { valores[0], valores[1], valores[2] };
	}

	private int[] mezclar(int[] vector) {
		Random rnd = new Random();
		for (int i = vector.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = vector[index];
			vector[index] = vector[i];
			vector[i] = a;
		}
		return vector;
	}

	/*
	 * public void sumarPts(int valor) { puntos += valor; if (valor > 0)
	 * total_puntos += valor; }
	 */
	public RNA getCerebro() {
		return cerebro;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getTotalPuntos() {
		return total_puntos;
	}

	public boolean estaVivo() {
		return vida > 0;
	}

	public void enemigoAvistado(Ser enemigo) {
		/*
		 * Algoritmo para apuntar a otro ser.
		 */
		if (victima == null) {
			victima = enemigo;
		}
	}

	public void agregaMisilSiguiendo(Misil m) {
		siguiendome.add(m);
	}

	public void eliminarMisilSiguiendo(Misil m) {
		siguiendome.remove(m);
	}

	public void sensores(float[] sens) {
		/*
		 * Pasamos valores de entrada a la red para saber que hacer
		 */

		float[] entradas = new float[ENTRADAS_RNA];
		for (int i = 0; i < sens.length; i++) {
			entradas[i] = sens[i];
		}
		entradas[CANT_OJOS] = vida / VIDA_MAX;
		entradas[CANT_OJOS + 1] = cant_misiles / municion;
		entradas[CANT_OJOS + 2] = cant_contramedidas / municion;
		/*
		 * Deteccion de misiles rastreandonos...
		 */
		float vex = 0;
		float vey = 0;
		float dis = 0;
		for (int i = siguiendome.size() - 1; i >= 0; i--) {
			Misil misil = siguiendome.get(i);
			if (misil != null && misil.estaVivo()) {
				if (dis == 0) {
					float disx = (float) (misil.getX() - getX());
					float disy = (float) (misil.getY() - getY());
					dis = (float) Math.sqrt(disx * disx + disy * disy);
					if (dis <= getVision() && dis > 0) {
						vex = disx / dis;
						vey = disy / dis;
						dis = (getVision() - dis) / getVision();
					} else {
						dis = 0;
					}
				}

			} else {
				siguiendome.remove(i);
			}
		}
		entradas[CANT_OJOS + 3] = vex;
		entradas[CANT_OJOS + 4] = vey;
		entradas[CANT_OJOS + 5] = dis;

		if (victima != null) {
			entradas[CANT_OJOS + 6] = 1;
		} else {
			entradas[CANT_OJOS + 6] = 0;// si tengo enemigo enfocado
		}
		entradas[CANT_OJOS + 7] = getVel() / VELOCIDAD_MAX;
		entradas[CANT_OJOS + 8] = getVex();
		entradas[CANT_OJOS + 9] = getVey();

		// fronteras = new ArrayList<Line2D>();
		if (getX() > getVision()) {
			entradas[CANT_OJOS + 10] = 0;
		} else {
			entradas[CANT_OJOS + 10] = (getVision() - getX()) / (getVision());
			// Line2D l = new Line2D.Float(getX(), getY(), 0, getY());
			// fronteras.add(l);
		}
		if (getY() > getVision()) {
			entradas[CANT_OJOS + 11] = 0;
		} else {
			entradas[CANT_OJOS + 11] = (getVision() - getY()) / (getVision());
			// Line2D l = new Line2D.Float(getX(), getY(), getX(), 0);
			// fronteras.add(l);
		}

		if (ancho_mundo - getX() > getVision()) {
			entradas[CANT_OJOS + 12] = 0;
		} else {
			entradas[CANT_OJOS + 12] = (getVision() - (ancho_mundo - getX())) / (getVision());
			// Line2D l = new Line2D.Float(getX(), getY(), ancho_mundo, getY());
			// fronteras.add(l);
		}

		if (alto_mundo - getY() > getVision()) {
			entradas[CANT_OJOS + 13] = 0;
		} else {
			entradas[CANT_OJOS + 13] = (getVision() - (alto_mundo - getY())) / (getVision());
			// Line2D l = new Line2D.Float(getX(), getY(), getX(), alto_mundo);
			// fronteras.add(l);
		}
		entradas[CANT_OJOS + 14] = 1.0f * hambre / MAX_HAMBRE;
		/**
		 * *********************************************************************
		 * ************* FIN DE LAS ENTRADAS
		 *********************************************************************************** 
		 */
		// la magia del cine
		float[] salidas = cerebro.reconocer(entradas);
		// fin de la magia del cine
		this.ruedas = new float[2];
		ruedas[0] = salidas[0];
		ruedas[1] = salidas[1];
		aceleracion = salidas[2];
		// solo si los valores son extremos, 70% fiabilidad
		dispara_misil = Math.abs(salidas[3] - 1) < 0.3f;
		// solo si los valores son extremos, 70% fiabilidad
		contramedidas = Math.abs(salidas[4] - 1) < 0.3f;
		edad++;
		hambre++;
		if (hambre > MAX_HAMBRE) {
			vida--;
			hambre = MAX_HAMBRE;
		}
		// fin de la magia
	}

	@Override
	public void dibujar(Graphics2D g) {
		if (estaVivo()) {
			if (camara.enPantalla(this)) {
				// dibujamos el puntaje
				g.setColor(color);
				// super.dibujar(g);
				AffineTransform reset = new AffineTransform();
				// reset.rotate(0,0,0);
				reset = g.getTransform();
				g.rotate(getAngulo() + Math.PI / 2.0, (int) getX(), (int) getY());
				// g.rotate(getAngulo()+Math.PI/2.0, 0, 0);
				g.drawImage(sprites.getSprite(Imagen.ID_NAVE), (int) (getX() - Imagen.RE_NAVE.width / 2.0),
						(int) (getY() - Imagen.RE_NAVE.height / 2.0), null);
				g.setTransform(reset);

				// g.setColor(Color.BLACK);
				// g.drawString("" + puntos , (int) (getX()-1 ), (int)
				// (getY()+1) );
				// dibujamos la naricita porq super ya dibuja el objeto
				g.drawLine((int) getX(), (int) getY(), (int) (getX() + Math.cos(getAngulo()) * DIAMETRO),
						(int) (getY() + Math.sin(getAngulo()) * DIAMETRO));
				// dibujamos la vida:
				g.drawLine((int) getX() - RADIO, (int) getY() - DIAMETRO, (int) (getX() - RADIO + DIAMETRO * vida
						/ VIDA_MAX), (int) getY() - DIAMETRO);
				// dibujamos el habmre
				g.drawLine((int) getX() - RADIO, (int) getY() + DIAMETRO, (int) (getX() - RADIO + DIAMETRO * hambre
						/ MAX_HAMBRE), (int) getY() + DIAMETRO);

				// g.drawString("[" + vida + "]", getX() - RADIO - 4, getY() -
				// DIAMETRO);
				// dibujamos la municion
				/*
				 * g.drawLine((int) (getX() - DIAMETRO), (int) (getY() + RADIO),
				 * (int) (getX() - DIAMETRO), (int) (getY() + RADIO - DIAMETRO *
				 * cant_misiles / MISILES_MAX));
				 */
				//
				// g.drawString("" +edad, getX() - DIAMETRO - RADIO, getY() +
				// 4);
				// Dibujamos la edad
				// g.drawString("P" + puntos, getX() + DIAMETRO, getY() + 4);
				// g.drawString("F" + frame%TURNO, getX() + DIAMETRO,
				// getY()+10);
				/*
				 * g.drawLine((int) (getX() + DIAMETRO), (int) (getY() + RADIO),
				 * (int) (getX() + DIAMETRO), (int) (getY() + RADIO - DIAMETRO
				 * cant_contramedidas / CONTRAMEDIDAS_MAX));
				 */
				// dibujamos el turno:
				/*
				 * g.drawLine((int) getX() - RADIO, (int) getY() + DIAMETRO,
				 * (int) (getX() - RADIO + DIAMETRO * (1 + frame % TURNO) /
				 * TURNO), (int) getY() + DIAMETRO);
				 */
				// dibujamos el tipo de red
				// g.drawString("sons:"+hijos+" dano:"+dano+" shield:"+escudo+" pts:"+puntos,
				// getX()
				// - DIAMETRO-20, getY() + DIAMETRO);
				if (elegido) {
					/*
					 * for (Line2D l : ojos) g.draw(l);
					 */
					g.draw(ojos.get(0));
					g.draw(ojos.get(ojos.size() - 1));

					for (Line2D l : fronteras) {
						g.draw(l);
					}

					if (victima != null && camara.enPantalla(victima)) {
						float disx = victima.getX() - getX();
						float disy = victima.getY() - getY();
						float dis = (float) Math.sqrt(disx * disx + disy * disy);
						if (dis > 1) {
							float dirx = disx / dis;
							float diry = disy / dis;
							g.drawLine((int) getX(), (int) getY(), (int) (getX() + dirx * dis), (int) (getY() + diry
									* dis));
						}
					}

				}
			}
			for (Misil m : misiles) {
				if (camara.enPantalla(m)) {
					m.dibujar(g);
				}
			}
			for (Contramedida c : contras) {
				if (camara.enPantalla(c)) {
					c.dibujar(g);
				}
			}
		} else {
			g.setColor(color); // dibujamos el tipo de red
			g.drawString(puntos + " " + cerebro.adn(), getX() - DIAMETRO, getY() + DIAMETRO);
			g.drawOval((int) getX() - RADIO, (int) getY() - RADIO, DIAMETRO, DIAMETRO);
		}

	}

	@Override
	public void actualizar() {
		/*
		 * Mis objetivos a la vista
		 */
		if (victima != null && !victima.estaVivo()) {
			victima = null;
		}
		/*
		 * Si ataco con misiles o no...
		 */
		if (dispara_misil) {
			dispara_misil = false;
			if (cant_misiles > 0) {
				/*
				 * Buscamos el enemigo mas opcionado
				 */
				Misil m = new Misil(getAnchoMundo(), getAltoMundo(), (float) (getX() + RADIO * Math.cos(getAngulo())),
						(float) (getY() + RADIO * Math.sin(getAngulo())), getAngulo(), sprites);
				// m.setPadre(this);
				m.setParametros(dano, alcance, persecucion);

				if (victima != null) {
					m.setObjetivo(victima);
					sumarPuntos();
				} else {
					vida -= CASTIGO;
				}
				misiles.add(m);
				cant_misiles--;
			} else {
				vida -= CASTIGO;
			}
		}

		/*
		 * Si lanzo contramedidas
		 */
		if (contramedidas) {
			contramedidas = false;
			if (cant_contramedidas > 0) {
				cant_contramedidas--;
				float offset = (float) (Math.random() * Math.PI / 2 + Math.PI / 2);
				/*
				 * if (Math.random() < 0.5) offset *= -1;
				 */
				Contramedida c = new Contramedida(getAnchoMundo(), getAltoMundo(), (float) (getX() + RADIO
						* Math.cos(getAngulo() + offset)), (float) (getY() + RADIO * Math.sin(getAngulo() + offset)),
						getAngulo() + offset);
				contras.add(c);
				c = new Contramedida(getAnchoMundo(), getAltoMundo(), (float) (getX() + RADIO
						* Math.cos(getAngulo() + offset + Math.PI / 2)), (float) (getY() + RADIO
						* Math.sin(getAngulo() + offset + Math.PI / 2)), (float) (getAngulo() + offset + Math.PI / 2));
				contras.add(c);
				if (siguiendome.size() == 0) {
					vida -= CASTIGO;
				} else {
					for (int i = siguiendome.size() - 1; i >= 0; i--) {
						Contramedida mico = contras.get((int) (Math.random() * contras.size()));
						siguiendome.get(i).setObjetivo(mico);
						siguiendome.remove(i);
						sumarPuntos();
					}
				}
			} else {
				vida -= CASTIGO;
			}
		}

		/*
		 * Actualizamos nuestra lista de misiles y contramedidas
		 */
		for (int i = misiles.size() - 1; i >= 0; i--) {
			Misil m = misiles.get(i);
			if (m.estaVivo()) {
				m.actualizar();
			} else {
				m.destruir();
				misiles.remove(i);
			}
		}
		for (int i = contras.size() - 1; i >= 0; i--) {
			Contramedida c = contras.get(i);
			if (c.estaVivo()) {
				c.actualizar();
			} else {
				contras.remove(i);
			}
		}

		girar(ruedas[0] - ruedas[1]);
		/*if (ruedas[1] > ruedas[0]) {
			girar(1);
		} else {
			girar(-1);
		}*/
		acelerar(aceleracion);

		super.actualizar();
		if (elegido) {
			calcularOjos();
		}
		this.frame++;
		/*
		 * Recarga de municiones
		 */
		if (frame % 500 == 0) {
			cant_misiles++;
			if (cant_misiles > municion) {
				cant_misiles = municion;
			}
		}
		if (frame % 800 == 0) {
			cant_contramedidas++;
			if (cant_contramedidas > municion) {
				cant_contramedidas = municion;
			}
		}

		if (frame > 8000) {
			frame = 0;
		}
		/*
		 * Efecto del golpe
		 */
		if (golpeve > 0) {
			setX(getX() + golpeve * golpevx);
			setY(getY() + golpeve * golpevy);
			golpeve -= 0.1f;
		}
		/*
		 * Rebotes
		 */
		if (getX() < 0) {
			vida -= CASTIGO;
			// puntos -= 1;
			setX(DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}

		if (getY() < 0) {
			vida -= CASTIGO;
			// puntos -= 1;
			setY(DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
		if (getX() > ancho_mundo) {
			vida -= CASTIGO;
			// puntos -= 1;
			setX(ancho_mundo - DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
		if (getY() > alto_mundo) {
			vida -= CASTIGO;
			// puntos -= 1;
			setY(alto_mundo - DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
	}

	public ArrayList<Line2D> getOjos() {
		// TODO Auto-generated method stub
		calcularOjos();
		victima = null;
		return ojos;
	}

	public ArrayList<Misil> getMisiles() {
		return misiles;
	}

	/*
	 * public ArrayList<Misil> nogetContramedidas() { return contras; }
	 */
	private void calcularOjos() {
		ojos = new ArrayList<Line2D>();
		float separador = 0.05f;
		float inf = (float) ((CANT_OJOS - 1) * separador / 2);
		for (int i = 0; i < CANT_OJOS; i++) {
			Line2D l = new Line2D.Float((float) (getX() + Math.cos(getAngulo() - inf + i * separador) * RADIO),
					(float) (getY() + Math.sin(getAngulo() - inf + i * separador) * RADIO),
					(float) (getX() + Math.cos(getAngulo() - inf + i * separador) * getVision()),
					(float) (getY() + Math.sin(getAngulo() - inf + i * separador) * getVision()));
			ojos.add(l);
		}
	}

	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		Rectangle2D rec = new Rectangle2D.Float(getX() - RADIO, getY() - RADIO, DIAMETRO, DIAMETRO);
		return rec;
	}

	public boolean miTuro() {
		return frame % TURNO == 0;
		/*
		 * turno = !turno; return turno;
		 */
		// return true;
	}

	public void destruir() {
		// TODO Auto-generated method stub
		vida = 0;
		for (Misil m : misiles) {
			m.destruir();
		}
		victima = null;
	}

	public Color miColor() {
		return this.color;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public void golpe(Misil m) {
		vida -= m.getDano();// * escudo / 100.0;
		golpevx = m.getVex();
		golpevy = m.getVey();
		golpeve = m.getVel() / 2;
		/*
		 * golpevx = m.getVex(); golpevy = m.getVey(); golpeve = m.getVel();//
		 * cuanto me empuja un misisl
		 */
	}

	public void curar() {
		// TODO Auto-generated method stub
		// vida += RECUPERACION;
		hambre -= dano;
		if (hambre < 0) {
			vida += hambre * -1;
			hambre = 0;
			if (vida > VIDA_MAX) {
				vida = VIDA_MAX;
			}
		}
		sumarPuntos();
	}

	public void setElegido(boolean es_elegido) {
		this.elegido = es_elegido;
	}

	public int getVision() {
		return vision;
	}

	private void sumarPuntos() {
		puntos++;
		total_puntos++;
	}

	public void resetPuntos() {
		puntos = 0;
	}
}
