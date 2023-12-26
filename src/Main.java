import as.intel.As;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        As as = new As();
        
        as.PRINTCH((char)'A');
        as.JMP("START");
        as.EXIT();
        as.LABEL("START");
<<<<<<< HEAD
=======
        as.PRINTCH((char)'B');
>>>>>>> 17ba30c9dd8bba8d66bf80741cfd12957e276e67
        as.MOV(as.AH,0x09);
        as.MOV(as.DX,"DATA");
        as.INT(0x21);
        as.EXIT();
        as.LABEL("DATA");
        as.DATA("\r\nHELLO WORLD$");

        System.out.println(As.asUnsigned(as.getByteCode()));
        System.out.println(As.asUnsigned(as.compile()));        

        ArrayList<Byte> data = new ArrayList<>();
        data = as.compile();

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream("TEST.COM");

            for (Byte b : data) {
                fos.write(b);
            }

            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}