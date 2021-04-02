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
	private JLabel labelProgressInfo;

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
			textFieldCount.setText("100000");
			panel.add(textFieldCount);

			// Button mongodbCluster
			button = new JButton("测试写入");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						button.setEnabled(false);
						new Thread(() -> {
							try {
								xx(ae);
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

			// Label progressInfo
			labelProgressInfo = new JLabel("");
			panel.add(labelProgressInfo);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("mysql测试工具");
		frame.setSize(900, 300);
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

			String databaseName = "test" + System.currentTimeMillis();

			{
				String sql = "create database " + databaseName;
				boolean b = stmt.execute(sql);
			}

			{
				String sql = "CREATE TABLE `" + databaseName + "`.`user` (" +
						"  `id` int(11) NOT NULL AUTO_INCREMENT," +
						"  `name` varchar(255) DEFAULT NULL," +
						"  `age` int(11) DEFAULT NULL," +
						"  `pos` varchar(255) DEFAULT NULL," +
						"  `seq` int(11) DEFAULT NULL," +
						"  PRIMARY KEY (`id`)" +
						")";
				boolean b = stmt.execute(sql);
			}

			{
				String sql = "INSERT INTO `" + databaseName + "`.`user` (`name`, `age`, `pos`, `seq`) VALUES (?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);

				long begin = System.currentTimeMillis();

				for (int i = 1; i <= count; i++) {
					pstmt.setString(1, "a");
					pstmt.setInt(2, 1);
					pstmt.setString(3, "b");
					pstmt.setInt(4, 2);
					pstmt.addBatch();

					if (i % 5000 == 0 || i == count) {
						pstmt.executeBatch();
					}

					if (i % 100 == 0 || i == count) {
						long end = System.currentTimeMillis();
						double percent = i * 100.0 / count;
						double seconds = (end - begin) / 1000.0;
						labelProgressInfo.setText(String.format("(%d/%d) %.2f%% %.2fs", i, count, percent, seconds));
					}
				}
			}

			{
				boolean b = stmt.execute("drop database " + databaseName);
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
