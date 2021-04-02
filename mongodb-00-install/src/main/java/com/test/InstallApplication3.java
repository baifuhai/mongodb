package com.test;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class InstallApplication3 {

	private JFrame frame;

	private JPanel panelContainer;

	private JTextField textFieldIp;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textFieldCount;

	private JButton button;
	private JButton buttonStop;
	private JLabel labelProgressInfo;

	private boolean stop = false;

	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	public void run() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField ip
			panel.add(new JLabel("ip"));
			textFieldIp = new JTextField(15);
			textFieldIp.setText("192.168.1.20");
			panel.add(textFieldIp);

			// TextField port
			panel.add(new JLabel("port"));
			textFieldPort = new JTextField(15);
			textFieldPort.setText("3308");
			panel.add(textFieldPort);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField username
			panel.add(new JLabel("username"));
			textFieldUsername = new JTextField(15);
			textFieldUsername.setText("root");
			panel.add(textFieldUsername);

			// TextField password
			panel.add(new JLabel("password"));
			textFieldPassword = new JTextField(15);
			textFieldPassword.setText("root");
			panel.add(textFieldPassword);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField count
			panel.add(new JLabel("count"));
			textFieldCount = new JTextField(15);
			textFieldCount.setText("1000000");
			panel.add(textFieldCount);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button write
			button = new JButton("测试写入");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						button.setEnabled(false);
						new Thread(() -> {
							try {
								Collection<Callable<Object>> callableList = new ArrayList<>();
								for (int i = 0; i < 3; i++) {
									callableList.add(() -> {
										try {
											xx(ae);
										} catch (Exception e) {
											log.info(e.getMessage(), e);
										}
										return null;
									});
								}
								executorService.invokeAll(callableList);
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								button.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(button);

			// Button stop
			buttonStop = new JButton("停止写入");
			buttonStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						stop = true;
					}
				}
			});
			panel.add(buttonStop);

			// Label progressInfo
			labelProgressInfo = new JLabel("");
			panel.add(labelProgressInfo);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("mysql测试工具");
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void xx(ActionEvent ae) throws Exception {
		String ip = textFieldIp.getText();
		String port = textFieldPort.getText();
		String username = textFieldUsername.getText();
		String password = textFieldPassword.getText();
		Integer count = Integer.parseInt(textFieldCount.getText());

		String url = String.format("jdbc:mysql://%s:%s/?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC", ip, port);

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);

			stmt = conn.createStatement();

			String databaseName = "test_" + UUID.randomUUID().toString().replace("-", "");

			{
				String sql = "create database " + databaseName;
				log.info("{}", sql);
				stmt.execute(sql);
			}

			{
				String sql = "" +
						"CREATE TABLE `" + databaseName + "`.`t_user` (" +
						"  `id` int(11) NOT NULL AUTO_INCREMENT," +
						"  `a` varchar(255) DEFAULT NULL," +
						"  `b` varchar(255) DEFAULT NULL," +
						"  `c` varchar(255) DEFAULT NULL," +
						"  `d` varchar(255) DEFAULT NULL," +
						"  `e` varchar(255) DEFAULT NULL," +
						"  `f` double DEFAULT NULL," +
						"  `g` varchar(255) DEFAULT NULL," +
						"  `h` varchar(255) DEFAULT NULL," +
						"  `i` varchar(255) DEFAULT NULL," +
						"  `j` bit(1) DEFAULT NULL," +
						"  `k` varchar(255) DEFAULT NULL," +
						"  `l` varchar(255) DEFAULT NULL," +
						"  `m` varchar(255) DEFAULT NULL," +
						"  `n` varchar(255) DEFAULT NULL," +
						"  `o` varchar(255) DEFAULT NULL," +
						"  `p` varchar(255) DEFAULT NULL," +
						"  `q` bit(1) DEFAULT NULL," +
						"  `r` varchar(255) DEFAULT NULL," +
						"  PRIMARY KEY (`id`)" +
						")";
				log.info("{}", sql);
				stmt.execute(sql);
			}

			{
				String sql = "" +
						"INSERT INTO `" + databaseName + "`.`t_user` (`a`,`b`,`c`,`d`,`e`,`f`,`g`,`h`,`i`,`j`,`k`,`l`,`m`,`n`,`o`,`p`,`q`,`r`)" +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);

				long begin = System.currentTimeMillis();

				stop = false;
				for (int i = 1; i <= count; i++) {
					pstmt.setString(1, "110000000000000000");
					pstmt.setString(2, "某某某某某某某某某某某某");
					pstmt.setString(3, "某某某某某某某某某某某某");
					pstmt.setString(4, "120000000000000000");
					pstmt.setString(5, "某某某某某某某某某某某某");
					pstmt.setDouble(6, 0.0);
					pstmt.setString(7, "130000000000000000");
					pstmt.setString(8, "某某某某某某某某某某某某");
					pstmt.setString(9, "某某某某某某某某某某某某");
					pstmt.setInt(10, 1);
					pstmt.setString(11, "140000000000000000,150000000000000000");
					pstmt.setString(12, "某某某某某某某某某某某某,某某某某某某某某某某某某");
					pstmt.setString(13, "[true,true]");
					pstmt.setString(14, "160000000000000000,170000000000000000");
					pstmt.setString(15, "某某某某某某某某某某某某,某某某某某某某某某某某某");
					pstmt.setString(16, "[false,false]");
					pstmt.setInt(17, 1);
					pstmt.setString(18, "180000000000000000");
					pstmt.addBatch();

					if (i % 5000 == 0 || i == count) {
						pstmt.executeBatch();
						log.info("pstmt.executeBatch({}/{})", i, count);
					}

					if (i % 100 == 0 || i == count) {
						long end = System.currentTimeMillis();
						double percent = i * 100.0 / count;
						double seconds = (end - begin) / 1000.0;
						labelProgressInfo.setText(String.format("(%d/%d) %.2f%% %.2fs", i, count, percent, seconds));
					}

					if (stop) {
						break;
					}
				}
			}

			{
				String sql = "drop database " + databaseName;
				log.info("{}", sql);
				stmt.execute(sql);
			}

			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
