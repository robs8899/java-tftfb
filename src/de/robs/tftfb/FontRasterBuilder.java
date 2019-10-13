package de.robs.tftfb;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.List;

public class FontRasterBuilder {
  
  private final String alias;
  private final int px, py, rotate;
  private final boolean flip;
  private final FontMetrics metrics;
  private final List<FontRasterEntry> renderedChars;

  public FontRasterBuilder(Font font, String alias, int px, int py, int rotate, boolean flip) {

    BufferedImage testImage = new BufferedImage(1, 1, FontRasterEntry.IMAGE_TYPE);
    Graphics testGraphics = testImage.getGraphics();
    this.metrics = testGraphics.getFontMetrics(font);
    testGraphics.dispose();

    this.alias = alias;
    this.px = px; this.py = py;
    this.rotate = rotate; this.flip = flip;
    this.renderedChars = new ArrayList<FontRasterEntry>();

  }
  
  public void add(int cpFrom, int cpTo) {
    
    for (int cp=cpFrom; cp<=cpTo; cp++) renderedChars.add(new FontRasterEntry(
        cp, metrics, px, py, rotate, flip));
    
  }
  
  public void add(int cp) {
    
    renderedChars.add(new FontRasterEntry(
        cp, metrics, px, py, rotate, flip));
    
  }
  
  public String toSourceCode() {
    
    String bitmapsSymbol = alias + "_bitmap";
    String dscrTblSymbol = alias + "_table";
    String fontInfoSymbol = alias + "_info";
    StringBuilder sb = new StringBuilder();

    sb.append("/* Character bitmaps */\n");
    sb.append("const uint8_t ").append(bitmapsSymbol).append("[] = {\n");
    for (FontRasterEntry e:renderedChars) {

      DataBuffer data = e.getRasterImage().getRaster().getDataBuffer();
      int rbw = e.getRasterImage().getWidth()/8; // full raster bytes width
      int rbh = e.getRasterImage().getHeight(); // raster bytes height

      for (int y=0; y < rbh; y++) {

        sb.append("\n");
        for (int x=0; x < rbw; x++) {
          int p = data.getElem(rbw*y+x);
          sb.append(String.format("  0x%02X,", p));
        }

        sb.append(" // ");
        for (int x=0; x < rbw; x++)  for (int b=0x80, i=0; b > 0; b >>= 1, i++) {
            if (8*x+i < e.getRasterWidthUsed()) {
              int p = data.getElem(rbw*y+x);
              sb.append(((p & b) == b) ? '#' : '.');
            } else sb.append('x'); // mark unused bits
          }
      }
      sb.append("\n");

    }
    sb.append("\n}\n");

    int idx = 0;
    sb.append("\n/* Bitmap descriptors: {bitmap height, bitmap index} */\n");
    sb.append("const font_entry ").append(dscrTblSymbol).append("[] = {\n\n");
    for (FontRasterEntry e:renderedChars) {
      int rbw = e.getRasterImage().getWidth();
      int rbh = e.getRasterImage().getHeight();
      sb.append("  {").append(rbh).append(", ").append(idx).append("},");
      sb.append(" // '").append((char)e.getCodePoint()).append("'\n");
      idx += rbh*rbw/8;
    }
    sb.append("\n}\n");

    FontRasterEntry first = renderedChars.get(0);

    sb.append("\n/* Font descriptor, elements are: raster width, first code point, */\n");
    sb.append("/* last code point, table pointer , bitmap pointer */\n");
    sb.append("const font_info ").append(fontInfoSymbol).append(" {\n\n");
    sb.append("  ").append(first.getRasterImage().getWidth()/8).append(", // raster bytes width\n");
    sb.append("  ").append(first.getRasterWidthUsed()).append(", // used bits of raster width\n");    
    sb.append("  ").append(first.getCodePoint()).append(", // code point of first entry\n");
    sb.append("  ").append(renderedChars.size()).append(", // number of entries\n");
    sb.append("  ").append(dscrTblSymbol).append(", // descriptor table\n");
    sb.append("  ").append(bitmapsSymbol).append(" // character bitmaps\n");
    sb.append("\n}\n");
    
    return sb.toString();
    
  }

}