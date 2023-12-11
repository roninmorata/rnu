/*
RNU
8086 Assembler
Author: Ronin Morata
*/

package as.intel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class As {
    //////////////
    // Registers

    public final int AL = 0x00;
    public final int CL = 0x01;
    public final int DL = 0x02;
    public final int BL = 0x03;
    public final int AH = AL & 0x04;
    public final int CH = CL & 0x04;
    public final int DH = DL & 0x04;
    public final int BH = BL & 0x04;

    public final int AX = 0x08;
    public final int CX = 0x09;
    public final int DX = 0x0A;
    public final int BX = 0x0B;
    public final int SP = 0x0C;
    public final int BP = 0x0D;
    public final int SI = 0x0E;
    public final int DI = 0x0F;

    /////////////
    // Bytecode

    private int _IP; // Instruction Pointer
    private ArrayList<Byte> _bytecode;
    private HashMap<String, Integer> _labels;
    private int _binLength;

    private void _bytecode_add(int x) {
        this._bytecode.add((byte)x);
        this._IP++;
    }

    public As() {
        this._IP = 0x100;
        this._bytecode = new ArrayList<Byte> ();
        this._labels = new HashMap<String, Integer> ();
        this._binLength = 0;
    }

    /////////////////
    // Instructions

    public void NOP() {
        this._bytecode_add(0x90);
    }

    public void INT(int val8b) {
        this._bytecode_add(0xCD);
        this._bytecode_add(val8b & 0xFF);
    }

    public void MOVR8(int reg8b, int val8b) {
        this._bytecode_add(0xB0 | reg8b);
        this._bytecode_add(val8b & 0xFF);
    }

    /////////////////////
    // Helper Functions

    private byte[] _byte(int value) {
        return new byte[] {(byte) (value & 0xFF)};
    }

    private byte[] _word(int value) {
        return new byte[] {(byte) (value & 0xFF), (byte) ((value & 0xFF00) >> 8)};
    }

    ////////////
    // Getters

    public ArrayList<Byte> getByteCode() {
        return this._bytecode;
    }

    public int getBinLength() {
        return this._binLength;
    }

    ////////////////
    // Compilation

    public ArrayList<Byte> compile() {
        ArrayList<Byte> result = new ArrayList<Byte> ();
        for (Object cell : this._bytecode) {
            if (cell instanceof String) {
                String s = (String) cell;
                switch (s.charAt(0)) {
                    case '1':
                        for (byte b : this._byte(this._labels.get(s.substring(2)))) {
                            result.add(b);
                        }
                        break;
                    case '2':
                        for (byte b : this._word(this._labels.get(s.substring(2)))) {
                            result.add(b);
                        }
                        break;
                    case '3':
                        String data = s.substring(2);
                        Matcher m = Pattern.compile("(.+):(.+)").matcher(data);
                        if (m.find()) {
                            for (byte b : this._byte(this._labels.get(m.group(2)) - Integer.parseInt(m.group(1)))) {
                                result.add(b);
                            }
                        }
                        break;
                }
            } else {
                result.add((Byte) cell);
            }
        }
        this._binLength = result.size();
        return result;
    }
}