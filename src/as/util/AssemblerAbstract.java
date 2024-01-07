/*
RNU
Assembler abstract class
Author: Ronin Morata, Anastasia Koshelenko
*/

package as.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AssemblerAbstract{

    protected int _IP; // Instruction Pointer
    protected ArrayList<Object> _bytecode;
    protected HashMap<String, Integer> _labels;
    protected int _binLength;

    protected <T> void _bytecode_add(T x) throws IllegalArgumentException {
        if(!((x instanceof String) || (x instanceof Number))){
            throw new IllegalArgumentException();
        }
        if(x instanceof Number){
            _bytecode.add((Byte) ((Number)x).byteValue());
        }else {
        _bytecode.add(x);
        }
        _IP++;
    }

    public static ArrayList<Object> asUnsigned(ArrayList<?> bytecode) {
        ArrayList<Object> unsignedNums = new ArrayList<>();
        for (Object signedByte : bytecode) {
            if (signedByte instanceof Byte) {
                unsignedNums.add(((Byte) signedByte).intValue() & 0xFF);
            } else {
                unsignedNums.add(signedByte);
            }
        }
        return unsignedNums;
    }

    protected byte[] _byte(int value) {
        return new byte[] { (byte) (value & 0xFF) };
    }

    protected byte[] _word(int value) {
        return new byte[] { (byte) (value & 0xFF), (byte) ((value & 0xFF00) >> 8) };
    }

    ////////////
    // Getters

    public ArrayList<Object> getByteCode() {
        return _bytecode;
    }

    public int getBinLength() {
        return _binLength;
    }

    ////////////////
    // Compilation

    public abstract ArrayList<Byte> compile();

    public void makeBinFile(String fileName, String executableFormat) {
        ArrayList<Byte> data = new ArrayList<>();
        
        switch (executableFormat) {
            case "PE":
            case "MZ":
                data.add((byte)0x4D); // 0x00: 'M'
                data.add((byte)0x5A); // 0x01: 'Z'
                data.add((byte)(this._binLength % 512)); // 0x02: Number of bytes in the last page.
                data.add((byte)0x00);
                data.add((byte)((this._binLength / 512) + 1)); // 0x04: Number of whole/partial pages.
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x06: Number of entries in the relocation table. 
                data.add((byte)0x00);

                // 0x08: Header size
                if (executableFormat == "PE") {
                    data.add((byte)(0x3C + 1));
                    data.add((byte)0x00);
                } else {
                    data.add((byte)(0x1C + 1));
                    data.add((byte)0x00);
                }

                data.add((byte)((this._binLength / 16) + (this._binLength % 16))); // 0x0A: The number of paragraphs required by the program, excluding the PSP and program image. If no free block is big enough, the loading stops.
                data.add((byte)0x00);
                data.add((byte)((this._binLength / 16) + (this._binLength % 16))); // 0x0C: The number of paragraphs requested by the program. If no free block is big enough, the biggest one possible is allocated.
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x0E:  Initial SS
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x10: Initial SP
                data.add((byte)0x00);
                data.add((byte)this._binLength); // 0x12: Checksum
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x14: Initial IP
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x16: Initial CS
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x18: Relocation table
                data.add((byte)0x00);
                data.add((byte)0x00); // 0x1A: Overlay
                data.add((byte)0x00);

                if (executableFormat == "PE") {
                    data.add((byte)0x00); // 0x1C: Reserved
                    for (int i = 0; i < 8; i++) {
                        data.add((byte)0x00);
                    }
                    data.add((byte)0x00); // 0x24: OEM identifier
                    data.add((byte)0x00);
                    data.add((byte)0x00); // 0x24: OEM info
                    data.add((byte)0x00);
                    data.add((byte)0x00); // 0x28: Reserved
                    data.add((byte)0x00);
                    for (int i = 0; i < 20; i++) {
                        data.add((byte)0x00);
                    }
                    data.add((byte)0x00); // 0x3C: PE header start
                } else {
                    data.add((byte)0x00); // 0x1C: Overlay information
                }

                break;
            case "BIN":
            case "COM":
            default:
        }

        ArrayList<Byte> executableData = new ArrayList<>();
        executableData = this.compile();
        for (Byte b : executableData) {
            data.add(b);
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fileName);

            for (Byte b : data) {
                fos.write(b);
            }

            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}