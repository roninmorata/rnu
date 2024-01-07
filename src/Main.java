import as.intel.As;

public class Main {
    public static void main(String[] args) {
        
        //no type casting in main

        As as = new As();
        
        as.PRINTCH('A');
        as.JMP("START");
        as.EXIT();
        as.LABEL("START");
        as.PRINTCH('B');
        as.MOV(as.AH,0x09);
        as.MOV(as.DX,"DATA");
        as.INT(0x21);
        as.EXIT();
        as.LABEL("DATA");
        as.DATA("\r\nHELLO WORLD$");

        System.out.println(As.asUnsigned(as.getByteCode()));
        System.out.println(As.asUnsigned(as.compile()));        

        as.makeBinFile("TEST.COM", "BIN");
    }
}