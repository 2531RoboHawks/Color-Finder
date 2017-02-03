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
		f.setLayout(new GridLayout(7, 1));
		JButton rgb = new JButton();
		rgb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderRGB(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderRGB(args[0], 0);
							}
						} else {
							new ColorFinderRGB(null, 0);
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
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderHSV(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderHSV(args[0], 0);
							}
						} else {
							new ColorFinderHSV(null, 0);
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
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderHLS(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderHLS(args[0], 0);
							}
						} else {
							new ColorFinderHLS(null, 0);
						}
					}
				};
				t.start();
			}

		});
		hls.setText("HLS");
		f.add(hls);
		JButton canny = new JButton();
		canny.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderCanny(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderCanny(args[0], 0);
							}
						} else {
							new ColorFinderCanny(null, 0);
						}
					}
				};
				t.start();
			}

		});
		canny.setText("Canny View");
		f.add(canny);
		JButton th = new JButton();
		th.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderThreashold(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderThreashold(args[0], 0);
							}
						} else {
							new ColorFinderThreashold(null, 0);
						}
					}
				};
				t.start();
			}
		});
		th.setText("Threashold View");
		f.add(th);
		JButton ne = new JButton();
		ne.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderTRGB(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderTRGB(args[0], 0);
							}
						} else {
							new ColorFinderTRGB(null, 0);
						}
					}
				};
				t.start();
			}

		});
		ne.setText("Threashold RGB");
		f.add(ne);
		JButton e = new JButton();
		e.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						f.dispose();
						if (args.length > 0) {
							try {
								int n = Integer.parseInt(args[0]);
								new ColorFinderTSC(null, n);
							} catch (NumberFormatException e) {
								new ColorFinderTSC(args[0], 0);
							}
						} else {
							new ColorFinderTSC(null, 0);
						}
					}
				};
				t.start();
			}

		});
		e.setText("Threashold Split Canny");
		f.add(e);
		f.setVisible(true);
	}

}
