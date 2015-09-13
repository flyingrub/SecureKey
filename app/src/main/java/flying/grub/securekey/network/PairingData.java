/*
 * Copyright (C) 2015 - Holy Lobster
 *
 * Nuntius is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Nuntius is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nuntius. If not, see <http://www.gnu.org/licenses/>.
 */

package flying.grub.securekey.network;

public class PairingData {
    private final String fingerprint;

    public PairingData(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getDeviceLabel() {
        return "SECUREKEY";
    }

    public byte[] getFingerprint() {
        // Example:
        // 14:5F:C4:F5:A9:E9:18:37:2A:8F:BA:82:68:48:01:CE:D6:8C:03:9E

        String trustedFingerprint = fingerprint.replaceAll(":", "");
        int byteNum = trustedFingerprint.length() / 2;

        byte[] bytes = new byte[byteNum];
        for (int i = 0; i < byteNum; i++) {
            int j = i * 2;
            bytes[i] = (byte) Integer.parseInt(trustedFingerprint.substring(j, j + 2), 16);
        }
        return bytes;
    }
}