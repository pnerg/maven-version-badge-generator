/**
 *  Copyright 2015 Peter Nerg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dmonix.maven;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Utility for generating images out of provided strings.
 * 
 * @author Peter Nerg
 */
public class ImageGenerator {

    private final String text;
    private int size = 14;
    private String font = "Arial";
    private String format = "PNG";

    /**
     * Inhibitive constructor.
     * 
     * @param text
     */
    private ImageGenerator(String text) {
        this.text = text;
    }

    /**
     * Creates the generator for the provided text.
     * 
     * @param text
     * @return
     */
    public static ImageGenerator forText(String text) {
        return new ImageGenerator(text);
    }

    /**
     * Sets the font size for the output Optional, default is <tt>14</tt>
     * 
     * @param size
     * @return
     */
    public ImageGenerator withFontSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * Sets the font type for the output.<br>
     * Optional, default is <tt>Arial</tt>
     * 
     * @param font
     * @return
     */
    public ImageGenerator withFont(String font) {
        this.font = font;
        return this;
    }

    /**
     * Sets the output format of the image.<br>
     * Optional, default is <tt>png</tt>
     * 
     * @param format
     * @return
     */
    public ImageGenerator withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Creates the image to the provided output stream
     * 
     * @param ostream
     * @throws IOException
     */
    public void createImage(OutputStream ostream) throws IOException {
        /*
         * Because font metrics is based on a graphics context, we need to create a small, temporary image so we can ascertain the width and height of the final
         * image
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(this.font, Font.BOLD, size);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        ImageIO.write(img, format, ostream);
    }

}
