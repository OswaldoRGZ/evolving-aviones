package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import modelo.Cementerio;
import modelo.Imagen;
import vista.ArquitecturaJuego;
import vista.Camara;
import vista.Pantalla;

public class Mundo implements ArquitecturaJuego {
	public int ANCHO = Pantalla.ANCHO;
	public int ALTO = Pantalla.ALTO;
	private int SERES_MIN = 10, TIEMPO_GENERACION = 10000, SERES_MAX = 100;
	// private final int SERES_MAX = 1000;

	private ArrayList<Ser> seres, cruza, externos;
	// private ArrayList<Point2D> cortes;
	private Camara camara;
	private int elegido, clones, cruzados, puntos_hijo, frame, vivos;
	private float coef;
	private Cementerio cementerio;
	private Imagen sprites;

	public Mundo(Camara cam) {
		this.camara = cam;
		this.camara.setDimMundo(ANCHO, ALTO);
		seres = new ArrayList<Ser>();
		cruza = new ArrayList<Ser>();
		externos = new ArrayList<Ser>();
		this.sprites = new Imagen();
		/*
		 * Creamos los seres que van a existir
		 */
		for (int i = 0; i < SERES_MIN; i++) {
			Ser s = new Ser(ANCHO, ALTO, camara, sprites);
			seres.add(s);
		}

		this.elegido = 0;
		this.clones = 0;
		this.cruzados = 0;
		this.puntos_hijo = 0;
		this.frame = TIEMPO_GENERACION;
		this.coef = 1;
		// cortes = new ArrayList<Point2D>();
	}

	public void crearSeres(ArrayList<RNA> cerebros) {
		if (cerebros == null)
			return;

		for (RNA rna : cerebros) {
			Ser ser = new Ser(ANCHO, ALTO, rna, camara, this.sprites);
			externos.add(ser);
			// camara.addSeguir(ser);
		}
	}

	public void setDimension(int alto, int ancho, int n_seres, int max_seres) {
		/*
		 * if (alto < Pantalla.ALTO) alto = Pantalla.ALTO; if (ancho <
		 * Pantalla.ANCHO) ancho = Pantalla.ANCHO;
		 */

		this.ALTO = alto;
		this.ANCHO = ancho;
		for (Objeto ser : seres) {
			ser.alto_mundo = alto;
			ser.ancho_mundo = ancho;
		}
		SERES_MIN = n_seres;
		SERES_MAX = max_seres;
		camara.setDimensiones(ANCHO, ALTO);
	}

	/**
	 * 
	 * @return
	 */
	public Ser siguienteElegido() {
		elegido++;
		if (elegido == seres.size())
			elegido = 0;
		try {
			seres.get(elegido).setElegido(true);
			return seres.get(elegido);
		} catch (IndexOutOfBoundsException e) {
			elegido = 0;
			return null;
		}

	}

	public Ser anteriorElegido() {
		elegido--;
		if (elegido < 0)
			elegido = 0;
		try {
			seres.get(elegido).setElegido(true);
			return seres.get(elegido);
		} catch (IndexOutOfBoundsException e) {
			elegido = 0;
			return null;
		}
	}

	@Override
	public void dibujar(Graphics2D g) {
		// TODO Auto-generated method stub
		// g.clearRect(0, 0, ANCHO, ALTO);
		g.setColor(Color.WHITE);
		g.fillRect(-500, -500, ANCHO + 1000, ALTO + 1000);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ANCHO, ALTO);
		g.setColor(Color.WHITE);
		g.drawString("Seres    : " + vivos + "(" + seres.size() + ")", camara.getPosx() + 20, camara.getPosy() + 10);
		g.drawString("Mutados  : " + clones, camara.getPosx() + 20, camara.getPosy() + 20);
		g.drawString("Cruzados : " + cruzados, camara.getPosx() + 20, camara.getPosy() + 30);
		g.drawString("Pts Hijo : " + puntos_hijo, camara.getPosx() + 20, camara.getPosy() + 40);
		// g.drawString("Fin Gener: " + frame, camara.getPosx() + 20,
		// camara.getPosy() + 50);
		// g.drawString("En espera: " + externos.size(), camara.getPosx() + 20,
		// camara.getPosy() + 60);
		// g.drawString("Coef Gen : " + coef, camara.getPosx() + 20,
		// camara.getPosy() + 70);
		// g.drawString("Elegido  : "+elegido,camara.getPosx()+20,camara.getPosy()+40);

