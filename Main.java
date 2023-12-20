import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.*;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

public class Main{
    public static void main(String[] args) {



        ksiazka ksiazka1=new ksiazka();
        status stat=new status();
        String stingip="";
        InetAddress localip;
        int port = 4211;
        try {
            localip=InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        stingip= String.valueOf(localip);
        int substringip=stingip.lastIndexOf('/')+1;
        String ips=stingip.substring(substringip,stingip.length());

        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        JPanel mp=new JPanel();
        mp.setLayout(new BoxLayout(mp,BoxLayout.Y_AXIS));

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("IP to: ");
        top.add(label);
        JLabel labelip = new JLabel(ips);
        JLabel statt=new JLabel("Status Ksiażki: "+stat.getStatus());
        JLabel czytell=new JLabel("Czytelnoków: 0");
        JLabel pisarzyy=new JLabel("Pisarzy: 0");
        top.add(labelip);
        top.add(statt);
        top.add(czytell);
        top.add(pisarzyy);
        mp.add(top);

        JPanel mid=new JPanel();
        mid.setLayout(new FlowLayout(FlowLayout.CENTER));
        JTextArea area=new JTextArea();
        area.setPreferredSize(new Dimension(300,300));
        mid.add(area);
        mp.add(mid);

        JButton b1 = new JButton("get text");

        ExecutorService settextksiazka=Executors.newCachedThreadPool();
        ExecutorService gettextksiazka=Executors.newCachedThreadPool();


        ExecutorService exes2=Executors.newCachedThreadPool();

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exes2.submit(()->{
                    if(stat.getStatus()=="free") {
                        stat.setStatus("pisarz pisze");
                        Future<?> future = gettextksiazka.submit(() -> {
                            pisarzyy.setText("pisarzy: "+ksiazka1.getPisarze());
                            area.setText(ksiazka1.read());
                            stat.setStatus("free");
                            pisarzyy.setText("pisarzy: "+ksiazka1.getPisarze());
                        });
                        try {
                            future.get();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        } catch (ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else{
                        statt.setText("Status Ksiażki: "+stat.getStatus());
                    }
                });
            }
        });
        JButton b2 = new JButton("Save text");
        ExecutorService exes1=Executors.newCachedThreadPool();
        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exes1.submit(() -> {

                    if (stat.getStatus() == "free") {
                        stat.setStatus("Czyta");
                        Future<?> future = settextksiazka.submit(() -> {
                            czytell.setText("Czytelników "+ksiazka1.getCzytelnicy());
                            ksiazka1.write(area.getText());
                            stat.setStatus("free");
                            czytell.setText("Czytelników "+ksiazka1.getCzytelnicy());
                        });

                        try {
                            future.get();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        } catch (ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        statt.setText("Status Ksiażki: " + stat.getStatus());
                    }

                });
            }
        });
        JPanel botpanel = new JPanel();
        botpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        botpanel.add(b1);
        botpanel.add(b2);
        mp.add(botpanel);

        frame.add(mp);
        frame.pack();
        frame.setVisible(true);

        ExecutorService wypisstatusu=Executors.newCachedThreadPool();
        wypisstatusu.submit(()->{
            pisarzyy.setText("pisarzy: "+ksiazka1.getPisarze());
        });

        ExecutorService serwer=Executors.newCachedThreadPool();
        serwer.submit(()->{

            try (ServerSocket ssocket=new ServerSocket(port)){
                while (true){
                    Socket socket=ssocket.accept();
                    try (PrintWriter out=new PrintWriter(socket.getOutputStream(),true)){
                        out.println(ksiazka1.read());
                    }
                    catch (IOException e){
                        System.out.printf("serwer sending error");
                    }
                }
            }
            catch (IOException e){
                System.out.printf("serwer error");
            }
            System.out.printf("connection close");
            serwer.shutdown();
        });

    }


}
