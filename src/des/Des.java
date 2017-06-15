/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maciek
 */
public class Des {
    static String plikZKluczem = "..\\klucz.txt";
    static String plikZWiadomoscia = "..\\wiadomosc.txt";
    static String plikZSzyfrogramem = "..\\szyfrogram.txt";
    static String plikZOdszyfrowanym = "..\\odszyfrowana.txt";
    static boolean szyfrowanie = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        c("Program implementujący algorytm szyfrowania / deszyfrowania DES:");
        c("czy chcesz szyfrować czy odszyfrować?");
        c("wciśnij: ");
        c("s dla szyfrowania, lub");
        c("o dla odszyfrowania i potwierdź wciskając ENTER.");
        String czySzyfrowanie = "";
        while(!"o".equals(czySzyfrowanie)&&!"s".equals(czySzyfrowanie))
        czySzyfrowanie = System.console().readLine();
        if(czySzyfrowanie.equals("s")) szyfrowanie = true;
        else if(czySzyfrowanie.equals("o")) szyfrowanie = false;
        
        Permutacje.init();
        Klucz.init();
        String wiadomosc="", szyfrogram="";
        c("");
        c("Podaj wiadomość do zaszyfrowania: ");
//        wiadomosc = System.console().readLine();
        if(szyfrowanie) 
        {
            wiadomosc = wczytajZPliku(plikZWiadomoscia);
//            zapiszDoPliku(wiadomosc, plikZWiadomoscia);
//            c(wiadomosc);
//            pause();
        }
        else wiadomosc = wczytajZPliku(plikZSzyfrogramem);
        
        if(!szyfrowanie)
        {
            c("kryptogram:");
            c(System.lineSeparator()+wiadomosc);
        }
        
        byte[] wiadomoscB = null;
        if(szyfrowanie)
        {
            c("wiadomosc w bajtach: ");
            wiadomoscB = wiadomosc.getBytes();
        }
        else
        {
            List<Integer> kryptogramIntList = new ArrayList<>();
            for(int i =0; i<wiadomosc.length(); i++)
            {
                int a = wiadomosc.charAt(i);
                kryptogramIntList.add(a); 
            }
            c("lista liczb w kryptogramie z pliku:");
            wypiszListe(kryptogramIntList);

            int [] kryptogramInts = new int[kryptogramIntList.size()];
            for(int i =0; i < kryptogramIntList.size(); i++)
            {
                kryptogramInts[i] = kryptogramIntList.get(i);
            }
            c("");
            c(Arrays.toString(kryptogramInts));
            wiadomoscB = new byte[kryptogramInts.length];
            for(int i = 0; i<kryptogramInts.length; i++)
            {
                wiadomoscB[i] = (byte) kryptogramInts[i];
            }
            c("");
            c(Arrays.toString(wiadomoscB));
        }
        for(byte b: wiadomoscB)
        {
            cl(Integer.toHexString(b)+" ");
        }
        if(szyfrowanie) c("wiadomosc:"); else c("szyfrogram:");
        c(wiadomosc);
        pause();
        c("");
        List<Boolean> wiadomoscAL = new ArrayList<>(tablicaBajtowToList(wiadomoscB));
        c("lista bitów wiadomości: ");
        wypiszListeBitow(wiadomoscAL);

        //rozdzielenie bitow wiadomosci na 64 bitowe listy
        List<List<Boolean>> wiadomoscAL64 = new ArrayList<>();
        for(int i =0; i<wiadomoscAL.size(); i++)
        {
            if(i%64==0) wiadomoscAL64.add(new ArrayList<>());
            wiadomoscAL64.get(wiadomoscAL64.size()-1).add(wiadomoscAL.get(i));
        }
        //uzupelnienie zerami ostatniej wiadomosci
        while(wiadomoscAL64.get(wiadomoscAL64.size()-1).size()!=64)
        {
            wiadomoscAL64.get(wiadomoscAL64.size()-1).add(Boolean.FALSE);
        }
        
        c(System.lineSeparator()+"lista 64 bitowych list bitów wiadomości: ");
        for(List<Boolean> l : wiadomoscAL64)
        {
            wypiszListeBitow(l);
        }
        
