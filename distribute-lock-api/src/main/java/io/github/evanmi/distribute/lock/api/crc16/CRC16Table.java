package io.github.evanmi.distribute.lock.api.crc16;

public class CRC16Table {
    private static final int POLYNOMIAL = 0x1021;
    private static final int INITIAL_VALUE = 0xFFFF;
    private static final int[] CRC_TABLE = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int crc = i << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
            CRC_TABLE[i] = crc & 0xFFFF;
        }
    }

    public static int calculateCRC16(byte[] data) {
        int crc = INITIAL_VALUE;

        for (byte b : data) {
            crc = (crc << 8) ^ CRC_TABLE[((crc >> 8) ^ (b & 0xFF)) & 0xFF];
        }

        return crc & 0xFFFF;
    }
}
