package com.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplication {

	private JFrame frame;
	private JPanel panelContainer;

	public static void main(String[] args) {
		new MainApplication().run();
	}

	public void run() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button
			JButton button = new JButton("mongodb工具");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					InstallApplication.getInstance(true).show();
				}
			});
			panel.add(button);

			// Button
			JButton button2 = new JButton("mongodb安装工具");
			button2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					InstallApplication2.getInstance(true).show();
				}
			});
			panel.add(button2);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button
			JButton button3 = new JButton("mysql测试工具");
			button3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					MysqlTestApplication.getInstance(true).show();
				}
			});
			panel.add(button3);

			// Button cpuTest
			JButton buttonCpuTest = new JButton("cpu测试工具");
			buttonCpuTest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					CpuTestApplication.getInstance(true).show();
				}
			});
			panel.add(buttonCpuTest);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button
			JButton button4 = new JButton("硬盘文件信息获取工具");
			button4.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					DiskFileInfoApplication.getInstance(true).show();
				}
			});
			panel.add(button4);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("工具集合");
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
