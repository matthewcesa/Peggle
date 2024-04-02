package model;

import java.io.Serializable;

public abstract class Objet implements Serializable{

  protected double x;
  protected double y;
  protected double largeur;
  protected double hauteur;
  protected double angle;

  public Objet(double x, double y, double largeur, double hauteur, double angle) {
    this.x = x;
    this.y = y;
    this.largeur = largeur;
    this.hauteur = hauteur;
    this.angle = angle;
  }
  public Objet() {
    this.x =0;
    this.y =0;
    this.largeur =0;
    this.hauteur =0;
    this.angle =0;
  }

  // ---------GETTER SETTER---------
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getLargeur() {
    return largeur;
  }

  public double getHauteur() {
    return hauteur;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public void setLargeur(double l){
    this.largeur = l;
  }

  public void setHauteur(double h){
    this.hauteur = h;
  }

  // ---------GETTER SETTER---------
}
