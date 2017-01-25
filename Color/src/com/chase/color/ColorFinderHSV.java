package com.chase.color;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ColorFinderHSV {

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

	int r_ = 180, g_ = 255, b_ = 255, r = 0, g = 0, b = 0;

	boolean e = false;

	BufferedImage img;

	String f;

	public ColorFinderHSV(String s, int n) {
		if (s == null) {
			cap = new VideoCapture(n);
			mat = new Mat();
		} else if (new File(s).exists()) {
			f = s;
		} else {
			System.exit(1);
		}
		view.setSize(640, 480);
		view.setTitle("Color Finder HSV");
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
		palet.setSize(480, 200);
		palet.setLocation(640, 0);
		palet.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		palet.setLayout(new GridLayout(7, 1));
		palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
		JSlider rmin = new JSlider();
		rmin.setMaximum(180);
		rmin.setMinimum(0);
		rmin.setValue(0);
		rmin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r = rmin.getValue();
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
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
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
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
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
			}
		});
		JSlider rmax = new JSlider();
		rmax.setMaximum(180);
		rmax.setMinimum(0);
		rmax.setValue(255);
		rmax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				r_ = rmax.getValue();
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
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
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
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
				palet.setTitle("Hmin" + r + " Hmax" + r_ + " Lmin" + g + " Lmax" + g_ + " Smin" + b + " Smax" + b_);
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
		palet.add(rmin);
		palet.add(rmax);
		palet.add(gmin);
		palet.add(gmax);
		palet.add(bmin);
		palet.add(bmax);
		palet.add(exit);
		palet.setVisible(true);
		while (!e) {
			if (cap != null) {
				cap.read(mat);
			} else {
				mat = Imgcodecs.imread(new File(f).getAbsolutePath());
			}
			Mat matp = mat.clone();
			ArrayList<MatOfPoint> c = new ArrayList<MatOfPoint>();
			Imgproc.cvtColor(matp, matp, Imgproc.COLOR_BGR2HLS);
			Core.inRange(matp, new Scalar(r, g, b), new Scalar(r_, g_, b_), matp);
			Imgproc.findContours(matp, c, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			int x = 0;
			int y = 0;
			for (int i = 0; i < c.size(); i++) {
				MatOfPoint mop = c.get(i);
				Rect rect = Imgproc.boundingRect(mop);
				x += rect.x + (rect.width / 2);
				y += rect.y + (rect.height / 2);
				Imgproc.rectangle(mat, rect.tl(), rect.br(), new Scalar(0, 255, 0));

			}
			if (!c.isEmpty()) {
				x /= c.size();
				y /= c.size();
				Imgproc.circle(mat, new Point(x, y), 2, new Scalar(0, 0, 255), 2);
				// Imgproc.line(mat, new Point(x, 0), new Point(x, 480), new
				// Scalar(0, 255, 0));
				// Imgproc.line(mat, new Point(0, y), new Point(640, y), new
				// Scalar(0, 255, 0));
			}
			img = matToBufferedImage(mat, null);
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
			new ColorFinderHSV(args[0], 0);
		} else {
			new ColorFinderHSV(null, 0);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
