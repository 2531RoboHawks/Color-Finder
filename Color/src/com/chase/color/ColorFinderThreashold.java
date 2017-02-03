package com.chase.color;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ColorFinderThreashold {

	@SuppressWarnings("serial")
	JFrame view = new JFrame() {
		@Override
		public void paint(Graphics gr) {
			gr.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
		}
	};

	public static BufferedImage matToBufferedImage(Mat matrix, BufferedImage bimg) {
		if (matrix != null) {
			int cols = matrix.cols();
			int rows = matrix.rows();
			int elemSize = (int) matrix.elemSize();
			byte[] data = new byte[cols * rows * elemSize];
			int type;
			matrix.get(0, 0, data);
			switch (matrix.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
			}

			// Reuse existing BufferedImage if possible
			if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
				bimg = new BufferedImage(cols, rows, type);
			}
			bimg.getRaster().setDataElements(0, 0, cols, rows, data);
		} else { // mat was null
			bimg = null;
		}
		return bimg;
	}

	JFrame palet = new JFrame();

	VideoCapture cap;

	Mat mat;

	int r_ = 255, g_ = 255, b_ = 255, r = 0, g = 0, b = 0;

	boolean e = false;

	BufferedImage img;

	String f;

	public ColorFinderThreashold(String s, int n) {
		if (s == null) {
			cap = new VideoCapture(n);
			if (!cap.isOpened()) {
				System.exit(1);
			}
			mat = new Mat();
		} else if (new File(s).exists()) {
			f = s;
		} else {
			System.exit(1);
		}
		view.setSize(640, 480);
		view.setTitle("Color Finder Threash View");
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
		palet.setSize(480, 200);
		palet.setLocation(640, 0);
		palet.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		palet.setLayout(new GridLayout(2, 1));
		palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
		JSlider rmin = new JSlider();
		rmin.setMaximum(255);
		rmin.setMinimum(0);
		rmin.setValue(0);
		rmin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r = rmin.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JSlider gmin = new JSlider();
		gmin.setMaximum(255);
		gmin.setMinimum(0);
		gmin.setValue(0);
		gmin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				g = gmin.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JSlider bmin = new JSlider();
		bmin.setMaximum(255);
		bmin.setMinimum(0);
		bmin.setValue(0);
		bmin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				b = bmin.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JSlider rmax = new JSlider();
		rmax.setMaximum(255);
		rmax.setMinimum(0);
		rmax.setValue(255);
		rmax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r_ = rmax.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JSlider gmax = new JSlider();
		gmax.setMaximum(255);
		gmax.setMinimum(0);
		gmax.setValue(255);
		gmax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				g_ = gmax.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JSlider bmax = new JSlider();
		bmax.setMaximum(255);
		bmax.setMinimum(0);
		bmax.setValue(255);
		bmax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				b_ = bmax.getValue();
				palet.setTitle("Rmin" + r + " Rmax" + r_ + " Gmin" + g + " Gmax" + g_ + " Bmin" + b + " Bmax" + b_);
			}
		});
		JButton exit = new JButton();
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				e = true;
			}
		});
		exit.setText("Quit");
		JSlider min = new JSlider();
		min.setMaximum(255);
		min.setMinimum(0);
		min.setValue(0);
		min.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r = min.getValue();
				palet.setTitle("Threash" + r);
			}
		});
		JSlider max = new JSlider();
		max.setMaximum(255);
		max.setMinimum(0);
		max.setValue(255);
		max.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r_ = max.getValue();
				palet.setTitle("Threash" + r);
			}
		});
		palet.add(min);
		palet.add(exit);
		palet.setVisible(true);
		while (!e) {
			if (cap != null) {
				cap.read(mat);
			} else {
				mat = Imgcodecs.imread(new File(f).getAbsolutePath());
			}
			Mat matp = new Mat();
			Imgproc.threshold(mat, matp, r, 255, Imgproc.THRESH_BINARY);
			img = matToBufferedImage(matp, null);
			view.repaint();
		}
		if (cap != null) {
			cap.release();
		}
		view.dispose();
		palet.dispose();
		System.exit(0);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			new ColorFinderThreashold(args[0], 0);
		} else {
			new ColorFinderThreashold(null, 0);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
