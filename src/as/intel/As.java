/*
RNU
8086 Assembler
Author: Ronin Morata, Anastasia Koshelenko
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
    private ArrayList<Object> _bytecode;
    private HashMap<String, Integer> _labels;
    private int _binLength;

    private <T> void _bytecode_add(T x) {
        this._bytecode.add(x);
        this._IP++;
    }

    public As() {
        this._IP = 0x100;
        this._bytecode = new ArrayList<Object> ();
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

    public void MOV(int reg8b, byte val8b) {
        this._bytecode_add(0xB0 | reg8b);
        this._bytecode_add(val8b & 0xFF);
    }

    public void MOV(int reg16b, short val16b) {
        this._bytecode_add(0xB0 | reg16b);
        this._bytecode_add(val16b & 0xFF);
        this._bytecode_add((val16b & 0xFF00) >> 8);
    }

    public void MOV(int reg16b, String val16b) {
        this._bytecode_add(0xB0 | reg16b);
        this._bytecode_add("2:" + val16b);
        this._IP++;
    }

    public void JMP(int dist8b) {
        this._bytecode_add(0xEB);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JMP(String dist8b) {
        this._bytecode_add(0xEB);
        this._bytecode_add("3:" + (this._IP-1) + ":" + dist8b); // _IP gets decremented because _bytecode_add() automatically increments it
    }

    public void JMP(short dist16b) {
        this._bytecode_add(0xE9);
        this._bytecode_add(dist16b & 0xFFFF);
        this._IP++;
    }

    public void INC(int reg16b) {
        this._bytecode_add(0x40 | reg16b >> 4);
    }

    public void DEC(int reg16b) {
        this._bytecode_add(0x40 | reg16b);
    }

    public void POP(int reg16b) {
        this._bytecode_add(0x50 | reg16b);
    }

    public void PUSH(int reg16b) {
        this._bytecode_add(0x50 | (reg16b >> 4));
    }

    public void CMP(byte val8b) {
        this._bytecode_add(0x3C);
        this._bytecode_add(val8b & 0xFF);
    }

    public void CMP(short val16b) {
        this._bytecode_add(0x3D);
        this._bytecode_add(val16b & 0xFFFF);
    }

    public void JZ(int dist8b) {
        this._bytecode_add(0x74);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JNZ(int dist8b) {
        this._bytecode_add(0x75);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JL(int dist8b) {
        this._bytecode_add(0x7C);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JGE(int dist8b) {
        this._bytecode_add(0x7D);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JLE(int dist8b) {
        this._bytecode_add(0x7E);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JG(int dist8b) {
        this._bytecode_add(0x7F);
        this._bytecode_add(dist8b & 0xFF);
    }

    public void JE(int dist8b) {
        this.JZ(dist8b);
    }

    public void JNE(int dist8b) {
        this.JNZ(dist8b);
    }

    /////////////////////////
    // Assembler Directives

    public void LABEL(String label) {
        this._labels.put(label, this._IP);
    }

    public void DATA(String data) { //FIXFIX
        this._bytecode_add(data);
    }

    public void EXIT() {
        this.MOV(this.AX, (short)0);
        this.INT(21);
    }

    public void EXIT(int value) {
        this.MOV(this.AX, (short)value);
        this.INT(21);
    }

    /////////////////////
    // Helper Functions

    public static ArrayList<Object> asUnsigned(ArrayList<?> bytecode){
        ArrayList<Object> unsignedNums = new ArrayList<>();
        for(Object signedByte: bytecode){
            if(signedByte instanceof Byte){
            unsignedNums.add(((Byte) signedByte).intValue() & 0xFF);
            }else{
                unsignedNums.add(signedByte);
            }
        }
        return unsignedNums;
    }

    private byte[] _byte(int value) {
        return new byte[] {(byte) (value & 0xFF)};
    }

    private byte[] _word(int value) {
        return new byte[] {(byte) (value & 0xFF), (byte) ((value & 0xFF00) >> 8)};
    }

    ////////////
    // Getters

    public ArrayList<Object> getByteCode() {
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
                result.add(((Integer) cell).byteValue());
            }
        }
        this._binLength = result.size();
        return result;
    }
}