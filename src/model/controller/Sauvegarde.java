package model.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.model.Obstacle;
import model.model.Player;

public class Sauvegarde {
   public static int joueur;
   public static int numNiveau = -1;
   public static ArrayList<Player> listeJoueurs = new ArrayList<Player>();

   public Sauvegarde() {
      File sauvegarde = new File("save.ser");
      if (!sauvegarde.exists()) {
         return;
      }
      try {
         FileInputStream fileIn = new FileInputStream(sauvegarde);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         listeJoueurs = (ArrayList<Player>) in.readObject();
         in.close();
         fileIn.close();
      } catch (IOException i) {
         i.printStackTrace();
      } catch (ClassNotFoundException c) {
         c.printStackTrace();
      }
   }

   public static void save(ArrayList<Obstacle> a, int n) {
      while (listeJoueurs.size() <= joueur) {
         listeJoueurs.add(new Player("Nouveau"));
      }
      while (listeJoueurs.get(joueur).liste.size() <= n) {
         listeJoueurs.get(joueur).liste.add(new ArrayList<Obstacle>());
      }
      if (a != null) {
         listeJoueurs.get(joueur).liste.set(n, a);
      }
      try {
         FileOutputStream fileOut = new FileOutputStream("save.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(listeJoueurs);
         out.close();
         fileOut.close();
         System.out.println("Sauvegarde effectué");
      } catch (IOException i) {
         i.printStackTrace();
      }
   }

   public static void save(Player p) {
      if (p != null) {
         while (listeJoueurs.size() <= joueur) {
            listeJoueurs.add(new Player("Nouveau"));
         }
         listeJoueurs.set(joueur, p);
      }
      if(p != null && p.liste.size() == 0){
         ArrayList<Obstacle> a = new ArrayList<>();
         p.liste.add(a);
      }
      try {
         FileOutputStream fileOut = new FileOutputStream("save.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(listeJoueurs);
         out.close();
         fileOut.close();
         System.out.println("Sauvegarde effectué");
      } catch (IOException i) {
         i.printStackTrace();
      }
      /*
       * for (Player player : listeJoueurs) {
       * System.out.println(player.getPseudo());
       * }
       */

   }

   public static ArrayList<Obstacle> charge(int n) {
      numNiveau = n;
      File sauvegarde = new File("save.ser");
      if (sauvegarde.exists()) {
         try {
            FileInputStream fileIn = new FileInputStream(sauvegarde);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            listeJoueurs = (ArrayList<Player>) in.readObject();
            in.close();
            fileIn.close();
         } catch (IOException i) {
            i.printStackTrace();
         } catch (ClassNotFoundException c) {
            c.printStackTrace();
         }
      }
      while (listeJoueurs.size() <= joueur) {
         listeJoueurs.add(new Player("Nouveau"));
      }
      if (n >= listeJoueurs.get(joueur).liste.size()) {
         ArrayList<Obstacle> a = new ArrayList<>();
         listeJoueurs.get(joueur).liste.add(a);
      }
      return listeJoueurs.get(joueur).liste.get(n);
   }
}
