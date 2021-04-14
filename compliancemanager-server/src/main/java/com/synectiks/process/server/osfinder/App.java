package com.synectiks.process.server.osfinder;

import com.synectiks.process.server.osfinder.utils.OS;

public class App {

  public static void main(String[] args) {

    System.out.println("name " + System.getProperty("os.name"));
    System.out.println("version " + System.getProperty("os.version"));
    System.out.println("arch " + System.getProperty("os.arch"));


    OS myOS = OS.OS;
    System.out.println("Your OS is :");
    System.out.println(" - Platform name = " + myOS.getPlatformName());
    System.out.println(" - OS name = " + myOS.getName());
    System.out.println(" - OS version = " + myOS.getVersion());
    System.out.println(" - OS architecture = " + myOS.getArch());
  }
}
