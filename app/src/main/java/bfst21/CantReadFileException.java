package bfst21;

public class CantReadFileException extends RuntimeException{

    public CantReadFileException(String filetype){
        super("Error: cannot read " + filetype + " type files");
    }
}
