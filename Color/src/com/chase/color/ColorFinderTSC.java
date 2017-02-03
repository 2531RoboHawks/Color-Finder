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

public class ColorFinderTSC {

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

	int threash = 0, canny1 = 0, canny2 = 10000;

	boolean e = false;

	BufferedImage img;

	String f;

	public ColorFinderTSC(String s, int n) {
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
		view.setTitle("Color Finder TSC");
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
		palet.setSize(600, 300);
		palet.setLocation(640, 0);
		palet.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		palet.setLayout(new GridLayout(4, 1));
		palet.setTitle("Threash" + threash + " Canny1 " + canny1 + " Canny2 " + canny2);
		JButton exit = new JButton();
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				e = true;
			}
		});
		exit.setText("Quit");
		JSlider thr = new JSlider();
		thr.setMinimum(0);
		thr.setMaximum(255);
		thr.setValue(0);
		thr.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				threash = thr.getValue();
				palet.setTitle("Threash " + threash + " Canny1 " + canny1 + " Canny2 " + canny2);
			}
		});
		JSlider min = new JSlider();
		min.setMinimum(0);
		min.setMaximum(10000);
		min.setValue(0);
		min.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				canny1 = min.getValue();
				palet.setTitle("Threash " + threash + " Canny1 " + canny1 + " Canny2 " + canny2);
			}
		});
		JSlider max = new JSlider();
		max.setMinimum(0);
		max.setMaximum(10000);
		max.setValue(10000);
		max.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				canny2 = max.getValue();
				palet.setTitle("Threash " + threash + " Canny1 " + canny1 + " Canny2 " + canny2);
			}
		});
		palet.add(thr);
		palet.add(min);
		palet.add(max);
		palet.add(exit);
		palet.setVisible(true);
		while (!e) {
			if (cap != null) {
				cap.read(mat);
			} else {
				mat = Imgcodecs.imread(new File(f).getAbsolutePath());
			}
			Mat mato = mat.clone();
			ArrayList<MatOfPoint> c = new ArrayList<MatOfPoint>();
			ArrayList<Mat> split = new ArrayList<Mat>();
			Imgproc.cvtColor(mato, mato, Imgproc.COLOR_BGR2RGB);
			Imgproc.threshold(mato, mato, threash, 255, Imgproc.THRESH_BINARY);
			Core.split(mato, split);
			Mat matg = split.get(1);
			Imgproc.Canny(matg, mato, canny1, canny2);
			Imgproc.findContours(mato, c, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
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
			new ColorFinderTSC(args[0], 0);
		} else {
			new ColorFinderTSC(null, 0);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
