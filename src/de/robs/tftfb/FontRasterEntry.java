package de.robs.tftfb;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FontRasterEntry {

  public static final int IMAGE_TYPE = BufferedImage.TYPE_BYTE_BINARY;

  public static final int ROTATE_000 = 0;
  public static final int ROTATE_090 = 1;
  public static final int ROTATE_180 = 2;
  public static final int ROTATE_270 = 3;

  private final int codePoint; // the code point of character
  private final BufferedImage rasterImage; // the rendered image, raster width in full bytes
  private final int rasterWidthUsed; // used bits in horizontal raster bytes include padding

  public FontRasterEntry(int cp, FontMetrics m, int px, int py, int rot, boolean flip) {

    this.codePoint = cp;
    int cd = m.getDescent();
    int cw = m.charWidth(codePoint);

    if (cw > 0) { // invalid chars have zero width

      cw += 2*px; // add padding columns to character width
      int ch = m.getHeight() + 2*py; // add padding rows to character height
      int rbw = 0; // initialize width for byte-packed raster bits
      int rbh = 0; // initialize height for byte-packed raster bits

      switch (rot) {

      // We use images of TYPE_BYTE_BINARY to render the glyphs.
      // It uses a byte-packet raster, so round up the raster width to full bytes
      case ROTATE_000: case ROTATE_180: rbw = cw+7 & -8; rbh = ch; break;
      case ROTATE_090: case ROTATE_270: rbw = ch+7 & -8; rbh = cw; break;

      }

      char chars[] = Character.toChars(codePoint);
      BufferedImage image = new BufferedImage(rbw, rbh, IMAGE_TYPE);
      Graphics2D g = image.createGraphics();

      // --- fill background ---

      g.setColor(Color.BLACK);
      g.fillRect(0, 0, rbw, rbh);

      // --- set rotation & flipping ---

      double theta = 0;
      double scw = 1.0, sch = flip ? -1 : 1;
      double trw = 0, trh = 0;

      switch (rot) {

      case ROTATE_000: theta = 0          ; trw = 0  ; trh = flip?-ch:0   ; break;
      case ROTATE_090: theta = Math.PI/2  ; trw = 0  ; trh = flip?0  :-ch ; break;
      case ROTATE_180: theta = Math.PI    ; trw = -cw; trh = flip?0  :-ch ; break;
      case ROTATE_270: theta = Math.PI*3/2; trw = -cw; trh = flip?-ch:0   ; break;

      }

      AffineTransform tx = new AffineTransform();
      tx.concatenate(AffineTransform.getRotateInstance(theta));
      tx.concatenate(AffineTransform.getScaleInstance(scw, sch));
      tx.concatenate(AffineTransform.getTranslateInstance(trw, trh));
      g.setTransform(tx);

      // --- draw the glyph ---

      g.setFont(m.getFont());
      g.setColor(Color.WHITE);
      g.drawChars(chars, 0, chars.length,
          px, ch - py - cd - 1);
      g.dispose();

      switch (rot) {

      case ROTATE_000: case ROTATE_180: this.rasterWidthUsed = cw;  break;
      case ROTATE_090: case ROTATE_270: this.rasterWidthUsed = ch; break;
      default: this.rasterWidthUsed = 0;

      }

      this.rasterImage = image;

    } else {

      this.rasterWidthUsed = 0;
      this.rasterImage = null;

    }

  }

  public int getCodePoint() {
    return codePoint;
  }

  public BufferedImage getRasterImage() {
    return rasterImage;
  }

  public int getRasterWidthUsed() {
    return rasterWidthUsed;
  }

}
