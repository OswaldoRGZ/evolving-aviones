package controlador;

import java.awt.Color;
import java.awt.Graphics2D;

import vista.ArquitecturaJuego;

public class Objeto implements ArquitecturaJuego {
	protected int RADIO = 9;
	protected int DIAMETRO = RADIO * 2;
	protected int VELOCIDAD_MAX = 3;
	protected int VELOCIDAD_MIN = 1;
	private float x, y, vex, vey, vel, angulo;
	protected int alto_mundo, ancho_mundo;
	private boolean servivo;

	Objeto(int ancho, int alto, boolean esta_vivo) {
		this.alto_mundo = alto;
		this.ancho_mundo = ancho;
		x = (float) (Math.random() * ancho_mundo);
		y = (float) (Math.random() * alto_mundo);
		vex = 0;
		vey = 0;
		vel = 0;
		angulo = (float) (Math.random() * Math.PI * 2);
		this.servivo = esta_vivo;
	}

	public boolean estaVivo() {
		return false;
	}

	public boolean esSerVivoNA() {
		return servivo;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getAngulo() {
		return angulo;
	}

	@Override
	public void dibujar(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.WHITE);
		g.fillOval((int) x - RADIO, (int) y - RADIO, DIAMETRO, DIAMETRO);
		/*
		 * g.drawLine((int) x, (int) y, (int) (x + Math.cos(angulo) * 500),
		 * (int) (y + Math.sin(angulo) * 500));
		 */
	}

	@Override
	public void actualizar() {
		// TODO Auto-generated method stub
		x += vex * vel;
		y += vey * vel;
		/*
		 * if (!this.esSerVivoNA()) if (vel > 0) vel = vel - 0.0001f; else vel =
		 * 0;
		 */

	}

	protected void girar(float valor) {
		if (valor == 0)
			return;
		if (valor < -1)
			valor = -1;
		if (valor > 1)
			valor = 1;
		angulo = (float) ((angulo + valor / 50) % (Math.PI * 2));
		vex = (float) Math.cos(angulo);
		vey = (float) Math.sin(angulo);
	}

	protected void acelerar(float valor) {
		if (valor < 0)
			valor = 0;
		if (valor > 1)
			valor = 1;
		valor = valor * VELOCIDAD_MAX;
		if (vel != valor) {
			if (vel < valor)
				vel += 0.01f;
			else
				vel -= 0.01f;
		}
		if (vel > VELOCIDAD_MAX)
			vel = VELOCIDAD_MAX;
		if (vel < VELOCIDAD_MIN)
			vel = VELOCIDAD_MIN;
	}

	protected float getVex() {
		return vex;
	}

	protected void setVex(float vex) {
		this.vex = vex;
	}

	protected float getVey() {
		return vey;
	}

	protected void setVey(float vey) {
		this.vey = vey;
	}

	protected float getVel() {
		return vel;
	}

	protected void setVel(float vel) {
		this.vel = vel;
	}

	protected void setX(float x) {
		this.x = x;
	}

	protected void setY(float y) {
		this.y = y;
	}

	protected void setAngulo(float a) {
		this.angulo = a;
	}

	protected int getAnchoMundo() {
		return this.ancho_mundo;
	}

	protected int getAltoMundo() {
		return this.alto_mundo;
	}

	protected void agregaMisilSiguiendo(Misil m) {
		return;
	}
}
