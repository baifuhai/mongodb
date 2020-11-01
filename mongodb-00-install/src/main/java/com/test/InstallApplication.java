package com.test;

import com.test.util.MongoUtil;
import com.test.util.MyUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class InstallApplication {

	private JFrame frame;

	private JPanel panelContainer;

	private JFileChooser fileChooser;

	private JTextField textFieldLocalMachineIp;
	private JTextField textFieldBeginShardNumber;
	private JTextField textFieldEndShardNumber;

	private boolean restartRunning;
	private boolean resizeOplogRunning;

	public void run() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField ChosenFilePath
			JTextField textFieldChosenFilePath = new JTextField(26);
			textFieldChosenFilePath.setEditable(false);
			textFieldChosenFilePath.setText("E:/mongodb/mongodb-cluster");
			panel.add(textFieldChosenFilePath);

			// Button OpenFileChooser
			JButton buttonOpenFileChooser = new JButton("选择mongodb-cluster目录");
			buttonOpenFileChooser.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooser.showOpenDialog(panelContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						textFieldChosenFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			panel.add(buttonOpenFileChooser);

			// FileChooser
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setSelectedFile(new File("E:/mongodb/mongodb-cluster"));

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Label
				JLabel label = new JLabel("本机ip");
				panel.add(label);

				// TextField
				textFieldLocalMachineIp = new JTextField(10);
				textFieldLocalMachineIp.setText("192.168.1.59");
				textFieldLocalMachineIp.setEnabled(false);
				panel.add(textFieldLocalMachineIp);
			}

			{
				// Label
				JLabel label = new JLabel("开始分片号");
				panel.add(label);

				// TextField
				textFieldBeginShardNumber = new JTextField(10);
				textFieldBeginShardNumber.setText("11");
				panel.add(textFieldBeginShardNumber);
			}

			{
				// Label
				JLabel label = new JLabel("结束分片号");
				panel.add(label);

				// TextField
				textFieldEndShardNumber = new JTextField(10);
				textFieldEndShardNumber.setText("50");
				panel.add(textFieldEndShardNumber);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button Step2
			JButton buttonStep2 = new JButton("第2步");
			buttonStep2.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							step2(clusterDir);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			buttonStep2.setEnabled(false);
			panel.add(buttonStep2);

			// Button Step6
			JButton buttonStep6 = new JButton("第6步");
			buttonStep6.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String localMachineIp = textFieldLocalMachineIp.getText();
					if (localMachineIp == null || localMachineIp.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入本机ip", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							step6(clusterDir, localMachineIp);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			buttonStep6.setEnabled(false);
			panel.add(buttonStep6);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button Restart
			JLabel label = new JLabel("");
			JButton buttonRestart = new JButton("重启服务");
			buttonRestart.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						if (restartRunning) {
							JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
						} else {
							restartRunning = true;
							buttonRestart.setEnabled(false);
							new Thread(() -> {
								try {
									restartService(label, beginShardNumber, endShardNumber);
									JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) {
									log.error(e.getMessage(), e);
									JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
								} finally {
									restartRunning = false;
									buttonRestart.setEnabled(true);
								}
							}).start();
						}
					}
				}
			});
			panel.add(buttonRestart);

			// Label
			panel.add(label);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button ClusterDirSize
			JButton buttonClusterDirSize = new JButton("获取cluster目录大小");
			buttonClusterDirSize.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					/*int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) */{
						try {
							long size = getClusterDirSize(clusterDir);
							JOptionPane.showMessageDialog(panelContainer, "执行成功，" + MyUtil.getHumanFileSize(size, true), "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonClusterDirSize);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button LocalDirSize
			JButton buttonLocalDirSize = new JButton("获取local目录大小");
			buttonLocalDirSize.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					/*int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) */{
						try {
							long size = getLocalDirSize(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功，" + MyUtil.getHumanFileSize(size, true), "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonLocalDirSize);

			// Button ResizeOplog
			JLabel label = new JLabel("");
			JButton buttonResizeOplog = new JButton("调整操作日志大小");
			buttonResizeOplog.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						if (resizeOplogRunning) {
							JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
						} else {
							resizeOplogRunning = true;
							buttonResizeOplog.setEnabled(false);
							new Thread(() -> {
								try {
									resizeOplog(label, beginShardNumber, endShardNumber);
									JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) {
									log.error(e.getMessage(), e);
									JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
								} finally {
									resizeOplogRunning = false;
									buttonResizeOplog.setEnabled(true);
								}
							}).start();
						}
					}
				}
			});
			panel.add(buttonResizeOplog);

			// Label
			panel.add(label);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button DiagnosticDirSize
			JButton buttonDiagnosticDirSize = new JButton("获取diagnostic目录大小");
			buttonDiagnosticDirSize.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					/*int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) */{
						try {
							long size = getDiagnosticDirSize(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功，" + MyUtil.getHumanFileSize(size, true), "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonDiagnosticDirSize);

			// Button DeleteDiagnosticDir
			JButton buttonDeleteDiagnosticDir = new JButton("删除diagnostic目录");
			buttonDeleteDiagnosticDir.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							deleteDiagnosticDir(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonDeleteDiagnosticDir);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button LogDirSize
			JButton buttonLogDirSize = new JButton("获取log目录大小");
			buttonLogDirSize.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					/*int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) */{
						try {
							long size = getLogDirSize(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功，" + MyUtil.getHumanFileSize(size, true), "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonLogDirSize);

			// Button CompressLogFile
			JButton buttonCompressLogFile = new JButton("压缩日志文件");
			buttonCompressLogFile.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							compressLogFile(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonCompressLogFile);

			// Button LogRotateAndCompressLogFile
			JButton buttonLogRotateAndCompressLogFile = new JButton("分割并压缩日志文件");
			buttonLogRotateAndCompressLogFile.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File clusterDir = fileChooser.getSelectedFile();
					if (clusterDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					String beginShardNumberText = textFieldBeginShardNumber.getText();
					if (beginShardNumberText == null || beginShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入开始分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int beginShardNumber = Integer.valueOf(beginShardNumberText);

					String endShardNumberText = textFieldEndShardNumber.getText();
					if (endShardNumberText == null || endShardNumberText.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入结束分片号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int endShardNumber = Integer.valueOf(endShardNumberText);

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						try {
							logRotateAndCompressLogFile(clusterDir, beginShardNumber, endShardNumber);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			panel.add(buttonLogRotateAndCompressLogFile);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("mongodb工具");
		frame.setSize(600, 350);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void step2(File clusterDir) throws Exception {
		File configCfgFile = new File(clusterDir, "config/mongo.cfg");
		log.info("step2: configCfgFile: {}", configCfgFile.getAbsolutePath());
		_step2(configCfgFile);

		File mongosCfgFile = new File(clusterDir, "mongos/mongo.cfg");
		log.info("step2: mongosCfgFile: {}", mongosCfgFile.getAbsolutePath());
		_step2(mongosCfgFile);

		for (int i = 1; i <= 10; i++) {
			File shardCfgFile = new File(clusterDir, "shard/s" + i + "/mongo.cfg");
			log.info("step2: shardCfgFile: {}", shardCfgFile.getAbsolutePath());
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

	private void step6(File clusterDir, String localMachineIp) throws Exception {
		File mongosCfgFile = new File(clusterDir, "mongos/mongo.cfg");
		log.info("step6: mongosCfgFile: {}", mongosCfgFile.getAbsolutePath());
		_step6(mongosCfgFile, localMachineIp);
	}

	private void _step6(File file, String localMachineIp) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		String dateTime = now.format(dateTimeFormatter);
		String suffix = "." + dateTime + ".javabak";

		MyUtil.copyFileToDir(file, file.getParentFile(), file.getName() + suffix);

		Map<String, Object> map = new HashMap<>();
		map.put("configDB: .+", "configDB: configReplicaSet/" + localMachineIp + ":27009");

		MyUtil.replaceToFileSelfByRegex(file, map);
	}

	private void restartService(JLabel label, int beginShardNumber, int endShardNumber) throws Exception {
		String host = "127.0.0.1";
		int beginShardPort = 27000;

		List<Pair<Integer, String>> pairList = new ArrayList<>();
		pairList.add(new Pair<>(26999, "MongoDBConfig"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			pairList.add(new Pair<>(beginShardPort + i, "MongoDBShard" + i));
		}
		pairList.add(new Pair<>(27000, "MongoDBMongos"));

		for (int i = 0; i < pairList.size(); i++) {
			Pair<Integer, String> pair = pairList.get(i);

			int port = pair.getKey();
			String serviceName = pair.getValue();

			log.info("restartService ({}/{})", i + 1, pairList.size());

			for (int j = 60; j >= 1; j--) {
				label.setText(String.format("(%d/%d) %d %s %ds", i + 1, pairList.size(), port, serviceName, j));
				Thread.sleep(1000);
			}
			label.setText(String.format("(%d/%d) %d %s ...", i + 1, pairList.size(), port, serviceName));

			MongoUtil.restartService(host, port, serviceName);
		}

		label.setText(String.format("(%d/%d) done", pairList.size(), pairList.size()));
	}

	private long getClusterDirSize(File clusterDir) throws Exception {
		BufferedWriter bw2 = null;
		try {
			String ymdhms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
			String fileName = "output_cluster_" + ymdhms + ".txt";

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("logs/" + fileName)));
			bw2 = bw;

			AtomicLong fileLengthSum = new AtomicLong();

			bw.write(clusterDir.getAbsolutePath());
			bw.newLine();

			log.info("getClusterDirSize: {}", clusterDir.getAbsolutePath());

			MyUtil.recurseDir(clusterDir, file -> {
				try {
					if (file.isFile()) {
						bw.write(file.getAbsolutePath() + " " + file.length() + " " + MyUtil.getHumanFileSize(file.length(), false));
						bw.newLine();

						log.info("getClusterDirSize: {} {} {}", file.getAbsolutePath(), file.length(), MyUtil.getHumanFileSize(file.length(), false));

						fileLengthSum.addAndGet(file.length());
					} else {
						bw.write(file.getAbsolutePath());
						bw.newLine();

						log.info("getClusterDirSize: {}", file.getAbsolutePath());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			bw.write(clusterDir.getAbsolutePath() + " " + fileLengthSum.get() + " " + MyUtil.getHumanFileSize(fileLengthSum.get(), false));
			bw.newLine();

			log.info("getClusterDirSize: {} {} {}", clusterDir.getAbsolutePath(), fileLengthSum.get(), MyUtil.getHumanFileSize(fileLengthSum.get(), false));

			return fileLengthSum.get();
		} catch (Exception e) {
			throw e;
		} finally {
			if (bw2 != null) {
				try {
					bw2.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private long getLocalDirSize(File clusterDir, int beginShardNumber, int endShardNumber) throws Exception {
		List<File> dirList = new ArrayList<>();
		dirList.add(new File(clusterDir, "config/data/local"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			dirList.add(new File(clusterDir, "shard/s" + i + "/data/local"));
		}

		return getDirSize(dirList, "local");
	}

	private long getDiagnosticDirSize(File clusterDir, int beginShardNumber, int endShardNumber) throws Exception {
		List<File> dirList = new ArrayList<>();
		dirList.add(new File(clusterDir, "config/data/diagnostic.data"));
		dirList.add(new File(clusterDir, "mongos/log/mongod.diagnostic.data"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			dirList.add(new File(clusterDir, "shard/s" + i + "/data/diagnostic.data"));
		}

		return getDirSize(dirList, "diagnostic");
	}

	private long getLogDirSize(File clusterDir, int beginShardNumber, int endShardNumber) throws Exception {
		List<File> dirList = new ArrayList<>();
		dirList.add(new File(clusterDir, "config/log"));
		dirList.add(new File(clusterDir, "mongos/log"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			dirList.add(new File(clusterDir, "shard/s" + i + "/log"));
		}

		return getDirSize(dirList, "log");
	}

	private long getDirSize(List<File> dirList, String name) throws Exception {
		BufferedWriter bw2 = null;
		try {
			String ymdhms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
			String fileName = "output_" + name + "_" + ymdhms + ".txt";

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("logs/" + fileName)));
			bw2 = bw;

			AtomicLong fileLengthSumAll = new AtomicLong();

			for (File dir : dirList) {
				AtomicLong fileLengthSum = new AtomicLong();

				bw.write(dir.getAbsolutePath());
				bw.newLine();

				log.info("getDirSize: {}", dir.getAbsolutePath());

				MyUtil.recurseDir(dir, file -> {
					try {
						if (file.isFile()) {
							bw.write(file.getAbsolutePath() + " " + file.length() + " " + MyUtil.getHumanFileSize(file.length(), false));
							bw.newLine();

							log.info("getDirSize: {} {} {}", file.getAbsolutePath(), file.length(), MyUtil.getHumanFileSize(file.length(), false));

							fileLengthSum.addAndGet(file.length());
						} else {
							bw.write(file.getAbsolutePath());
							bw.newLine();

							log.info("getDirSize: {}", file.getAbsolutePath());
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});

				bw.write(dir.getAbsolutePath() + " " + fileLengthSum.get() + " " + MyUtil.getHumanFileSize(fileLengthSum.get(), false));
				bw.newLine();
				bw.newLine();

				log.info("getDirSize: {} {} {}", dir.getAbsolutePath(), fileLengthSum.get(), MyUtil.getHumanFileSize(fileLengthSum.get(), false));
				log.info("");

				fileLengthSumAll.addAndGet(fileLengthSum.get());
			}

			bw.write("" + fileLengthSumAll.get() + " " + MyUtil.getHumanFileSize(fileLengthSumAll.get(), false));
			bw.newLine();

			log.info("getDirSize: {} {}", fileLengthSumAll.get(), MyUtil.getHumanFileSize(fileLengthSumAll.get(), false));

			return fileLengthSumAll.get();
		} catch (Exception e) {
			throw e;
		} finally {
			if (bw2 != null) {
				try {
					bw2.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private void resizeOplog(JLabel label, int beginShardNumber, int endShardNumber) throws Exception {
		String host = "127.0.0.1";
		int beginShardPort = 27000;

		List<Integer> portList = new ArrayList<>();
		portList.add(26999);
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			portList.add(beginShardPort + i);
		}

		for (int i = 0; i < portList.size(); i++) {
			int port = portList.get(i);

			log.info("resizeOplog ({}/{})", i + 1, portList.size());

			for (int j = 60; j >= 31; j--) {
				label.setText(String.format("(%d/%d) %d %ds", i + 1, portList.size(), port, j));
				Thread.sleep(1000);
			}
			label.setText(String.format("(%d/%d) %d ...", i + 1, portList.size(), port));

			MongoUtil.replSetResizeOplog(host, port, 990);

			for (int j = 30; j >= 1; j--) {
				label.setText(String.format("(%d/%d) %d %ds", i + 1, portList.size(), port, j));
				Thread.sleep(1000);
			}
			label.setText(String.format("(%d/%d) %d ...", i + 1, portList.size(), port));

			MongoUtil.compact(host, port, "oplog.rs", true);
		}

		label.setText(String.format("(%d/%d) done", portList.size(), portList.size()));
	}

	private void deleteDiagnosticDir(File clusterDir, int beginShardNumber, int endShardNumber) {
		List<File> dirList = new ArrayList<>();
		dirList.add(new File(clusterDir, "config/data/diagnostic.data"));
		dirList.add(new File(clusterDir, "mongos/log/mongod.diagnostic.data"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			dirList.add(new File(clusterDir, "shard/s" + i + "/data/diagnostic.data"));
		}

		for (File dir : dirList) {
			log.info("deleteDiagnosticDir: {}", dir.getAbsolutePath());
			MyUtil.deleteDir(dir);
		}
	}

	private void compressLogFile(File clusterDir, int beginShardNumber, int endShardNumber) {
		MongoUtil.compressLogFile(clusterDir, beginShardNumber, endShardNumber);
	}

	private void logRotateAndCompressLogFile(File clusterDir, int beginShardNumber, int endShardNumber) {
		MongoUtil.logRotateAndCompressLogFile(clusterDir, beginShardNumber, endShardNumber);
	}

}
