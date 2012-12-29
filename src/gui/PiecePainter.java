package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import util.ImageResource;

public class PiecePainter {

	BufferedImage img;
	
	public PiecePainter(String imgPath) {
		
		img = ImageResource.grabImage(imgPath);
	}

	public void paint(Graphics g, int x, int y) {
		
		g.drawImage(img, x, y, null);
	}
	
}
