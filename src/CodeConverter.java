/**
 *
 * @author Rob Logan - c3165020
 */

import java.net.*;
import java.io.*;

public class CodeConverter {
    private static final int DEFAULTPORT = 1234;
    private static final String READY = "ASCII: OK";
    
    public static void main(String args[]) throws IOException{
        
        if (args.length != 1) {
            System.err.println("Usage: java CodeConverter <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Starting server on port "+portNumber);
        
        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            String outputLine = "";
            String state = "CA";
            String tempState = "";
            inputLine = in.readLine();
            System.out.println("REQUEST: "+inputLine);
            System.out.println("RESPONSE: ASCII: OK");
            out.println("ASCII: OK");
            inputLine = in.readLine();
            while(true){
                switch(state){
                    case "AC":
                        while(inputLine!=null){
                            if(inputLine.equals("CA")||inputLine.equals("AC")){
                                state = "CA";
                                response(inputLine, "CHANGE: OK", out);
                                inputLine = in.readLine();
                                break;
                            }
                            int i = Integer.parseInt(inputLine);
                            if(isValid(i)){
                                outputLine = Character.toString((char)i);
                                response(inputLine,outputLine, out);
                                inputLine = in.readLine();   
                            }else{
                                response(inputLine, "RESPONSE: ERR", out);
                                inputLine = in.readLine();
                            }
                            
                            if(inputLine.equals("BYE")||inputLine.equals("END")){
                                if(inputLine.equals("BYE")){
                                    tempState = state;
                                }
                                state = inputLine;
                            }
                        }
                        break;
                    case "CA":
                        while(inputLine!=null){
                            if(inputLine.equals("CA")||inputLine.equals("AC")){
                                state = inputLine;
                                response(inputLine, "CHANGE: OK", out);
                                inputLine = in.readLine();
                                break;
                            }
                            char c = inputLine.charAt(0);
                            if(isValid((int)c)){
                                outputLine = Integer.toString((int)c);
                                response(inputLine,outputLine, out);
                                inputLine = in.readLine();    
                            }else{
                                response(inputLine, "RESPONSE: ERR", out);
                                inputLine = in.readLine();
                            }

                            if(inputLine.equals("BYE")||inputLine.equals("END")){
                                if(inputLine.equals("BYE")){
                                    tempState = state;
                                }
                                state = inputLine;
                            }
                        }
                        break;
                    case "BYE":
                        state = tempState;
                        response(inputLine, "BYE: OK", out);
                        inputLine = in.readLine();
                        break;
                    case "END":
                        System.out.println("REQUEST: "+inputLine);
                        System.out.println("RESPONSE: END: OK");
                        out.println("END: OK");
                        System.out.println("Server shuts down");
                        clientSocket.close();
                        System.exit(1);
                        break;
                }
                //break;
            }            
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
    public static void response(String in, String out, PrintWriter writer){
        System.out.println("REQUEST: "+in);
        System.out.println("RESPONSE: "+out);
        writer.println(out);//send a response back to the client
        System.out.println(READY);
        writer.println(READY);
    }
    public static boolean isValid(int ascii){
        boolean flag = false;
        if (ascii>47&&ascii<58 || ascii>64&&ascii<91 || ascii>96&&ascii<123){
            flag = true;
        }
        return flag;
    }
}
