package com.test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.test.domain.MongoDatabaseCallback;
import com.test.util.MongoUtil;
import com.test.util.MyUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class InstallApplication {

	private JFrame frame;

	private JPanel panelContainer;

	private JFileChooser fileChooser;

	private JTextField textFieldOldServerIp;
	private JTextField textFieldNewServerIp;

	private JTextField textFieldBeginShardNumber;
	private JTextField textFieldEndShardNumber;

	private JFileChooser fileChooserImportFile;
	private JTextField textFieldImportFile;

	private boolean running;

	private JTextPane textPane;

	public void run() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Path
				String path = "E:/mongodb/mongodb-cluster";

				// FileChooser
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setSelectedFile(new File(path));

				// TextField
				JTextField textField = new JTextField(26);
				textField.setEditable(false);
				textField.setText(path);
				panel.add(textField);

				// Button
				JButton button = new JButton("选择mongodb-cluster目录");
				button.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						int returnVal = fileChooser.showOpenDialog(panelContainer);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
						}
					}
				});
				panel.add(button);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Label
				JLabel label = new JLabel("老服务器ip");
				panel.add(label);

				// TextField
				textFieldOldServerIp = new JTextField(10);
				textFieldOldServerIp.setText("192.168.1.59");
				panel.add(textFieldOldServerIp);
			}

			{
				// Label
				JLabel label = new JLabel("新服务器ip");
				panel.add(label);

				// TextField
				textFieldNewServerIp = new JTextField(10);
				textFieldNewServerIp.setText("192.168.1.20");
				panel.add(textFieldNewServerIp);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

					String localMachineIp = textFieldOldServerIp.getText();
					if (localMachineIp == null || localMachineIp.equals("")) {
						JOptionPane.showMessageDialog(panelContainer, "请输入老服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
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
//			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button
			JButton button = new JButton("重启服务2");
			button.addActionListener(new ActionListener(){
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
						if (running) {
							JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
						} else {
							running = true;
							button.setEnabled(false);
							new Thread(() -> {
								try {
									restartService2(beginShardNumber, endShardNumber);
									JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) {
									log.error(e.getMessage(), e);
									JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
								} finally {
									running = false;
									button.setEnabled(true);
								}
							}).start();
						}
					}
				}
			});
			panel.add(button);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Button
				JButton button = new JButton("停止服务");
				button.addActionListener(new ActionListener(){
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
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								button.setEnabled(false);
								new Thread(() -> {
									try {
										stopService(beginShardNumber, endShardNumber);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										button.setEnabled(true);
									}
								}).start();
							}

						}
					}
				});
				panel.add(button);
			}

			{
				// Button
				JButton button = new JButton("启动服务");
				button.addActionListener(new ActionListener(){
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
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								button.setEnabled(false);
								new Thread(() -> {
									try {
										startService(beginShardNumber, endShardNumber);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										button.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(button);
			}

			{
				// Button
				JButton button = new JButton("重启服务");
				button.addActionListener(new ActionListener(){
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
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								button.setEnabled(false);
								new Thread(() -> {
									try {
										restartService(beginShardNumber, endShardNumber);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										button.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(button);
			}

			{
				// Button
				JButton button = new JButton("停止并删除服务");
				button.addActionListener(new ActionListener(){
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
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								button.setEnabled(false);
								new Thread(() -> {
									try {
										stopAndDeleteService(beginShardNumber, endShardNumber);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										button.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(button);
			}

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
			JLabel labelResizeOplog = new JLabel("");
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
						if (running) {
							JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
						} else {
							running = true;
							buttonResizeOplog.setEnabled(false);
							new Thread(() -> {
								try {
									resizeOplog(labelResizeOplog, beginShardNumber, endShardNumber);
									JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) {
									log.error(e.getMessage(), e);
									JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
								} finally {
									running = false;
									buttonResizeOplog.setEnabled(true);
								}
							}).start();
						}
					}
				}
			});
			panel.add(buttonResizeOplog);

			// Label ResizeOplog
			panel.add(labelResizeOplog);

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

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Path
				String path = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";

				// FileChooser
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setSelectedFile(new File(path));

				// TextField
				JTextField textField = new JTextField(33);
				textField.setEditable(false);
				textField.setText(path);
				panel.add(textField);

				// Button
				JButton button = new JButton("选择导出目录");
				button.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						int returnVal = fileChooser.showOpenDialog(panelContainer);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
						}
					}
				});
				panel.add(button);

				// Button Export
				JButton buttonExport = new JButton("导出");
				buttonExport.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						File exportDir = fileChooser.getSelectedFile();
						if (exportDir == null) {
							JOptionPane.showMessageDialog(panelContainer, "请选择导出目录", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						String oldServerIp = textFieldOldServerIp.getText();
						if (oldServerIp == null || oldServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入老服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonExport.setEnabled(false);
								new Thread(() -> {
									try {
										export(oldServerIp, exportDir);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonExport.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonExport);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Button GetCount
				JButton buttonGetCount = new JButton("获取数量");
				buttonGetCount.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						String oldServerIp = textFieldOldServerIp.getText();
						if (oldServerIp == null || oldServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入老服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonGetCount.setEnabled(false);
								new Thread(() -> {
									try {
										getCount(oldServerIp);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonGetCount.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonGetCount);

				// Button Delete
				JButton buttonDelete = new JButton("删除数据");
				buttonDelete.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						String oldServerIp = textFieldOldServerIp.getText();
						if (oldServerIp == null || oldServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入老服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						String newServerIp = textFieldNewServerIp.getText();
						if (newServerIp == null || newServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入新服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonDelete.setEnabled(false);
								new Thread(() -> {
									try {
										delete(oldServerIp, newServerIp);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonDelete.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonDelete);

				// Button RemoveShard4
				JButton buttonRemoveShard4 = new JButton("删除分片4");
				buttonRemoveShard4.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						String newServerIp = textFieldNewServerIp.getText();
						if (newServerIp == null || newServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入新服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonRemoveShard4.setEnabled(false);
								new Thread(() -> {
									try {
										removeShard(newServerIp, 4);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonRemoveShard4.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonRemoveShard4);

				// Button RemoveShard8
				JButton buttonRemoveShard8 = new JButton("删除分片8");
				buttonRemoveShard8.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						String newServerIp = textFieldNewServerIp.getText();
						if (newServerIp == null || newServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入新服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonRemoveShard8.setEnabled(false);
								new Thread(() -> {
									try {
										removeShard(newServerIp, 8);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonRemoveShard8.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonRemoveShard8);

				// Button Transfer
				JButton buttonTransfer = new JButton("迁移");
				buttonTransfer.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						String oldServerIp = textFieldOldServerIp.getText();
						if (oldServerIp == null || oldServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入老服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						String newServerIp = textFieldNewServerIp.getText();
						if (newServerIp == null || newServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入新服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonTransfer.setEnabled(false);
								new Thread(() -> {
									try {
										transfer(oldServerIp, newServerIp);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonTransfer.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
//				panel.add(buttonTransfer);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			{
				// Path
				String path = null;

				// FileChooser
				fileChooserImportFile = new JFileChooser();
				fileChooserImportFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooserImportFile.setMultiSelectionEnabled(false);
//				fileChooserImportFile.setSelectedFile(new File(path));

				// TextField
				textFieldImportFile = new JTextField(33);
				textFieldImportFile.setEditable(false);
//				textFieldImportFile.setText(path);
				panel.add(textFieldImportFile);

				// Button
				JButton button = new JButton("选择文件");
				button.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						int returnVal = fileChooserImportFile.showOpenDialog(panelContainer);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							textFieldImportFile.setText(fileChooserImportFile.getSelectedFile().getAbsolutePath());
						}
					}
				});
				panel.add(button);

				// Button Import
				JButton buttonImport = new JButton("导入");
				buttonImport.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event) {
						File importFile = fileChooserImportFile.getSelectedFile();
						if (importFile == null) {
							JOptionPane.showMessageDialog(panelContainer, "请选择文件", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						String newServerIp = textFieldNewServerIp.getText();
						if (newServerIp == null || newServerIp.equals("")) {
							JOptionPane.showMessageDialog(panelContainer, "请输入新服务器ip", "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}

						int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (returnVal == JOptionPane.YES_OPTION) {
							if (running) {
								JOptionPane.showMessageDialog(panelContainer, "正在执行", "提示", JOptionPane.INFORMATION_MESSAGE);
							} else {
								running = true;
								buttonImport.setEnabled(false);
								new Thread(() -> {
									try {
										importData(newServerIp, importFile);
										JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
									} finally {
										running = false;
										buttonImport.setEnabled(true);
									}
								}).start();
							}
						}
					}
				});
				panel.add(buttonImport);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// TextPane
			textPane = new JTextPane();
			textPane.setEditable(false);

			// JScrollPane
			JScrollPane scrollPane = new JScrollPane(textPane);
			scrollPane.setPreferredSize(new Dimension(0, 100));

			// add to panelContainer
			panelContainer.add(scrollPane);
		}

		// Frame
		frame = new JFrame("mongodb工具");
		frame.setSize(600, 570);
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

	private void restartService2(int beginShardNumber, int endShardNumber) throws Exception {
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
				textPane.setText(String.format("(%d/%d) %d %s %ds", i + 1, pairList.size(), port, serviceName, j));
				Thread.sleep(1000);
			}

			MongoUtil.stopService(host, port, serviceName);
			textPane.setText(String.format("(%d/%d) %d %s stop ...", i + 1, pairList.size(), port, serviceName));

			MongoUtil.startService(serviceName);
			textPane.setText(String.format("(%d/%d) %d %s start ...", i + 1, pairList.size(), port, serviceName));
		}

		textPane.setText(String.format("(%d/%d) done", pairList.size(), pairList.size()));
	}

	private void stopService(int beginShardNumber, int endShardNumber) throws Exception {
		String host = "127.0.0.1";
		int beginShardPort = 27000;

		List<Pair<Integer, String>> pairList = new ArrayList<>();
		pairList.add(new Pair<>(27000, "MongoDBMongos"));
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			pairList.add(new Pair<>(beginShardPort + i, "MongoDBShard" + i));
		}
		pairList.add(new Pair<>(26999, "MongoDBConfig"));

		for (int i = 0; i < pairList.size(); i++) {
			Pair<Integer, String> pair = pairList.get(i);

			int port = pair.getKey();
			String serviceName = pair.getValue();

			log.info("stopService ({}/{})", i + 1, pairList.size());

			textPane.setText(String.format("(%d/%d) %d %s stop ...", i + 1, pairList.size(), port, serviceName));

			MongoUtil.stopService(host, port, serviceName);
		}

		textPane.setText(String.format("(%d/%d) done", pairList.size(), pairList.size()));
	}

	private void startService(int beginShardNumber, int endShardNumber) throws Exception {
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

			log.info("startService ({}/{})", i + 1, pairList.size());

			textPane.setText(String.format("(%d/%d) %d %s start ...", i + 1, pairList.size(), port, serviceName));

			MongoUtil.startService(serviceName);
		}

		textPane.setText(String.format("(%d/%d) done", pairList.size(), pairList.size()));
	}

	private void restartService(int beginShardNumber, int endShardNumber) throws Exception {
		stopService(beginShardNumber, endShardNumber);
		startService(beginShardNumber, endShardNumber);
	}

	private void stopAndDeleteService(int beginShardNumber, int endShardNumber) throws Exception {
		stopService(beginShardNumber, endShardNumber);

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

			log.info("deleteService ({}/{})", i + 1, pairList.size());

			textPane.setText(String.format("(%d/%d) %d %s delete ...", i + 1, pairList.size(), port, serviceName));

			try {
				MyUtil.deleteService(serviceName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		textPane.setText(String.format("(%d/%d) done", pairList.size(), pairList.size()));
	}

	private long getClusterDirSize(File clusterDir) throws Exception {
		BufferedWriter bw2 = null;
		try {
			String ymdhms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
			String fileName = "output_cluster_" + ymdhms + ".txt";

			bw2 = new BufferedWriter(new FileWriter(new File("logs/" + fileName)));
			BufferedWriter bw = bw2;

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

			bw2 = new BufferedWriter(new FileWriter(new File("logs/" + fileName)));
			BufferedWriter bw = bw2;

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

	private void logRotateAndCompressLogFile(File clusterDir, int beginShardNumber, int endShardNumber) throws Exception {
		MongoUtil.logRotateAndCompressLogFile(clusterDir, beginShardNumber, endShardNumber);
	}

	private void export(String oldServerIp, File exportDir) throws Exception {
		BufferedWriter bw = null;
		try {
			log.info("export begin: {} {}", oldServerIp, exportDir.getAbsolutePath());
			logTextPane(textPane, String.format("export begin: %s %s", oldServerIp, exportDir.getAbsolutePath()));

			String fileName = "result-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + ".txt";

			log.info("fileName: {}", fileName);
			logTextPane(textPane, String.format("fileName: %s", fileName));

			File file = new File(exportDir, fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fileChooserImportFile.setSelectedFile(file);
			textFieldImportFile.setText(file.getAbsolutePath());

			bw = new BufferedWriter(new FileWriter(file));

			long count = 0L;

			count += export(bw, oldServerIp, 27014, 4);
			count += export(bw, oldServerIp, 27018, 8);

			log.info("export end: {}", count);
			logTextPane(textPane, String.format("export end: %d", count));

			JOptionPane.showMessageDialog(panelContainer, "导出了" + count + "条数据，导出的文件不要删除，稍后要再导入", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private long export(BufferedWriter bw, String oldServerIp, int port, int shardNumber) throws Exception {
		log.info("export shard{} begin: {} {}", shardNumber, oldServerIp, port);
		logTextPane(textPane, String.format("export shard%d begin: %s %d", shardNumber, oldServerIp, port));

		long count = MongoUtil.runCallback(oldServerIp, port, "zd_pd_data_middle_platform", new MongoDatabaseCallback<Long>() {
			@Override
			public Long doWithMongoDatabase(MongoDatabase mongoDatabase) throws Exception {
				MongoCollection<Document> collection = mongoDatabase.getCollection("t_peak_sudden_change");

				long countDocuments = collection.countDocuments();

				log.info("export shard{}: {}", shardNumber, countDocuments);
				logTextPane(textPane, String.format("export shard%d: %d", shardNumber, countDocuments));

//				int returnVal = JOptionPane.showConfirmDialog(panelContainer, "数据" + countDocuments + "条，是否导出？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (true/*returnVal == JOptionPane.YES_OPTION*/) {
					bw.write("// shard" + shardNumber + " " + countDocuments);
					bw.newLine();

					AtomicLong currentCount = new AtomicLong();
					MyUtil.getByPage(2000, countDocuments, false, (skip, limit) -> {
						FindIterable<Document> documentFindIterable = collection.find().sort(Sorts.ascending("_id")).skip(skip).limit(limit);
						for (Document document : documentFindIterable) {
							bw.write(document.toJson(JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build()));
							bw.newLine();

							currentCount.incrementAndGet();
						}
						bw.flush();

						log.info("export shard{}: ({}/{})", shardNumber, currentCount.get(), countDocuments);
						logTextPane(textPane, String.format("export shard%d: (%d/%d)", shardNumber, currentCount.get(), countDocuments));

						return true;
					});

					if (currentCount.get() != countDocuments) {
						throw new Exception("导出的数量" + currentCount.get() + "和数据库数量" + countDocuments + "不一致");
					}

					return currentCount.get();
				} else {
					log.info("export shard{} cancel", shardNumber);
					logTextPane(textPane, String.format("export shard%d cancel", shardNumber));

					return 0L;
				}
			}
		});

		log.info("export shard{} end: {}", shardNumber, count);
		logTextPane(textPane, String.format("export shard%d end: %d", shardNumber, count));

		return count;
	}

	private void getCount(String oldServerIp) throws Exception {
		try {
			log.info("getCount begin: {}", oldServerIp);
			logTextPane(textPane, String.format("getCount begin: %s", oldServerIp));

			long count = 0L;

			count += getCount(oldServerIp, 27014, 4);
			count += getCount(oldServerIp, 27018, 8);

			log.info("getCount end: {}", count);
			logTextPane(textPane, String.format("getCount end: %d", count));

			JOptionPane.showMessageDialog(panelContainer, "共" + count + "条数据", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			throw e;
		}
	}

	private long getCount(String oldServerIp, int port, int shardNumber) throws Exception {
		log.info("getCount shard{} begin: {} {}", shardNumber, oldServerIp, port);
		logTextPane(textPane, String.format("getCount shard%d begin: %s %d", shardNumber, oldServerIp, port));

		long count = MongoUtil.runCallback(oldServerIp, port, "zd_pd_data_middle_platform", new MongoDatabaseCallback<Long>() {
			@Override
			public Long doWithMongoDatabase(MongoDatabase mongoDatabase) throws Exception {
				MongoCollection<Document> collection = mongoDatabase.getCollection("t_peak_sudden_change");

				long countDocuments = collection.countDocuments();

				log.info("getCount shard{}: {}", shardNumber, countDocuments);
				logTextPane(textPane, String.format("getCount shard%d: %d", shardNumber, countDocuments));

				return countDocuments;
			}
		});

		log.info("getCount shard{} end: {}", shardNumber, count);
		logTextPane(textPane, String.format("getCount shard%d end: %d", shardNumber, count));

		return count;
	}

	private void delete(String oldServerIp, String newServerIp) throws Exception {
		MongoClient mongoClient = null;
		try {
			log.info("delete begin: {} {}", oldServerIp, newServerIp);

			mongoClient = new MongoClient(newServerIp, 27000);

			MongoDatabase mongoDatabase = mongoClient.getDatabase("zd_pd_data_middle_platform");

			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("t_peak_sudden_change");

			long count = 0L;

			count += delete(mongoCollection, oldServerIp, 27014, 4);
			count += delete(mongoCollection, oldServerIp, 27018, 8);

			log.info("delete end: {}", count);

			JOptionPane.showMessageDialog(panelContainer, "删除了" + count + "条数据", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			throw e;
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}

	private long delete(MongoCollection<Document> mongoCollection, String oldServerIp, int port, int shardNumber) throws Exception {
		log.info("delete shard{} begin: {} {}", shardNumber, oldServerIp, port);
		logTextPane(textPane, String.format("delete shard%d begin: %s %d", shardNumber, oldServerIp, port));

		long count = MongoUtil.runCallback(oldServerIp, port, "zd_pd_data_middle_platform", new MongoDatabaseCallback<Long>() {
			@Override
			public Long doWithMongoDatabase(MongoDatabase mongoDatabase) throws Exception {
				MongoCollection<Document> collection = mongoDatabase.getCollection("t_peak_sudden_change");

				long countDocuments = collection.countDocuments();

				log.info("delete shard{}: {}", shardNumber, countDocuments);
				logTextPane(textPane, String.format("delete shard%d: %d", shardNumber, countDocuments));

				AtomicLong currentCount = new AtomicLong();
				MyUtil.getByPage(2000, countDocuments, true, (skip, limit) -> {
					FindIterable<Document> documentFindIterable = collection.find().sort(Sorts.ascending("_id")).skip(skip).limit(limit);
					for (Document document : documentFindIterable) {
						ObjectId objectId = document.getObjectId("_id");

						DeleteResult deleteResult = mongoCollection.deleteOne(Filters.eq("_id", objectId));
						if (deleteResult.getDeletedCount() == 1) {
							currentCount.incrementAndGet();
						} else {
							throw new Exception("删除失败" + objectId.toString());
						}
					}

					log.info("delete shard{}: ({}/{})", shardNumber, currentCount.get(), countDocuments);
					logTextPane(textPane, String.format("delete shard%d: (%d/%d)", shardNumber, currentCount.get(), countDocuments));

					return true;
				});

				if (currentCount.get() != countDocuments) {
					throw new Exception("删除的数量" + currentCount.get() + "和数据库数量" + countDocuments + "不一致");
				}

				return currentCount.get();
			}
		});

		log.info("delete shard{} end: {}", shardNumber, count);
		logTextPane(textPane, String.format("delete shard%d end: %d", shardNumber, count));

		return count;
	}

	private void removeShard(String newServerIp, int shardNumber) throws Exception {
		Document resultDoc = MongoUtil.removeShard(newServerIp, 27000, "shardReplicaSet" + shardNumber);
		String msg = resultDoc.getString("msg");
		String state = resultDoc.getString("state");
		if (!Objects.equals(state, "completed")) {
			throw new Exception("删除失败：" + msg);
		}
	}

	private void transfer(String oldServerIp, String newServerIp) throws Exception {
		MongoClient mongoClient = null;
		try {
			log.info("transfer begin: {} {}", oldServerIp, newServerIp);
			logTextPane(textPane, String.format("transfer begin: %s %s", oldServerIp, newServerIp));

			mongoClient = new MongoClient(newServerIp, 27000);

			MongoDatabase mongoDatabase = mongoClient.getDatabase("zd_pd_data_middle_platform");

			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("t_peak_sudden_change");

			long count = 0L;

			count += transfer(mongoCollection, oldServerIp, 27014, 4);
			count += transfer(mongoCollection, oldServerIp, 27018, 8);

			log.info("transfer end: {}", count);
			logTextPane(textPane, String.format("transfer end: %d", count));

			JOptionPane.showMessageDialog(panelContainer, "迁移了" + count + "条数据", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			throw e;
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}

	private long transfer(MongoCollection<Document> mongoCollection, String oldServerIp, int port, int shardNumber) throws Exception {
		log.info("transfer shard{} begin: {} {}", shardNumber, oldServerIp, port);
		logTextPane(textPane, String.format("transfer shard%d begin: %s %d", shardNumber, oldServerIp, port));

		long count = MongoUtil.runCallback(oldServerIp, port, "zd_pd_data_middle_platform", new MongoDatabaseCallback<Long>() {
			@Override
			public Long doWithMongoDatabase(MongoDatabase mongoDatabase) throws Exception {
				MongoCollection<Document> collection = mongoDatabase.getCollection("t_peak_sudden_change");

				long countDocuments = collection.countDocuments();

				log.info("transfer shard{}: {}", shardNumber, countDocuments);
				logTextPane(textPane, String.format("transfer shard%d: %d", shardNumber, countDocuments));

				AtomicLong currentCount = new AtomicLong();
				MyUtil.getByPage(2000, countDocuments, true, (skip, limit) -> {
					List<Document> documentList = new ArrayList<>();

					FindIterable<Document> documentFindIterable = collection.find().sort(Sorts.ascending("_id")).skip(skip).limit(limit);
					for (Document document : documentFindIterable) {
						ObjectId objectId = document.getObjectId("_id");

						DeleteResult deleteResult = mongoCollection.deleteOne(Filters.eq("_id", objectId));
						if (deleteResult.getDeletedCount() == 1) {
							documentList.add(document);
							currentCount.incrementAndGet();
						} else {
							throw new Exception("删除失败" + objectId.toString());
						}
					}

					if (documentList.size() > 0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							log.error(e.getMessage(), e);
						}

						for (Document document : documentList) {
							mongoCollection.insertOne(document);
						}
					}

					log.info("transfer shard{}: ({}/{})", shardNumber, currentCount.get(), countDocuments);
					logTextPane(textPane, String.format("transfer shard%d: (%d/%d)", shardNumber, currentCount.get(), countDocuments));

					return true;
				});

				if (currentCount.get() != countDocuments) {
					throw new Exception("迁移的数量" + currentCount.get() + "和数据库数量" + countDocuments + "不一致");
				}

				return currentCount.get();
			}
		});

		log.info("transfer shard{} end: {}", shardNumber, count);
		logTextPane(textPane, String.format("transfer shard%d end: %d", shardNumber, count));

		return count;
	}

	private void importData(String newServerIp, File importFile) throws Exception {
		BufferedReader br = null;
		MongoClient mongoClient = null;
		try {
			log.info("import begin: {} {}", newServerIp, importFile.getAbsolutePath());
			logTextPane(textPane, String.format("import begin: %s %s", newServerIp, importFile.getAbsolutePath()));

			br = new BufferedReader(new FileReader(importFile));

			mongoClient = new MongoClient(newServerIp, 27000);

			MongoDatabase mongoDatabase = mongoClient.getDatabase("zd_pd_data_middle_platform");

			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("t_peak_sudden_change");

			long count = 0L;

			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("") && !line.startsWith("//")) {
					Document document = Document.parse(line);
					mongoCollection.insertOne(document);
					count++;
				}

				if (count % 1000 == 0) {
					log.info("import: {}", count);
					logTextPane(textPane, String.format("import: %d", count));

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}
			}

			log.info("import end: {}", count);
			logTextPane(textPane, String.format("import end: %d", count));

			JOptionPane.showMessageDialog(panelContainer, "导入了" + count + "条数据", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}

	private void logTextPane(JTextPane textPane, String s) {
		String ymdhms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		textPane.setText(ymdhms + ": " + s);
//		textPane.setText(s);
	}

}
