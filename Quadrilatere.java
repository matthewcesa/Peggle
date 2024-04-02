package model;
import java.awt.*;
import view.Image;

import view.View;
public class Quadrilatere extends Obstacle {//peut etre un carré comme un rectangle
    int typeCollision = 0;
    public Point coinHautGauche, coinBasGauche, coinHautDroit, coinBasDroit;
    public Quadrilatere(double x, double y, double largeur, double hauteur) {
        super(x, y, largeur, hauteur, false, 100);
        coinHautGauche = new Point((int) x, (int) y);
        coinHautDroit = new Point((int) ((x + largeur)), (int) y);
        coinBasGauche = new Point((int) x, (int) (y + hauteur));
        coinBasDroit = new Point((int) (x + largeur), (int) (y + hauteur));

        if(largeur>=hauteur){
            this.image=Image.quadHorizontal;
        }
        else{
            this.image=Image.quadVertical;
        }
    }

    @Override
    public boolean collision(Balle balle) {
    if ((balle.y >= this.y && balle.y <= (this.y + this.hauteur))
        && (((balle.x + balle.rayon) >= this.x && balle.x<= this.x) || ((balle.x - balle.rayon)<= (this.x + this.largeur) && balle.x>=(this.x + this.largeur) ))) {
      //System.out.println("Collision quadrilatere 1");
      typeCollision = 1;
      return true;
    } 
    else if ((balle.x>= this.x && balle.x<= (this.x + this.largeur))
        && (((balle.y + balle.rayon) >= this.y && balle.y <= this.y) || ((balle.y - balle.rayon)<= (this.y + this.hauteur) && balle.y >= (this.y + this.hauteur)))) {
      //System.out.println("Collision quadrilatere 2");
      typeCollision = 2;
      return true;
    }

    else if(balle.rayon>= Math.sqrt((balle.x+balle.rayon/2-this.coinHautGauche.x)*(balle.x+balle.rayon/2-this.coinHautGauche.x) + (balle.y+balle.rayon/2 - this.coinHautGauche.y)*(balle.y+balle.rayon/2 - this.coinHautGauche.y))){
      //collision coin haut gauche
      //System.out.println("Collision quadrilatere 3");
      typeCollision = 3;
      return true;
    }
    else if(balle.rayon>= Math.sqrt((balle.x+balle.rayon/2 - this.coinHautDroit.x)*(balle.x+balle.rayon/2 - this.coinHautDroit.x) + (balle.y+balle.rayon/2 - this.coinHautDroit.y)*(balle.y+balle.rayon/2 - this.coinHautDroit.y))){
      //collision coin haut droit
      //System.out.println("Collision quadrilatere 4");
      typeCollision = 4;
      return true;
    }
    else if(balle.rayon>= Math.sqrt((balle.x+balle.rayon/2 - this.coinBasGauche.x)*(balle.x+balle.rayon/2 - this.coinBasGauche.x) + (balle.y+balle.rayon/2 - this.coinBasGauche.y)*(balle.y+balle.rayon/2 - this.coinBasGauche.y))){
      //collision coin bas gauche
      //System.out.println("Collision quadrilatere 5");
      typeCollision = 5;
      return true;
    }
    else if(balle.rayon>= Math.sqrt((balle.x+balle.rayon/2 - this.coinBasDroit.x)*(balle.x+balle.rayon/2-this.coinBasDroit.x) + (balle.y+balle.rayon/2 - this.coinBasDroit.y)*(balle.y+balle.rayon/2-this.coinBasDroit.y))){
      //collision coin bas droit
      //System.out.println("Collision quadrilatere 6");
      typeCollision = 6;
      return true;
    }
    return false;

    }

    @Override
    public boolean rebond(Balle balle) {
        if (collision(balle)) {
            switch (this.typeCollision) {
                case 1:
                  balle.vX = balle.x > this.x ? Math.abs(balle.vX * -0.95) : -Math.abs(balle.vX * -0.95);
                  break;
                case 2:
                  //balle.vY = balle.vY * -0.95;
                  balle.vY = balle.y > this.y ? Math.abs(balle.vY * -0.95) : -Math.abs(balle.vY * -0.95);
                  break;
                case 3:
                    coin(this.coinHautGauche,balle);
                  break;
                case 4:
                    coin(this.coinHautDroit,balle);
                  break;
                case 5:
                    coin(this.coinBasGauche,balle);
                  break;
                case 6:
                    coin(this.coinBasDroit,balle);
                  break;
              } 
            return true;
        }
      return false;
    }

    public void coin(Point point,Balle balle){
        double n1 = balle.vX; // Variable auxiliaire pour garder balle.vX avant qu'on modifie sa valeur
        double newX = balle.vX - (2 * (balle.vX * (balle.x - point.x) + balle.vY * (balle.y - point.y)))
        / (((balle.x - point.x)) * ((balle.x - point.x))+ ((balle.y - point.y)) * ((balle.y - point.y)))
        * ((balle.x - point.x)) * 0.95;

        double newY = balle.vY - (2 * (n1 * (balle.x - point.x) + balle.vY * (balle.y - point.y)))
        / (((balle.x - point.x)) * ((balle.x - point.x)) + ((balle.y - point.y)) * ((balle.y - point.y)))
        * ((balle.y - point.y)) * 0.95;

        balle.vX = balle.x > this.x ? Math.abs(newX) : -Math.abs(newX);
        balle.vY = balle.y > this.y ? Math.abs(newY) : -Math.abs(newY);
    }

    public void perdDeLaVie(int degats) {
    }
    @Override
    public void dessine(Graphics g){
        int gx=(int)(this.x*View.ratioX);
        int gy=(int)(this.y*View.ratioY);
        int gw=(int)(this.largeur*View.ratioX);
        int gh=(int)(this.hauteur*View.ratioY);
        if(image == null){
            g.fillRect(gx,gy,gw,gh);
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(this.image,gx, gy,gw,gh,null);
    }

    @Override
    public Quadrilatere clone(double x, double y, int v, double largeur,double hauteur){
        Quadrilatere q = new Quadrilatere(x, y, largeur, hauteur);
        q.image = null;
        q.coinHautGauche = new Point((int) (this.x), (int) (this.y));
        q.coinHautDroit = new Point((int) ((this.x+this.largeur)), (int) (this.y));
        q.coinBasGauche = new Point((int) (this.x), (int) ((this.y + this.hauteur)));
        q.coinBasDroit = new Point((int) ((this.x + this.largeur)), (int) ((this.y + this.hauteur)));
        return q;
    }

    @Override
    public boolean utiliseRayon(){
        return false;
    }

}
