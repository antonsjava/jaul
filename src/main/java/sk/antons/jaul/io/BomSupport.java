/*
 *
 */
package sk.antons.jaul.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Bom support for InputStream - Reader and OutputStream - Writer conversion.
 *
 * Supported boms
 + - UTF-8 --- EF BB BF
 + - UTF-16BE (UTF-16) --- FE FF
 + - UTF-16LE --- FF FE
 + - UTF-32BE (UTF-32) --- 00 00 FE FF
 + - UTF-32LE --- FF FE 00 00
 *
 * @author antons
 */
public class BomSupport {

    /**
     * Creates OutputStreamWriter(os, encoding), But for supported encodings
     * writes BOM to os first.
     * @param os
     * @param encoding
     * @return
     * @throws IOException
     */
    public static OutputStreamWriter writer(OutputStream os, String encoding) throws IOException {
        String enc = encoding.toLowerCase();
        switch(enc) {
            case "utf-8":
                os.write(0xEF);
                os.write(0xBB);
                os.write(0xBF);
                break;
            case "utf-16":
                break; //bom writen by default by OutputStreamWriter
            case "utf-16be":
                os.write(0xFE);
                os.write(0xFF);
                break;
            case "utf-16le":
                os.write(0xFF);
                os.write(0xFE);
                break;
            case "utf-32":
            case "utf-32be":
                os.write(0x00);
                os.write(0x00);
                os.write(0xFE);
                os.write(0xFF);
                break;
            case "utf-32le":
                os.write(0xFF);
                os.write(0xFE);
                os.write(0x00);
                os.write(0x00);
                break;
            default:
        }
        return new OutputStreamWriter(os, encoding);
    }


    /**
     * Creates InputStreamReader(is, encoding). Byt if supported BOM is
     * recognized, than given encoding is replaced by recognized encoding.
     * @param is
     * @param encoding
     * @return
     * @throws IOException
     */
    public static InputStreamReader reader(InputStream is, String encoding) throws IOException {

        UndoInputStream uis = UndoInputStream.instance(is);
        int b1 = uis.read();
        int b2 = -1;
        int b3 = -1;
        int b4 = -1;
        if(b1 == 0xEF) { // utf8
            b2 = uis.read();
            if(b2 == 0xBB) { // utf8
                b3 = uis.read();
                if(b3 == 0xBF) { // utf8
                    return new InputStreamReader(uis, "UTF-8");
                } else {
                    uis.undo((byte)b2);
                    uis.undo((byte)b1);
                    return new InputStreamReader(uis, encoding);
                }
            } else {
                uis.undo((byte)b2);
                uis.undo((byte)b1);
                return new InputStreamReader(uis, encoding);
            }
        } else if(b1 == 0xFE) { //utf16BE
            b2 = uis.read();
            if(b2 == 0xFF) { // utf16BE
                return new InputStreamReader(uis, "UTF-16BE");
            } else {
                uis.undo((byte)b2);
                uis.undo((byte)b1);
                return new InputStreamReader(uis, encoding);
            }
        } else if(b1 == 0xFF) { //utf16LE utf32LE
            b2 = uis.read();
            if(b2 == 0xFE) { // utf16LE utf32LE
                b3 = uis.read();
                if(b3 == 0x00) { // utf16LE utf32LE
                    b4 = uis.read();
                    if(b4 == 0x00) { // utf32LE
                        return new InputStreamReader(uis, "UTF-32LE");
                    } else {
                        uis.undo((byte)b4);
                        uis.undo((byte)b3);
                        return new InputStreamReader(uis, "UTF-16LE");
                    }
                } else {
                    uis.undo((byte)b3);
                    return new InputStreamReader(uis, "UTF-16LE");
                }
            } else {
                uis.undo((byte)b2);
                uis.undo((byte)b1);
                return new InputStreamReader(uis, encoding);
            }
        } else if(b1 == 0x00) { //utf32BE
            b2 = uis.read();
            if(b2 == 0x00) { // utf32BE
                b3 = uis.read();
                if(b3 == 0xFE) { // utf16LE utf32BE
                    b4 = uis.read();
                    if(b4 == 0xFF) { // utf32BE
                        return new InputStreamReader(uis, "UTF-32BE");
                    } else {
                        uis.undo((byte)b4);
                        uis.undo((byte)b3);
                        uis.undo((byte)b2);
                        uis.undo((byte)b1);
                        return new InputStreamReader(uis, encoding);
                    }
                } else {
                    uis.undo((byte)b3);
                    uis.undo((byte)b2);
                    uis.undo((byte)b1);
                    return new InputStreamReader(uis, encoding);
                }
            } else {
                uis.undo((byte)b2);
                uis.undo((byte)b1);
                return new InputStreamReader(uis, encoding);
            }
        } else {
            uis.undo((byte)b1);
            return new InputStreamReader(uis, encoding);
        }
    }
}