        List<Boolean> szyfrogramList = new ArrayList<>();
        for(List<Boolean> l: wiadomoscAL64)
        {
            List<List<Boolean>> listaLewychWiadomosci = new ArrayList<>();
            List<List<Boolean>> listaPrawychWiadomosci = new ArrayList<>();
            
            List<Boolean> IP = new ArrayList<>(permutacja(l, Permutacje.IP));
            c(System.lineSeparator()+"IP:");
            wypiszListeBitow(IP);
            List<Boolean> lewaM = new ArrayList<>(IP.subList(0, (int) (IP.size()*0.5)));
            List<Boolean> prawaM = new ArrayList<>(IP.subList((int) (IP.size()*0.5), IP.size()));
            listaLewychWiadomosci.add(lewaM);
            listaPrawychWiadomosci.add(prawaM);
            c(System.lineSeparator()+"lewa część wiadomości:");
            wypiszListeBitow(lewaM);
            c(System.lineSeparator()+"prawa część wiadomości:");
            wypiszListeBitow(prawaM);
            for(int i=0; i<16; i++)
            {
                listaLewychWiadomosci.add(new ArrayList<>(listaPrawychWiadomosci.get(i)));
                List<Boolean> prawaMRozszerzony48 = new ArrayList<>(permutacja(listaPrawychWiadomosci.get(i), Permutacje.E));
                c(System.lineSeparator()+"prawy blok rozszerzony do 48 bitów: ");
                wypiszListeBitow(prawaMRozszerzony48);
                c("");
                List<Boolean> prawaXORklucz;
                if(szyfrowanie) 
                {
                    prawaXORklucz = new ArrayList<>(xorList(prawaMRozszerzony48, Klucz.podklucze48bitowe.get(i+1)));
                }
                else
                {
                    prawaXORklucz = new ArrayList<>(xorList(prawaMRozszerzony48, Klucz.podklucze48bitowe.get(16-i)));                    
                }
                c("podklucz:");
                if(szyfrowanie) wypiszListeBitow(Klucz.podklucze48bitowe.get(i+1));
                else wypiszListeBitow(Klucz.podklucze48bitowe.get(16-i));
                c(System.lineSeparator()+"xor:");
                wypiszListeBitow(prawaXORklucz);
                List<List<Boolean>> listaSzesciobitowychCzesci = new ArrayList<>();
                for(int k =0; k<prawaXORklucz.size(); k++)
                {
                    if(k%6==0) listaSzesciobitowychCzesci.add(new ArrayList<>());
                    listaSzesciobitowychCzesci.get(listaSzesciobitowychCzesci.size()-1).add(prawaXORklucz.get(k));
                }
                c(System.lineSeparator()+"xor podzielony na 6bitowe czesci:");
                List<Boolean> transformacjaSBoxa = new ArrayList<>();
                for(int k = 0; k < listaSzesciobitowychCzesci.size(); k++)
                {
                    wypiszListeBitow(listaSzesciobitowychCzesci.get(k));
                    int wspX = 0, wspY = 0;
                    wspY = 2*(listaSzesciobitowychCzesci.get(k).get(0)? 1 : 0) + 1*(listaSzesciobitowychCzesci.get(k).get(5)? 1 : 0);
                    c(System.lineSeparator()+"wiersz: " + wspY);
                    wspX = 8*(listaSzesciobitowychCzesci.get(k).get(1)? 1 : 0) + 4*(listaSzesciobitowychCzesci.get(k).get(2)? 1 : 0) + 2*(listaSzesciobitowychCzesci.get(k).get(3)? 1 : 0) + 1*(listaSzesciobitowychCzesci.get(k).get(4)? 1 : 0);
                    c("kolumna: " + wspX);
                    short podst = (short) Permutacje.listaSBoxow.get(k)[wspY][wspX];
                    c("wartosc w s boxie:"+podst);
                    char podstchar= (char) podst;
                    String podstS =""+ podstchar;
                    c("String podst:"+podstS);
                    c("wartosc jako bity:"+ Integer.toBinaryString(podst));
                    byte[] podstBT = podstS.getBytes();
                    byte podstB = podstBT[0];
                    c(podstB);
                    for (int j = 0; j<4; j++)
                    {
                        transformacjaSBoxa.add(isBitSet(podstB, 3-j));
                    }
                }
                wypiszListeBitow(transformacjaSBoxa);
                List<Boolean> f = new  ArrayList<>(permutacja(transformacjaSBoxa, Permutacje.P));
                c("po permutacji P:");
                wypiszListeBitow(f);
                c("listalewych.get(i):");
                wypiszListeBitow(listaLewychWiadomosci.get(i));
                listaPrawychWiadomosci.add(new ArrayList<>(xorList(listaLewychWiadomosci.get(i), f)));
                c("prawa wiad" + i+1);
                wypiszListeBitow(listaPrawychWiadomosci.get(i+1));
            }
            c("lista lewych wiadomosci:");
            for(List k: listaLewychWiadomosci)
            {
                wypiszListeBitow(k);
            }
            c("lista prawych wiadomosci:");
            for(List k: listaPrawychWiadomosci)
            {
                wypiszListeBitow(k);
            }
            List<Boolean> RL = new ArrayList<>();
            RL.addAll(listaPrawychWiadomosci.get(16));
            RL.addAll(listaLewychWiadomosci.get(16));
            c("RL:");
            wypiszListeBitow(RL);
            List<Boolean> IP1 = new ArrayList<>(permutacja(RL, Permutacje.IP1));
            szyfrogramList.addAll(IP1);
        }
        c("szyfrogram:");
        wypiszListeBitow(szyfrogramList);
        /////do tego miejsca jest dobrze
        List<Integer> szyfrogramInt = new ArrayList<>();
        char znak = 0;
        for(int i = 0; i<szyfrogramList.size()-7; i+=8)
        {
            c(System.lineSeparator()+"i: "+i);
            int potegaDwojki = 1;
            for(int j = 7; j>=0; j--)
            {
                if(szyfrogramList.get(j+i)) znak += potegaDwojki;
                potegaDwojki *=2;
                c("wartość: \t" + Integer.toBinaryString(znak));
            }
            
            c("wartość:" + Integer.toString(znak));
            szyfrogram+=znak;
            int znakJakoLiczba = znak;
            szyfrogramInt.add(znakJakoLiczba);
            znak = 0;
        }
        if(szyfrowanie) c("szyfrogram:"); else c("jawny tekst:");
        c(szyfrogram);
        c("lista liczb w szyfrogramie:");
        wypiszListe(szyfrogramInt);
        if(szyfrowanie) zapiszDoPliku(szyfrogram, plikZSzyfrogramem);
        else zapiszDoPliku(szyfrogram, plikZOdszyfrowanym);        

//        leweBity = bityL.toByteArray()[0];
//        c(Integer.toBinaryString(leweBity));
//        c(Arrays.toString(wiadomoscB));
//        wiadomosc= new StringBuilder(wiadomosc).reverse().toString();
//        c(wiadomosc);
    }

    public static void zapiszDoPliku(String s, String nazwaPliku)
    {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nazwaPliku), "UTF-8"))) {
            out.write(s);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Des.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Des.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Des.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String wczytajZPliku(String nazwaPliku)
    {
            String res = "";   
            String str = "";
            try 
            {
                File fileDir = new File(nazwaPliku);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"))) 
                {
                    while ((str = in.readLine()) != null) 
                    {
                        res += str;
                        res += (char ) 13;
                    }
                }
                res = res.substring(0, res.length()-1);
	    }
	    catch (UnsupportedEncodingException e)
	    {
                System.out.println(e.getMessage());
	    }
	    catch (IOException e)
	    {
                System.out.println(e.getMessage());
	    }
	    catch (Exception e)
	    {
                System.out.println(e.getMessage());
            }
        return res;
    }
    
    static void c(Object o)
    {
        System.out.println(o);
    }
    
    static void cl(Object o)
    {
        System.out.print(o);
    }
    
    private static Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }    
    private static String boolToString(Boolean b)
    {
        if(b) return "1";
        else return "0";
    }
    static void wypiszListeBitow(List<Boolean> listaBitow)
    {
        c("");
        for(Boolean b : listaBitow)
        {
            cl(boolToString(b));
        }
    }
    static List<Boolean> permutacja(List<Boolean> lista, int[] tablica) 
    {
        List<Boolean> res = new ArrayList<>();
        for(int j = 0; j<tablica.length; j++)
        {
            res.add(lista.get(tablica[j]-1));
        }
        return res;
    }
    
    static void wypiszListe(List lista)
    {
        c("");
        for(Object o : lista)
        {
            cl(o+" ");
        }
    }

    private static List<Boolean> xorList(List<Boolean> prawaMRozszerzony48, List<Boolean> podklucz) 
    {
        List<Boolean> res = new ArrayList<>();
        for(int i = 0; i<prawaMRozszerzony48.size(); i++)
        {
            res.add(prawaMRozszerzony48.get(i)^podklucz.get(i));
        }
        return res;
    }

    private static List<Integer> stringToIntList(String s)
    {
        List<Integer> res = new ArrayList<>();
        for(int i =0; i<s.length(); i++)
        {
            int a = s.charAt(i);
            res.add(a); 
        }
        return res;
    }
    
    static void pause()
    {
        String s = System.console().readLine();
    }
    
    static List<Boolean> tablicaBajtowToList(byte [] tab)
    {
        List<Boolean> res = new ArrayList<>();
        for(int i =0; i<tab.length; i++)
        {
            for (int j = 0; j<8; j++)
            {
                res.add(isBitSet(tab[i], 7-j));
            }
        }
        return res;
    }
}
