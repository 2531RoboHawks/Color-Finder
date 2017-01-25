package com.chase.color;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main {

	private static JFrame f = new JFrame();

	public static void main(String[] args) {
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(200, 200);
		f.setLayout(new GridLayout(3, 1));
		JButton rgb = new JButton();
		rgb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							new ColorFinderRGB(args[0], 1);
						} else {
							new ColorFinderRGB(null, 1);
						}
					}
				};
				t.start();
			}

		});
		rgb.setText("RGB");
		f.add(rgb);
		JButton hsv = new JButton();
		hsv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							new ColorFinderHSV(args[0], 1);
						} else {
							new ColorFinderHSV(null, 1);
						}
					}
				};
				t.start();
			}

		});
		hsv.setText("HSV");
		f.add(hsv);
		JButton hls = new JButton();
		hls.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							new ColorFinderHLS(args[0], 1);
						} else {
							new ColorFinderHLS(null, 1);
						}

					}
				};
				t.start();
			}

		});
		hls.setText("HLS");
		f.add(hls);
		f.setVisible(true);
	}

}
