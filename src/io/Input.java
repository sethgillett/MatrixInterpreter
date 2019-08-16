package io;

import java.util.Scanner;

public abstract class Input {
  private static Scanner reader = new Scanner(System.in);

  public static String readLine() {
    return reader.nextLine();
  }

  public static void close() {
    reader.close();
  }
}