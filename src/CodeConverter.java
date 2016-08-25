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
        boolean stateFlag = false;
        if (args.length != 1) {
            System.err.println("Usage: java CodeConverter <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Starting server on port "+portNumber+" in 'Char->ASCII' mode.");
        ServerSocket serverSocket = new ServerSocket(portNumber);
        
        while(true){
            try (    
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
            ) {
                while(true){
                    String inputLine = "";
                    String outputLine = "";
                    String state = "STARTUP";
                    while(true){
                        inputLine = in.readLine();
                        if(chkState(inputLine)){
                            state = inputLine;
                            stateFlag = true;
                        }else{
                            stateFlag = false;
                        }
                        switch(state){
                            case "AC":                  
                                if(stateFlag){
                                    response(inputLine, "CHANGE: OK", out);
                                    break;
                                }
                                try{
                                    int i = Integer.parseInt(inputLine);
                                    if(isValid(i)){
                                        outputLine = Character.toString((char)i);
                                        response(inputLine,outputLine, out);
                                    }else{
                                        response(inputLine, "RESPONSE: ERR", out);
                                    }   
                                }catch(NumberFormatException e){
                                    response(inputLine, "RESPONSE: ERR", out);
                                }
                                break;
                            case "CA":
                                if(stateFlag){
                                    response(inputLine, "CHANGE: OK", out);
                                    break;
                                }
                                char c = inputLine.charAt(0);
                                if(isValid((int)c)&&inputLine.length()==1){
                                    outputLine = Integer.toString((int)c);
                                    response(inputLine,outputLine, out);
                                }else{
                                    response(inputLine, "RESPONSE: ERR", out);
                                }
                                break;
                            case "BYE":
                                response(inputLine, "BYE: OK", out);
                                state = "STARTUP";
                                break;
                            case "END":
                                System.out.println("REQUEST: "+inputLine);
                                System.out.println("RESPONSE: END: OK");
                                out.println("END: OK");
                                System.out.println("Server shuts down");
                                System.exit(1);
                                break;
                            case "STARTUP":
                                System.out.println("REQUEST: "+inputLine);
                                System.out.println("RESPONSE: ASCII: OK");
                                out.println("ASCII: OK");
                                state = "CA";
                                break;  
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected on port "
                    + portNumber + ". Listening for a new connection");
            }
        } 
    }
    
    private static void response(String in, String out, PrintWriter writer){
        System.out.println("REQUEST: "+in);
        System.out.println("RESPONSE: "+out);
        writer.println(out);//send a response back to the client
        System.out.println(READY);
        writer.println(READY);
    }
    
    private static boolean chkState(String s){
        boolean stateFlag = false;
        if((s.equals("STARTUP"))||(s.equals("AC"))||(s.equals("CA"))||(s.equals("BYE"))||(s.equals("END"))){
            stateFlag = true;
        }
        return stateFlag;
    }
    
    private static boolean isValid(int ascii){
        boolean flag = false;
        if ((ascii>47&&ascii<58) || (ascii>64&&ascii<91) || (ascii>96&&ascii<123)){
            flag = true;
        }
        return flag;
    }
}
