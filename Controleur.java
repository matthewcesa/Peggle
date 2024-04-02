package controller;

import view.*;
import javax.swing.*;
import model.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controleur {

    public View view;
    public Modele modele;
    public double angleTir;
    public Timer timer;
    public double t;
    public boolean balleEnJeu;
    public int facteur;

    public Controleur() {
        this.balleEnJeu = false;
        modele = new Modele();
        new Image();
        new Sauvegarde();
        view = new View(this);
        facteur = 1;
        // --------------ANIMATION----------------------
        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // canon
                view.setColorX();
                view.setColorY();
                view.calculeAngle();
                if (View.enJeu) {
                    view.repaint();
                    view.munition.removeAll();
                    view.afficheMunition();
                    view.munition.revalidate();
                }
                // puit
                view.placePuit();
                if (modele.getBalle() != null) {
                    modele.getBalle().update();
                    // rebond
                    for (int i = 0; i < modele.niveau.list.size(); i++) {
// <<<<<<< HEAD
//                         // if (modele.niveau.list.get(i) instanceof Pegs) {
//                             if (modele.niveau.list.get(i).rebond(modele.getBalle())) {
//                                 view.bruitage("ressources/SonsWav/rebond.wav");
//                                 modele.niveau.list.get(i).perdDeLaVie(1);
//                                 boolean detruit = modele.niveau.list.get(i).getEstMort();
//                                 if (detruit) {
//                                     double x = modele.niveau.list.get(i).getX();
//                                     double y = modele.niveau.list.get(i).getY();
//                                     view.addExplosion(x, y);
//                                     modele.niveau.list.remove(i);
//                                 }
//                                 modele.player.calculScore(detruit, facteur++,balleEnJeu);
//                                 view.setScore();
//                             }
//                         // }
// =======
                        if(!modele.niveau.list.get(i).getEstMort()){
                            if (modele.niveau.list.get(i).rebond(modele.getBalle()) && modele.niveau.list.get(i) instanceof Pegs) {
                                view.bruitage("ressources/SonsWav/rebond.wav");
                                modele.niveau.list.get(i).perdDeLaVie(1);
                                modele.getNiveau().vieActuelTotale++;
                                boolean detruit = modele.niveau.list.get(i).getEstMort();
                                int point = modele.player.calculScore(detruit, facteur++,balleEnJeu);
                                if (detruit) {
                                    double x = modele.niveau.list.get(i).getX();
                                    double y = modele.niveau.list.get(i).getY();
                                    view.addExplosion(x, y,point);
                                    modele.niveau.list.remove(i);
                                }
                                view.setScore();
                            }
                        }                        
                    }
                    if (modele.getBalle().getX() - modele.getBalle().rayon / 2 <= 0
                            || modele.getBalle().getX() + modele.getBalle().rayon / 2 >= 800) {
                        modele.balle.rebondMur();
                        view.bruitage("ressources/SonsWav/rebond.wav");
                    }
                    // munition
                    if (View.enJeu) {
                        view.repaint();
                        view.munition.removeAll();
                        view.afficheMunition();
                        view.munition.revalidate();
                        if ( modele.getBalle().getX() * View.ratioY>= view.puit.getX() && modele.getBalle().getX() * View.ratioX <= view.puit.getX() + view.puit.getWidth()
                                && modele.getBalle().getY() * View.ratioY >= view.puit.getY()
                                && modele.getBalle().getY() * View.ratioY <= view.puit.getY() + view.puit.getHeight()) {
                            if (balleEnJeu) {
                                // view.addExplosion(modele.balle.x, modele.balle.x);
                                // view.addExplosion(xBalle, yBalle); //marche pas
                                modele.setBalle(null);
                                view.nbMunition++;
                                balleHorsJeu();
                            }
                        }
                        if (modele.getBalle()!= null && modele.getBalle().getY() > view.getPartie().getHeight()) {
                            modele.setBalle(null);
                            balleHorsJeu();
                        }
                    }

                    if(modele.niveau.NoMorePeg() && !balleEnJeu) {
                        if(view.nbMunition > 0) {
                            for(int i = 0 ; i < view.nbMunition ; i++) {
                                modele.getPlayer().score += 400; 
                                // s il n y a plus de pegs mais qu'il reste des munitions
                            } 
                        }
                        if(Sauvegarde.numNiveau == -1){
                            view.nextLevel();
                        }
                    }
                    if(view.nbMunition == 0 && !balleEnJeu) {
                        if(Sauvegarde.numNiveau == -1) {
                            view.nextLevel();
                        }
                    }
                }
            }
        });
        timer.start();
    }

    public void balleHorsJeu() {
        this.balleEnJeu = false;
        facteur = 1;
    }

    public void tirer() {
        if (!this.balleEnJeu) {
            view.nbMunition--;

            this.balleEnJeu = true;
            this.modele.setBalle(null);
            t = 0;
            this.angleTir = this.view.getAngle();
            this.modele.setBalle(new Balle(View.xBoutCanon/View.ratioX, View.yBoutCanon/View.ratioY, 500d, 180 - this.angleTir));
        }
    }

    public void finTour() {
        this.modele.setBalle(null);
        this.balleEnJeu = false;
        this.modele.niveau = new Niveau(this.modele.niveau.niveau + 1);
        view.partie.removeAll();
        view.changerPanel(view.choixNiveauPane(this));
        view.partie.revalidate();
    }

    // ---------GETTER SETTER---------
    public double getAngleTir() {
        return angleTir;
    }
    // ---------GETTER SETTER---------
}
