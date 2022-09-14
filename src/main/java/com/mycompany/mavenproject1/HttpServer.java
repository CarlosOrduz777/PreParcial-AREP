package com.mycompany.mavenproject1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author carlos.orduz
 */
public class HttpServer {
     public static void main(String[] args) throws IOException {
   ServerSocket serverSocket = null;
   try { 
      serverSocket = new ServerSocket(HttpServer.getPort());
   } catch (IOException e) {
      System.err.println("Could not listen on port: 35000.");
      System.exit(1);
   }
   boolean running = true;
   while(running){
       Socket clientSocket = null;
   try {
       System.out.println("Listo para recibir ...");
       clientSocket = serverSocket.accept();
   } catch (IOException e) {
       System.err.println("Accept failed.");
       System.exit(1);
   }
   PrintWriter out = new PrintWriter(
                         clientSocket.getOutputStream(), true);
   BufferedReader in = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()));
   String inputLine, outputLine;
   boolean isFirstLine = true;
   String path = "";
   String city = "";
   StringBuffer body = new StringBuffer();
   while ((inputLine = in.readLine()) != null) {
      System.out.println("Recibí: " + inputLine);
      if(isFirstLine){
            String[] receive = inputLine.split(" ");
            path = receive[1];
            if(path.startsWith("/consulta")){
                if(path.contains("?")){
                    city = path.split("=")[1];
                }else{
                    city = "London";
                }
                body = HttpConnectionExample.getApi(city);
            } else if (path.startsWith("/clima")) {
                body = getForm();
            }else {
                System.out.printf("Error");
            }
          System.out.printf(path);
            isFirstLine = false;
      }

      if (!in.ready()) {break; }
   }





       outputLine = "HTTP/1.1 200 OK\r\n"
               + "Content-type: text/html\r\n"
               + "\r\n"
               + body;
    out.println(outputLine);
    out.close(); 
    in.close(); 
    clientSocket.close(); 
   }
    serverSocket.close(); 
  }
     public static StringBuffer getForm(){
        return new StringBuffer("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Form Example</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Form with GET</h1>\n" +
                "<form action=\"/clima\">\n" +
                "    <label for=\"name\">Name:</label><br>\n" +
                "    <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n" +
                "    <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "</form>\n" +
                "<div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "<script>\n" +
                "            function loadGetMsg() {\n" +
                "                let city = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/consulta?lugar=\"+city);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
     }
   public static int getPort(){
       if(System.getenv("PORT")!=null){
           return new Integer(System.getenv("PORT"));
       }
       return 4567;
   }
}