		// g.drawString("Seres : "+seres.size(),camara.getPosx()+20,camara.getPosy()+20);
		for (Ser s : seres) {
			s.dibujar(g);
		}
		/*
		 * for (Point2D p: cortes) { g.drawOval((int)p.getX()-2,(int)p.getY()-2,
		 * 4, 4); }if(cortes.size() > 1000) cortes = new ArrayList<Point2D>();
		 */
	}

	@Override
	public void actualizar() {
		// TODO Auto-generated method stub
		/*
		 * Ciclo princiipal del mundo Colisiones actualizaciones
		 */
		vivos = 0;
		colisiones();
		for (int i = seres.size() - 1; i >= 0; i--) {
			Ser s = seres.get(i);
			if (!s.estaVivo()) {
				s.destruir();
				seres.remove(i);
			} else {
				vivos++;
				if (s.miTuro()) {
					// recojemos la infor que sus sensores detectan
					float[] sens = sensores(s);
					s.sensores(sens);
				}
				s.actualizar();
			}

		}
		/*
		 * agregar seres creados externamente
		 */
		for (int i = externos.size() - 1; i >= 0; i--) {
			if (vivos > SERES_MAX)
				break;
			seres.add(externos.get(i));
			externos.remove(i);
			vivos++;
			frame = 0;
		}
		/*
		 * Miramos si existe un espacio para crear un hijo
		 */
		if (vivos < SERES_MAX && frame % 10 == 0) {
			// vamos a ver quien es el que tiene mas puntos
			Ser mejor = null;
			double max = 0;
			ArrayList<Ser> nuevos = new ArrayList<Ser>();
			for (Ser ser : seres) {
				if (ser.estaVivo()) {
					if (ser.getPuntos() > max) {
						mejor = ser;
						max = ser.getPuntos();
					}
				}
			}
			/*
			 * Creamos el hijo del mejor
			 */
			if (mejor != null) {
				Ser nuevo = new Ser(ANCHO, ALTO, mejor, this.camara, this.sprites);
				nuevos.add(nuevo);
				clones++;
				cruza.add(mejor);
				mejor.resetPuntos();
			}
			// si no existe un buen ser
			Ser extra = new Ser(ANCHO, ALTO, this.camara, this.sprites);
			nuevos.add(extra);

			while (cruza.size() > 2) {
				Ser nuevo = new Ser(ANCHO, ALTO, cruza.get(0), cruza.get(1), this.camara, this.sprites);
				nuevos.add(nuevo);
				cruza.remove(0);
				cruza.remove(1);
				cruzados++;
			}
			for (Ser ser : nuevos) {
				seres.add(ser);
				vivos++;
			}
		}
		frame++;
		if (frame >= TIEMPO_GENERACION)
			frame = 0;
	}

	private void finGeneracion() {
		if (frame < 0) {
			frame = TIEMPO_GENERACION;
			// lista de los nuevos
			ArrayList<Ser> nuevos = new ArrayList<Ser>();
			// calculamos el promedio
			puntos_hijo = 1;
			for (Ser ser : seres) {
				puntos_hijo += ser.getPuntos();
			}
			puntos_hijo = 1 + puntos_hijo / (seres.size() + 1);
			// evaluamos cada ser si tiene probabilidades de pasar a la
			// siguiente
			// generacion
			for (Ser ser : seres) {
				double prob = ser.getPuntos() / puntos_hijo;

				boolean paso = false;
				while (prob > coef) {
					prob -= coef;
					paso = true;
					Ser nuevo = new Ser(ANCHO, ALTO, ser.getCerebro(), this.camara, this.sprites);
					nuevo.setColor(ser.miColor());
					/*
					 * double ang = Math.random()*2*Math.PI; nuevo.setX( (float)
					 * (ser.getX()+Math.cos(ser.getAngulo()+ang)*ser.DIAMETRO )
					 * ); nuevo.setY( (float)
					 * (ser.getY()+Math.sin(ser.getAngulo()+ang)*ser.DIAMETRO )
					 * ); nuevo.setAngulo((float) (ser.getAngulo()+ang));
					 * nuevo.golpe( (float)
					 * Math.cos(ser.getAngulo()+ang),(float)
					 * Math.sin(ser.getAngulo()+ang),ser.VELOCIDAD_MAX);
					 */
					nuevos.add(nuevo);
					clones++;
				}
				// evaluamos si efectivamente paso a la siguiente generacion o
				// tica
				// matarlo
				if (!paso) {
					ser.destruir();
					// despues en el ciclo principal sera removido
				} else {
					cruza.add(ser);
					// si paso a la siguiente generacion, examinamos si estaba
					// muerto
					if (!ser.estaVivo()) {
						cementerio.addMuerto(ser);
					} // else
						// si esta vivo, simplemente que siga viviendo
						// ser.sumarPts(-ser.getPuntos());

				}
			}
			// Limpiamos la generacion de los seres basura

			for (int i = seres.size() - 1; i >= 0; i--) {
				Ser s = seres.get(i);
				if (!s.estaVivo()) {
					s.destruir();
					seres.remove(i);
				}
			}
			// agregamos los nuevos
			while (cruza.size() > 2) {
				Ser nuevo = new Ser(ANCHO, ALTO, cruza.get(0), cruza.get(1), this.camara, this.sprites);
				nuevos.add(nuevo);
				cruza.remove(0);
				cruza.remove(1);
				cruzados++;
			}
			for (Ser ser : nuevos) {
				seres.add(ser);
			}
			// recalculamos el coeficiente
			coef = coef + 2 * seres.size() / SERES_MIN;
			coef = coef / 3;
			/*
			 * Mantenemos la poblacion
			 */
			if (externos.size() <= 0)
				while (seres.size() < SERES_MIN) {
					Ser nuevo = new Ser(ANCHO, ALTO, this.camara, this.sprites);
					seres.add(nuevo);
				}
			/*
			 * las nuevas generaciones de siempre
			 */
			while (seres.size() < SERES_MIN + SERES_MIN / 3) {
				Ser nuevo = new Ser(ANCHO, ALTO, this.camara, this.sprites);
				seres.add(nuevo);
			}
			cementerio.finGeneracion();
		}
	}

	private void colisiones() {
		// Primero revisamos las colisiones de misiles.
		for (Ser s : seres) {
			if (s.estaVivo())
				for (Misil misil : s.getMisiles()) {
					if (misil.estaVivo() && misil.getObjetivo() != null) {
						Objeto obj = misil.getObjetivo();
						if (obj.getClass() == Ser.class) {
							Ser ser = (Ser) obj;
							if (ser.estaVivo())
								if (distancia(ser, misil) < ser.RADIO + misil.RADIO) {
									ser.golpe(misil);
									misil.golpe();
									s.curar();
									// break;
								}
						}
					}
				}
		}
	}

	private float[] sensores(Ser s) {
		// TODO Auto-generated method stub
		ArrayList<Line2D> ojos = s.getOjos();
		int ojo = 0, cont_corte = 0;
		float[] sens = new float[ojos.size()];
		for (Ser ser : seres) {
			if (ser.estaVivo() && !ser.equals(s)) {

				if (distancia(s, ser) <= s.getVision()) {
					// si ni siquiera esta cerca, de que sirve?
					ojo = 0;
					cont_corte = 0;
					for (Line2D line2d : ojos) {
						if (line2d.getBounds2D().intersects(ser.getBounds2D())) {
							float m = (float) (line2d.getY2() - line2d.getY1());
							m = (float) (m / (line2d.getX2() - line2d.getX1()));
							float px = (float) ((m * m * line2d.getX1()) - m * (line2d.getY1() - ser.getY()) + ser
									.getX());
							px = px / (m * m + 1);
							float py = (float) (m * (px - line2d.getX1()) + line2d.getY1());

							if (distancia(ser.getX(), px, ser.getY(), py) < ser.RADIO) {
								m = distancia(s, ser);

								if (sens[ojo] == 0)
									sens[ojo] = (s.getVision() - m) / s.getVision();
								else {
									float dis = (s.getVision() - m) / s.getVision();
									if (dis > sens[ojo]) {
										sens[ojo] = dis;
									}
								}
								if (ojo >= Math.floor(ojos.size() / 2) - 2 && ojo <= Math.ceil(ojos.size() / 2) + 2) {
									// Solo si esta en los ojos del centro, el
									// misil
									// saldra apuntando
									s.enemigoAvistado(ser);
								}
								// Point2D p = new Point2D.Float(px, py);
								// cortes.add(p);
								cont_corte++;
								// si este sensor lo detecta, los sensores
								// lejanos no
							} else {
								// verificamos si ya fue detectado por otro
								// sensor
								if (cont_corte > 0)
									// si es asi, no seguimos buscando mas, otro
									// sensor no
									// lo
									// va a detectar
									break;
							}
						}
						ojo++;
					}
				}
			}
		}
		/*
		 * Aqui voy, estaba mejorando el algoritmos de los sensores...
		 */
		return sens;
	}

	private float distancia(Objeto s1, Objeto s2) {
		/*
		 * Retorna raiz ( (s1.x-s2.x)^2 + (s1.y-s2.y)^2 )
		 */
		return (float) (Math.sqrt(Math.pow(s1.getX() - s2.getX(), 2) + Math.pow(s1.getY() - s2.getY(), 2)));
	}

	private float distancia(float x1, float x2, float y1, float y2) {
		/*
		 * Retorna raiz ( (s1.x-s2.x)^2 + (s1.y-s2.y)^2 )
		 */
		return (float) (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
	}

	public void setCementerio(Cementerio c) {
		// TODO Auto-generated method stub
		cementerio = c;
	}

	/*
	 * public void agregarSer(RNA cerebro) { // TODO Auto-generated method stub
	 * Ser ser = new Ser(ANCHO, ALTO, cerebro, camara); seres.add(ser); }
	 */

	public Ser getGanador() {
		if (seres.size() > 1)
			return null;
		return seres.get(0);
	}

	public void setPromedioClon(int valor) {
		// TODO Auto-generated method stu
		if (valor > 0)
			this.puntos_hijo = valor;
	}
}