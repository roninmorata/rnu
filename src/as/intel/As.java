/*
RNU
8086 Assembler
Author: Ronin Morata, Anastasia Koshelenko
*/

package as.intel;

import java.io.UnsupportedEncodingException;
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

    private <T> void _bytecode_add(T x) throws IllegalArgumentException {
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

    public As() {
        _IP = 0x100;
        _bytecode = new ArrayList<Object> ();
        _labels = new HashMap<String, Integer> ();
        _binLength = 0;
    }

    /////////////////
    // Instructions

    public void NOP() {
        _bytecode_add(0x90);
    }

    public void INT(int val8b) {
        _bytecode_add(0xCD);
        _bytecode_add(val8b);
    }

    public void MOV(int reg, int val) {
        _bytecode_add(0xB0 | reg);
        _bytecode_add(val);
        if ((reg >= 0x08) && (reg <= 0x0F)) {
            _bytecode_add((val & 0xFF00) >> 8);
        }
    }

    public void MOV(int reg16b, String val16b) {
        this._bytecode_add(0xB0 | reg16b);
        this._bytecode_add("2:" + val16b);
        this._IP++;
    }

    public void JMP(int dist8b) {
        _bytecode_add(0xEB);
        _bytecode_add(dist8b);
    }

    public void JMP(String dist8b) {
        _bytecode_add(0xEB);
        _bytecode_add("3:" + (_IP-1) + ":" + dist8b); // _IP gets decremented because _bytecode_add() automatically increments it
    }

    public void JMP(short dist16b) {
        _bytecode_add(0xE9);
        _bytecode_add(dist16b & 0xFFFF);
        _IP++;
    }

    public void INC(int reg16b) {
        _bytecode_add(0x40 | reg16b >> 4);
    }

    public void DEC(int reg16b) {
        _bytecode_add(0x40 | reg16b);
    }

    public void POP(int reg16b) {
        _bytecode_add(0x50 | reg16b);
    }

    public void PUSH(int reg16b) {
        _bytecode_add(0x50 | (reg16b >> 4));
    }

    public void CMP(byte val8b) {
        _bytecode_add(0x3C);
        _bytecode_add(val8b & 0xFF);
    }

    public void CMP(short val16b) {
        _bytecode_add(0x3D);
        _bytecode_add(val16b & 0xFFFF);
    }

    public void JZ(int dist8b) {
        _bytecode_add(0x74);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JNZ(int dist8b) {
        _bytecode_add(0x75);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JL(int dist8b) {
        _bytecode_add(0x7C);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JGE(int dist8b) {
        _bytecode_add(0x7D);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JLE(int dist8b) {
        _bytecode_add(0x7E);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JG(int dist8b) {
        _bytecode_add(0x7F);
        _bytecode_add(dist8b & 0xFF);
    }

    public void JE(int dist8b) {
        JZ(dist8b);
    }

    public void JNE(int dist8b) {
        JNZ(dist8b);
    }

    /////////////////////////
    // Assembler Directives

    public void LABEL(String label) {
        _labels.put(label, _IP);
    }

    public void DATA(String data) { //FIXFIX
        _bytecode_add(data);
    }

    public void EXIT() {
        MOV(AX, (short)0);
        INT(21);
    }

    public void EXIT(int value) {
        MOV(AX, (short)value);
        INT(21);
    }

    /////////////////////
    // Helper Functions

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

    private byte[] _byte(int value) {
        return new byte[] { (byte) (value & 0xFF) };
    }

    private byte[] _word(int value) {
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

    public ArrayList<Byte> compile(){
        ArrayList<Byte> result = new ArrayList<Byte> ();
        for (Object cell : _bytecode) {
            if (cell instanceof String) {
                String s = (String) cell;
                switch (s.charAt(0)) {
                    case '1':
                        for (byte b : _byte(_labels.get(s.substring(2)))) {
                            result.add(b);
                        }
                        break;
                    case '2':
                        for (byte b : _word(_labels.get(s.substring(2)))) {
                            result.add(b);
                        }
                        break;
                    case '3':
                        String data = s.substring(2);
                        Matcher m = Pattern.compile("(.+):(.+)").matcher(data);
                        if (m.find()) {
                            for (byte b : _byte(_labels.get(m.group(2)) - Integer.parseInt(m.group(1)))) {
                                result.add(b);
                            }
                        }
                        break;
                    default:
                        try{
                        for(byte b: s.getBytes("ASCII")){
                            result.add(b);
                        }
                    }catch(UnsupportedEncodingException e){
                        System.out.println(e.getMessage());
                    }

                }
            } else {
                result.add((Byte) cell);
            }
        }
        _binLength = result.size();
        return result;
    }
}