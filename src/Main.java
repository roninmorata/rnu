import as.intel.As;

public class Main {
    public static void main(String[] args) {
        As as = new As();
        
        as.JMP("START");
        as.EXIT();
        as.LABEL("START");
        as.MOV(as.AH,(byte)0x09);
        as.MOV(as.DX,"DATA");
        as.INT(21);
        as.EXIT();
        as.LABEL("DATA");
        as.DATA("HELLO WORLD");

        System.out.println(As.asUnsigned(as.getByteCode()));
        System.out.println(As.asUnsigned(as.compile()));        
    }
}