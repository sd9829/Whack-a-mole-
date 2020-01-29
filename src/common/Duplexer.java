package common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Quick code to help with server
 * @author Kelsey Donovan
 * username: ksd4250
 * @author: Soumya Dayal
 * username: sd9829
 */


public class Duplexer implements AutoCloseable {

    private Scanner scan;
    private PrintWriter writer;

    public Duplexer(Socket sock) throws IOException{
        this.scan = new Scanner(sock.getInputStream());
        writer = new PrintWriter(sock.getOutputStream());
    }

    public void sendMess(String mess) {
        this.writer.println(mess);
        writer.flush();
    }

    public String getMess() {
        return this.scan.nextLine();
    }

    @Override
    public void close() {
        writer.close();
        scan.close();
    }


}
