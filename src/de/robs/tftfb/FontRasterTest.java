package de.robs.tftfb;

import java.awt.Font;

public class FontRasterTest {

  public static void main(String args[]) {

    Font font = new Font("Open Sans Regular", Font.PLAIN, 24);
    FontRasterBuilder frb = new FontRasterBuilder(font,
        "osans", 0, -2, FontRasterEntry.ROTATE_090, true);
    
    frb.add(32, 127);
    
    System.out.println(frb.toSourceCode());

  }
  
}
