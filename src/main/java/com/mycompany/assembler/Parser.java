package com.mycompany.assembler;
import java.io.*;

public class Parser {
    BufferedReader reader;
    String command = null;
    int lineNum = 0;

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

    public void advance() {
        try {
            command = reader.readLine();
            lineNum++;
            while (hasMoreCommands() && skipLine()) {
                command = reader.readLine();
                lineNum++;
            }
            String eMessage = String.format("Invalid syntax on line %d", lineNum); 
            if(hasMoreCommands()){
                command = command.replaceAll(" ", "");
                command = command.split("//")[0];
                if(!validCommand())
                    throw new Exception(eMessage);
            }
        } catch (Exception e) {
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
        
    public boolean validCommand() {
        switch(commandType()){
            case "A_COMMAND" -> {
            }
            case "L_COMMAND" -> {
                if(command.charAt(command.length()-1) != ')')
                    return false;
                else
                    break;
            }
            case "C_COMMAND" -> {
                if(command.split(";").length != 1){
                    boolean comp1 = compCheck(command.split(";")[0]);
                    boolean dest = jumpCheck(command.split(";")[1]);
                    if(!comp1 || !dest)
                        return false;
                }
                if(command.split("=").length != 1){
                    boolean jump = destCheck(command.split("=")[0]);
                    boolean comp2 = compCheck(command.split("=")[1]);
                    if(!comp2 || !jump)
                        return false;
                }   
                break;
            } 
            default -> {
                return false;
            }
        }
        return true;
    }
    
    public boolean destCheck(String dest){
        return switch (dest) {
            case "M" -> true;
            case "D" -> true;
            case "MD" -> true;
            case "A" -> true;
            case "AM" -> true;
            case "AD" -> true;
            case "AMD" -> true;
            default -> false;
        };
    }
    
    public boolean compCheck(String comp){
        return switch (comp) {
            case "0" -> true;
            case "1" -> true;
            case "-1" -> true;
            case "D" -> true;
            case "A" -> true;
            case "!D" -> true;
            case "!A" -> true;
            case "-D" -> true;
            case "-A" -> true;
            case "D+1" -> true;
            case "A+1" -> true;
            case "D-1" -> true;
            case "A-1" -> true;
            case "D+A" -> true;
            case "D-A" -> true;
            case "A-D" -> true;
            case "D&A" -> true;
            case "D|A" -> true;
            case "M" -> true;
            case "!M" -> true;
            case "-M" -> true;
            case "M+1" -> true;
            case "M-1" -> true;
            case "D+M" -> true;
            case "D-M" -> true;
            case "M-D" -> true;
            case "D&M" -> true;
            case "D|M" -> true;
            default -> false;
        };
    }
    
    public boolean jumpCheck(String jump){
        return switch (jump) {
            case "JGT" -> true;
            case "JEQ" -> true;
            case "JGE" -> true;
            case "JLT" -> true;
            case "JNE" -> true;
            case "JLE" -> true;
            case "JMP" -> true;
            default -> false;
        };
    }
}
