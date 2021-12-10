package com.jeojck.imgtohtml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

@Service
public class ConverterService {

    private final static Logger log = LoggerFactory.getLogger(ConverterService.class);

    private String zeroPad(String input) {
        if(input.length() == 1) {
            input = String.format("0%1$s", input);
        }

        return input;
    }

    private String toHex(int[] rgb) {
        String r = zeroPad(Integer.toHexString(rgb[0]));
        String g = zeroPad(Integer.toHexString(rgb[1]));
        String b = zeroPad(Integer.toHexString(rgb[2]));

        String formattedRgb = String.format("%1$s%2$s%3$s", r, g, b);
        log.debug("Formatted RGB: ", formattedRgb);
        return formattedRgb;
    }

    public String imgToHtml(MultipartFile file, Model model) throws IOException {
        StringBuffer table = new StringBuffer();
        table.append("<table class=\"imgtable\">\n");

        long start = System.currentTimeMillis();

        BufferedImage img = ImageIO.read(file.getInputStream());
        Raster r = img.getData();

        int height = img.getHeight();
        int width = img.getWidth();

        log.info("Image size: {}H x {}w", height, width);
        for(int y = 0; y < height; y++) {
            table.append("<tr class=\"imgrow\">");
            for (int x = 0; x < width; x++) {
                int[] pixels = r.getPixel(x, y, (int[]) null);
                String rgb = toHex(pixels);
                table.append(String.format("<td class=\"imgcell\" bgcolor=\"%1$s\"></td>", rgb));
            }
        }

        table.append("</tr>\n");

        long end = System.currentTimeMillis();
        long convTime = end - start;
        log.info("time to convert: {}ms", convTime);

        table.append("\n</table>");

        model.addAttribute("tableData", table);
        model.addAttribute("convTime", convTime);

        return table.toString();
    }
}
