package net.bioclipse.r;

public class NoRException extends Exception {
    private String message;
    public NoRException(String m){
        message=m;
    }
    public NoRException(Throwable c){
        initCause(c);
        message=(c==null)?null:c.toString();
    }
    public NoRException(String m, Throwable c){
        message=m;
        initCause(c);
    }
    public String toString(){
        return message;
    }
}
