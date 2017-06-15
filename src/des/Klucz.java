/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des;

import static des.Des.*;
import java.util.ArrayList;
import java.util.List;
import static des.Permutacje.*;

/**
 *
 * @author maciek
 */
public class Klucz {
    static String klucz="";
    static ArrayList<List<Boolean>> podklucze48bitowe = new ArrayList<>();

    static void init()
    {
        klucz = wczytajZPliku(Des.plikZKluczem);
        c("Klucz to: " + klucz);
        byte[] kluczB = klucz.getBytes();
        wypisywanie :
        {
            c("klucz w bajtach to:");
            for(byte b: kluczB)
            {
                cl(Integer.toBinaryString(b));
                cl(" ");
            }        c("");
            for(byte b: kluczB)
            {
                cl(Integer.toHexString(b));
                cl(" ");
            }
            c("");
            c("osiem bajtów klucza to: ");
            for(int i =0; i<8; i++)
            {
                cl(Integer.toBinaryString(kluczB[i]));
                cl(" ");
            }
            c(System.lineSeparator()+"osiem bajtów klucza w hex to: ");
            for(int i =0; i<8; i++)
            {
                cl(Integer.toHexString(kluczB[i]));
                cl(" ");
            }
        }
        List<Boolean> kluczList = new ArrayList<>(tablicaBajtowToList(kluczB));
        c("klucz jako lista bitow:");
        wypiszListeBitow(kluczList);
        List<Boolean> Kplus = new ArrayList<>(permutacja(kluczList, Permutacje.PC1));
        c(""); c("klucz po permutacji IP-1 - K+:");
        wypiszListeBitow(Kplus);
        ArrayList<List<Boolean>> lewePodklucze = new ArrayList<>();
        ArrayList<List<Boolean>> prawePodklucze = new ArrayList<>();
        lewePodklucze.add(Kplus.subList(0, (int) (Kplus.size()*0.5)));
        prawePodklucze.add(Kplus.subList((int) (Kplus.size()*0.5), Kplus.size()));
        wypiszListeBitow(lewePodklucze.get(0));
        wypiszListeBitow(prawePodklucze.get(0));
        for(int i = 0; i<przesunieciaPodkluczy.length; i++)
        {
            List<Boolean> bity = new ArrayList<>(lewePodklucze.get(lewePodklucze.size()-1));
            List<Boolean> koniec = new ArrayList<>();
            for(int j=0; j<przesunieciaPodkluczy[i]; j++)
            {
                koniec.add(bity.remove(0));
            }
            bity.addAll(koniec);
            lewePodklucze.add(bity);
            bity = new ArrayList<>(prawePodklucze.get(prawePodklucze.size()-1));
//            bity = lewePodklucze.get(lewePodklucze.size()-1);
            koniec.clear();
            for(int j=0; j<przesunieciaPodkluczy[i]; j++)
            {
                koniec.add(bity.remove(0));
            }
            bity.addAll(koniec);
            prawePodklucze.add(bity);
        }            
        ArrayList<List<Boolean>> podkluczeZlaczone = new ArrayList<>();
        for(int i =0; i<lewePodklucze.size(); i++)
        {
            podkluczeZlaczone.add(new ArrayList<>());
            podkluczeZlaczone.get(i).addAll(lewePodklucze.get(i));
            podkluczeZlaczone.get(i).addAll(prawePodklucze.get(i));
        }        
        c(System.lineSeparator() + "podklucze złączone po sześć bitów w ośmiu bajtach:");
        for(List l : podkluczeZlaczone)
        {
            wypiszListeBitow(l);
        }
        for(List l : podkluczeZlaczone)
        {
            podklucze48bitowe.add(permutacja(l, PC2));
        }
        c(System.lineSeparator()+"podklucze po permutacji PC2: ");
        for(List l : podklucze48bitowe)
        {
            wypiszListeBitow(l);
        }
    }
}
