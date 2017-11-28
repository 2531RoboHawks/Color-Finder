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
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ColorFinderM {

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

	Mat mat, mat1, mat2;

	int t = 25, b = 27;

	boolean e = false;

	BufferedImage img;

	String f;

	public ColorFinderM(String s, int n) {
		if (s == null) {
			cap = new VideoCapture(n);
			if (!cap.isOpened()) {
				System.exit(1);
			}
			mat = new Mat();
			mat1 = new Mat();
			mat2 = new Mat();
		} else if (new File(s).exists()) {
			f = s;
		} else {
			System.exit(1);
		}
		view.setSize(640, 480);
		view.setTitle("Motion Track");
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
		view.setVisible(true);
		palet.setSize(480, 200);
		palet.setLocation(640, 0);
		palet.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		palet.setLayout(new GridLayout(2, 1));
		palet.setTitle("Threash" + t);
		JSlider threash = new JSlider();
		threash.setMaximum(255);
		threash.setMinimum(0);
		threash.setValue(t);
		threash.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				t = threash.getValue();
				palet.setTitle("Threash" + t);
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
		palet.add(threash);
		palet.add(exit);
		palet.setVisible(true);
		while (!e) {
			if (cap != null) {
				cap.read(mat1);
				cap.read(mat2);
				Core.subtract(mat1, mat2, mat);
			} else {
				mat = Imgcodecs.imread(new File(f).getAbsolutePath());
			}
			Mat matp = new Mat();
			Imgproc.cvtColor(mat, matp, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(matp, matp, new Size(b, b), 0);
			ArrayList<MatOfPoint> c = new ArrayList<MatOfPoint>();
			Imgproc.threshold(matp, matp, t, 255, Imgproc.THRESH_BINARY);
			Imgproc.findContours(matp, c, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			for (int i = 0; i < c.size(); i++) {
				MatOfPoint mop = c.get(i);
				Rect rect = Imgproc.boundingRect(mop);
				Imgproc.rectangle(mat1, rect.tl(), rect.br(), new Scalar(0, 255, 0));
			}
			img = matToBufferedImage(mat1, null);
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
			new ColorFinderM(args[0], 0);
		} else {
			new ColorFinderM(null, 0);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
}
