package com.mycompany.assembler;
import java.io.*;

/**
 *
 */
public class Assembler {
    static int romAddress = 0;
    static int ramAddress = 16;
    static BufferedWriter outFile;

    public static void main(String[] args) {
        String path = (args.length == 0) ? "RectL.asm" : args[0];
        String fileName = path.split("\\.")[0];
        File program = new File(path);
        try{
            outFile = new BufferedWriter(new FileWriter(fileName+".hack"));
        }catch(IOException e){
        }
        SymbolTable symbolTable = new SymbolTable();
        Parser parser = new Parser(program);
        parser.advance();
        String op = "";
        String comp = "";
        String dest = "";
        String jump = "";
        String binCommand = "";
        String immediate = "";
        String newImmediate = "";
        
        //First Pass Symbol Table
        while(parser.hasMoreCommands()){
            switch (parser.commandType()) {
                case "A_COMMAND" -> {
                    romAddress++;
                    break;
                }
                case "L_COMMAND" -> {
                    if(!symbolTable.contains(parser.symbol())){
                        symbolTable.addEntry(parser.symbol(), romAddress);
                    }

                    break;
                }
                default -> {
                    romAddress++;
                    break;
                }
            }
            parser.advance();
        }
        
        //Second Pass Generate Binary
        parser = new Parser(program);
        parser.advance();
        while (parser.hasMoreCommands()) {
            switch (parser.commandType()) {
                case "C_COMMAND" -> {
                    op = "111";
                    comp = Code.comp(parser.comp());
                    if (parser.dest()!= null){
                        dest = Code.dest(parser.dest());
                    }   if (parser.jump()!= null)
                        jump = Code.jump(parser.jump());
                    binCommand = op + comp + dest + jump;
                }
                case "A_COMMAND" -> {
                    op = "0";
                    if(Character.isDigit(parser.symbol().charAt(0))){
                        immediate = Integer.toBinaryString(Integer.parseInt(parser.symbol()));
                    }else{
                        if(!symbolTable.contains(parser.symbol())){
                            symbolTable.addEntry(parser.symbol(), ramAddress);
                            ramAddress++;
                        }
                        immediate = Integer.toBinaryString(symbolTable.GetAddress(parser.symbol()));
                    }
                    int n = immediate.length();
                    for(int i = 0; i < 15-n; i++){
                        immediate = "0" + immediate;
                    }   
                    binCommand = op + immediate;
                }
                default -> {
                    if(!symbolTable.contains(parser.symbol())){
                        symbolTable.addEntry(parser.symbol(), romAddress);
                        romAddress++;
                    }
                }
            }
            
            if(!parser.commandType().equals("L_COMMAND"))
                writeToFile(binCommand);
            parser.advance();
        }
        try{
            outFile.close();
        }catch(IOException e){}
    }
    
    public static void writeToFile(String input){
        try{
            outFile.write(input+"\n");
        }catch(IOException e){}
    }
}
