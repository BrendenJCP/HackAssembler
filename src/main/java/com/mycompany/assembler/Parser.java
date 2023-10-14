package com.mycompany.assembler;
import java.io.*;

//Need to not accept some strings
public class Parser {
    BufferedReader reader;
    String command = null;

    public Parser(File program) {
        try {
            reader = new BufferedReader(new FileReader(program));
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public boolean hasMoreCommands() {
        return command != null;
    }

    public boolean isBlankLine() {
        return command.length() == 0;
    }

    public boolean skipLine() {
        if (isBlankLine()) {
            return true;
        }
        return command.charAt(0) == '/' && command.charAt(1) == '/';
    }
    
    public boolean validCommand() {
        switch(commandType()){
            case "A_COMMAND" -> {
            }
            case "L_COMMAND" -> {
                if(command.charAt(command.length()-1) != ')')
                    return false;
                else
                    return true;
            }
            //Set difference??
            case "C_COMMAND" -> {
                if(command.split(";").length == 1 && command.split("=").length == 1)
                    return false;
                else
                    return true;
            } 
            default -> {
                return false;
            }
        }
        return false;
    }

    public void advance() {
        try {
            command = reader.readLine();
            while (hasMoreCommands() && skipLine()) {
                command = reader.readLine();
            }
            if(hasMoreCommands()){
                command = command.replaceAll(" ", "");
                command = command.split("//")[0];
            }
        } catch (IOException e) {
        }
    }

    public String commandType() {
        return switch(command.charAt(0)){
            case '@' -> "A_COMMAND";
            case '(' -> "L_COMMAND";
            default -> "C_COMMAND";
        };
    }

    public String symbol() {
        return switch(commandType()){
            case "A_COMMAND" -> command.replaceAll("@", "");
            case "L_COMMAND" -> command.replaceAll("[()]", "");
            default -> null;
        };
    }
    
    public String dest(){
        String[] splitEq = command.split("=");
        if(!commandType().equals("C_COMMAND"))
            return null;
        if(splitEq.length == 1)
            return "";
        else 
            return splitEq[0];
    }
    
    public String comp(){
        if(!commandType().equals("C_COMMAND"))
            return null;
        if(dest().equals(""))
            return command.split(";")[0];
        else{
            return command.split("=")[1];
        }
    }
    
    public String jump(){
        String[] splitJmp = command.split(";");
        if(!commandType().equals("C_COMMAND"))
            return null;
        if(splitJmp.length == 1)
            return "";
        else 
            return splitJmp[1];
    }
}
