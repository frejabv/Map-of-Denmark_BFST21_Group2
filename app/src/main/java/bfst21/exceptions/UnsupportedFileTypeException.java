package bfst21.exceptions;

public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String filetype) {
        super("Error: cannot read " + filetype + " type files");
    }
}
