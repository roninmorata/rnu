import as.intel.As;

public class Main {
    public static void main(String[] args) {
        As as = new As();
        
        as.NOP();
        as.INT(21);
        as.MOVR8(as.AX,3);
        as.LABEL("HELLO");
        as.MOVR16(as.DX,"HELLO");

        System.out.println(As.asUnsigned(as.getByteCode()));
        System.out.println(As.asUnsigned(as.compile()));        
    }
}