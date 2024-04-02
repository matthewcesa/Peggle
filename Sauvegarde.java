package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Obstacle;
import model.Player;

public class Sauvegarde {
   public static int joueur;
   public static int numNiveau = -1;
   public static ArrayList<Player> listeJoueurs = new ArrayList<Player>();

   public Sauvegarde() {
      try {
         FileInputStream fileIn = new FileInputStream("save.ser");
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
      if (a != null || n != 0) {
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
      try {
         FileInputStream fileIn = new FileInputStream("save.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         listeJoueurs = (ArrayList<Player>) in.readObject();
         in.close();
         fileIn.close();
      } catch (IOException i) {
         i.printStackTrace();
      } catch (ClassNotFoundException c) {
         c.printStackTrace();
      }
      if (n >= listeJoueurs.get(joueur).liste.size()) {
         ArrayList<Obstacle> a = new ArrayList<>();
         listeJoueurs.get(joueur).liste.add(a);
      }
      return listeJoueurs.get(joueur).liste.get(n);
   }
}
