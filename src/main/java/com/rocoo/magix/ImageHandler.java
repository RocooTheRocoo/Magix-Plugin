package com.rocoo.magix;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ImageHandler {

    private Magix magix;

    private static Map<String, char[]> tokens = new HashMap<>();
    private static Map<char[], byte[]> tokenToImageData = new HashMap<>();

    public ImageHandler(Magix magix) throws IOException {
        this.magix = magix;
        byte[] data = loadDefaultImage(Magix.class.getResourceAsStream("/image.gnp"), false);
        tokenToImageData.put(tokens.get("default"), data);
        this.magix.setImageToken(tokens.get("default"));
    }

    private byte[] loadDefaultImage(final InputStream inputStream, boolean close) {
        try {
            DataInputStream stream = new DataInputStream(new BufferedInputStream(inputStream));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);

            byte[] buffer;

            if (!stream.readUTF().equals("GNP"))
                throw new IllegalArgumentException("Illegal image format!");

            outputStream.writeLong(0x89504E470D0A1A0AL); // write the default PNG signature

            int header1 = stream.readInt();
            int header2 = stream.readInt();

            outputStream.writeInt(header1);
            outputStream.writeInt(header2);

            buffer = new byte[17];
            stream.read(buffer);
            outputStream.write(buffer);

            while (true) {

                int length = stream.readInt();
                int type = stream.readInt();

                outputStream.writeInt(length);
                outputStream.writeInt(type);

                if (type == 0x49454E44) { // End
                    int code = stream.readInt();
                    outputStream.writeInt(code);
                    break;
                }

                switch (type) {
                    case 0x74455874:  { // tEXT
                        buffer = new byte[length];

                        for (int i = 0; i < buffer.length; i++) {
                            buffer[i] = (byte) ~stream.readByte();
                        }

                        outputStream.write(buffer);
                        int keyLength = 0;

                        while (buffer[keyLength] != 0) {
                            keyLength++;
                        }

                        // String key = new String(buffer, 0, keyLength, StandardCharsets.UTF_8);
                        // String value = new String(buffer, keyLength + 1, length - keyLength - 1, StandardCharsets.UTF_8);

                        String rawToken = new String(buffer, keyLength + 1, length - keyLength - 1, StandardCharsets.UTF_8);
                        tokens.put("default", rawToken.toCharArray());

                        buffer = new byte[4];
                        stream.read(buffer);

                        outputStream.write(buffer);
                        break;
                    }

                    default: {
                        buffer = new byte[length + 4];
                        stream.read(buffer);

                        outputStream.write(buffer);
                        break;
                    }
                }
            }

            outputStream.flush();
            outputStream.close();

            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (close) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public byte[] getImageByToken(char[] token) {
        return tokenToImageData.get(token);
    }
}
