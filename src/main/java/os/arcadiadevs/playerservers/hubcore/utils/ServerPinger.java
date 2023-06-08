/*
 * MIT License.
 *
 * Copyright (c) 2021 MrMicky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package os.arcadiadevs.playerservers.hubcore.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;

/**
 * Simple tool to ping a Minecraft server to get MOTD, online players and max players.
 *
 * @author MrMicky
 */
public class ServerPinger {

  public static CompletableFuture<PingResult> ping(InetSocketAddress address, int timeout) {
    return CompletableFuture.supplyAsync(() -> {
      try (Socket socket = new Socket()) {

        socket.setSoTimeout(timeout);
        socket.connect(address, timeout);

        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             InputStream in = socket.getInputStream();
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_16BE)) {

          out.write(new byte[] {(byte) 0xFE, 0x01});

          int packetId = in.read();
          int length = reader.read();

          if (packetId != 0xFF) {
            return new PingResult(ServerStatus.UNKNOWN, null, null, null);
          }

          if (length <= 0) {
            return new PingResult(ServerStatus.UNKNOWN, null, null, null);
          }

          char[] chars = new char[length];

          if (reader.read(chars, 0, length) != length) {
            return new PingResult(ServerStatus.UNKNOWN, null, null, null);
          }

          String string = new String(chars);

          if (!string.startsWith("§")) {
            return new PingResult(ServerStatus.UNKNOWN, null, null, null);
          }

          String[] data = string.split("\000");

          int players = Integer.parseInt(data[4]);
          int maxPlayers = Integer.parseInt(data[5]);

          return new PingResult(ServerStatus.ONLINE, players, maxPlayers, data[3]);
        }
      } catch (IOException e) {
        return new PingResult(ServerStatus.OFFLINE, null, null, null);
      }
    });
  }

  public record PingResult(ServerStatus status, Integer players, Integer maxPlayers, String motd) {

    @Override
    public String toString() {
      return "PingResult{players=" + this.players + ", maxPlayers=" + this.maxPlayers + '}';
    }
  }
}