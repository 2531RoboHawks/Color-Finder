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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ColorFinderHSVSplitChannel {

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

	int g_ = 1000, s_ = 255, v_ = 255, g = 0, s = 0, v = 0;

	boolean e = false;

	BufferedImage img;

	String f;

	public ColorFinderHSVSplitChannel(String ss, int n) {
		if (ss == null) {
			cap = new VideoCapture(n);
			mat = new Mat();
		} else if (new File(ss).exists()) {
			f = ss;
		} else {
			System.exit(1);
		}
		view.setSize(640, 480);
		view.setTitle("Color Finder HSV");
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
		palet.setSize(480, 150);
		palet.setLocation(640, 0);
		palet.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		palet.setLayout(new GridLayout(7, 1));
		palet.setTitle("Gmin" + g + " Gmax" + g_);
		JSlider gmin = new JSlider();
		gmin.setMaximum(1000);
		gmin.setMinimum(0);
		gmin.setValue(0);
		gmin.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				g = gmin.getValue();
				palet.setTitle("Gmin" + g + " Gmax" + g_);
			}
		});

		JSlider gmax = new JSlider();
		gmax.setMaximum(1000);
		gmax.setMinimum(0);
		gmax.setValue(1000);
		gmax.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				g_ = gmax.getValue();
				palet.setTitle("Hmin" + g + " Gmax" + g_);
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
		palet.add(gmin);
		palet.add(gmax);
		palet.add(exit);
		palet.setVisible(true);

		while (!e) {
			if (cap != null) {
				cap.read(mat);
			} else {
				mat = Imgcodecs.imread(new File(f).getAbsolutePath());
			}
			Mat hsv = mat.clone();
			ArrayList<Mat> channels = new ArrayList<Mat>();
			Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_BGR2RGB);
			Mat thresHSV = new Mat();
			Imgproc.threshold(hsv, hsv, 254, 255, Imgproc.THRESH_BINARY);

			Core.split(hsv, channels);
			Mat H = channels.get(0);
			Mat S = channels.get(1);
			Mat V = channels.get(2);
			Mat shiftedV = S.clone();
			int shift = 25;
			byte[] data = new byte[(int) (shiftedV.cols() * shiftedV.rows() * shiftedV.elemSize())];
			shiftedV.get(0, 0, data);
			for (int j = 0; j < data.length; j += 2) {
				byte b = (byte) ((data[j] + shift) % 180);
				data[j] = b;
			}
			Mat cannyV = new Mat();
			Imgproc.Canny(shiftedV, cannyV, g, g_);
			ArrayList<MatOfPoint> contoursV = new ArrayList<MatOfPoint>();

			Imgproc.findContours(cannyV, contoursV, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			Mat outputV = mat.clone();
			int x = 0;
			int y = 0;
			for (int i = 0; i < contoursV.size(); i++) {
				if (Imgproc.contourArea(contoursV.get(i)) > 800) {
					// Imgproc.drawContours(outputV, contoursV, i, new Scalar(0,
					// 0, 255));

					Rect rect = Imgproc.boundingRect(contoursV.get(i));
					x += rect.x + (rect.width / 2);
					y += rect.y + (rect.height / 2);
					Imgproc.rectangle(outputV, rect.tl(), rect.br(), new Scalar(0, 255, 0));

				}

			}
			mat = outputV.clone();
			/*
			 * Mat matp = mat.clone(); ArrayList<MatOfPoint> c = new
			 * ArrayList<MatOfPoint>(); Imgproc.cvtColor(matp, matp,
			 * Imgproc.COLOR_BGR2HLS); Core.inRange(matp, new Scalar(h, s, v),
			 * new Scalar(h_, s_, v_), matp); Imgproc.findContours(matp, c, new
			 * Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE); int x =
			 * 0; int y = 0; for (int i = 0; i < c.size(); i++) { MatOfPoint mop
			 * = c.get(i); Rect rect = Imgproc.boundingRect(mop); x += rect.x +
			 * (rect.width / 2); y += rect.y + (rect.height / 2);
			 * Imgproc.rectangle(mat, rect.tl(), rect.br(), new Scalar(0, 255,
			 * 0));
			 * 
			 * } if (!c.isEmpty()) { x /= c.size(); y /= c.size();
			 * Imgproc.circle(mat, new Point(x, y), 2, new Scalar(0, 0, 255),
			 * 2); // Imgproc.line(mat, new Point(x, 0), new Point(x, 480), new
			 * // Scalar(0, 255, 0)); // Imgproc.line(mat, new Point(0, y), new
			 * Point(640, y), new // Scalar(0, 255, 0)); } img =
			 * matToBufferedImage(mat, null); view.repaint();
			 */
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
			new ColorFinderHSVSplitChannel(args[0], 0);
		} else {
			new ColorFinderHSVSplitChannel(null, 0);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
