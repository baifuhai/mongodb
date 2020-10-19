package com.test;

import com.test.util.MyUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class InstallApplication {

	private JFrame frame;

	private JPanel panelContainer;

	private JTextField textFieldChosenFilePath;
	private JButton buttonOpenFileChooser;

	private JFileChooser fileChooser;
	private File chosenFile;

	private JButton buttonStep2;

	private JButton buttonStep6;

	public static void main(String[] args) {
		new InstallApplication();
	}

	public InstallApplication(){
		init();
	}

	public void init() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField ChosenFilePath
			textFieldChosenFilePath = new JTextField(30);
			textFieldChosenFilePath.setEditable(false);
			panel.add(textFieldChosenFilePath);

			// Button OpenFileChooser
			buttonOpenFileChooser = new JButton("选择mongodb-cluster目录");
			buttonOpenFileChooser.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooser.showOpenDialog(panelContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						chosenFile = fileChooser.getSelectedFile();
						textFieldChosenFilePath.setText(chosenFile.getAbsolutePath());
					}
				}
			});
			panel.add(buttonOpenFileChooser);

			// File Chooser
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button Step2
			buttonStep2 = new JButton("第2步");
			buttonStep2.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chosenFile == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							step2(chosenFile);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e1.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonStep2);

			// Button Step6
			buttonStep6 = new JButton("第6步");
			buttonStep6.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chosenFile == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							step6(chosenFile);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e1.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonStep6);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("mongodb配置文件修改工具");
		frame.setSize(400, 200);
//		frame.setLocation(200, 200);
//		frame.setBounds(400, 100, 800, 700);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void step2(File clusterDir) throws Exception {
		File configCfgFile = new File(clusterDir, "config/mongo.cfg");
		System.out.println("step2: configCfgFile: " + configCfgFile.getAbsolutePath());
		_step2(configCfgFile);

		File mongosCfgFile = new File(clusterDir, "mongos/mongo.cfg");
		System.out.println("step2: mongosCfgFile: " + mongosCfgFile.getAbsolutePath());
		_step2(mongosCfgFile);

		for (int i = 1; i <= 10; i++) {
			File shardCfgFile = new File(clusterDir, "shard/s" + i + "/mongo.cfg");
			System.out.println("step2: shardCfgFile: " + shardCfgFile.getAbsolutePath());
			_step2(shardCfgFile);
		}
	}

	private void _step2(File file) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		String dateTime = now.format(dateTimeFormatter);
		String suffix = "." + dateTime + ".javabak";

		MyUtil.copyFileToDir(file, file.getParentFile(), file.getName() + suffix);

		Map<String, Object> map = new HashMap<>();
		map.put("bindIp: .+", "bindIp: 0.0.0.0");

		MyUtil.replaceToFileSelfByRegex(file, map);
	}

	private void step6(File clusterDir) throws Exception {
		File mongosCfgFile = new File(clusterDir, "mongos/mongo.cfg");
		System.out.println("step6: mongosCfgFile: " + mongosCfgFile.getAbsolutePath());
		_step6(mongosCfgFile);
	}

	private void _step6(File file) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		String dateTime = now.format(dateTimeFormatter);
		String suffix = "." + dateTime + ".javabak";

		MyUtil.copyFileToDir(file, file.getParentFile(), file.getName() + suffix);

		Map<String, Object> map = new HashMap<>();
		map.put("configDB: .+", "configDB: configReplicaSet/老机ip:27009");

		MyUtil.replaceToFileSelfByRegex(file, map);
	}

}
